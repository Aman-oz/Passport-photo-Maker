package com.ots.aipassportphotomaker.presentation.ui.history

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.dbmodels.CreatedImageEntity
import com.ots.aipassportphotomaker.domain.repository.DocumentRepository
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.domain.util.onError
import com.ots.aipassportphotomaker.domain.util.onSuccess
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import com.ots.aipassportphotomaker.presentation.ui.home.HomeScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.home.HomeScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryScreenViewModel @Inject constructor(
    val networkMonitor: NetworkMonitor,
    private val documentRepository: DocumentRepository
) : BaseViewModel() {

    private val _uiState: MutableStateFlow<HistoryScreenUiState> = MutableStateFlow(
        HistoryScreenUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<HistoryScreenNavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _refreshListState: MutableSharedFlow<Unit> = singleSharedFlow()
    val refreshListState = _refreshListState.asSharedFlow()

    private val _documents: MutableStateFlow<List<CreatedImageEntity>> = MutableStateFlow(emptyList()) // Updated to CreatedImageEntity
    val documents = _documents.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog = _showDeleteDialog.asStateFlow()

    private val _selectedImageId = MutableStateFlow<Int?>(null)
    val selectedImageId = _selectedImageId.asStateFlow()

    private var currentType: String = "All"

    init {
        observeNetworkStatus()
        loadData()
        getHistoryByType("All")
        observeRefreshEvents()
    }
    private fun loadData() {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = false, errorMessage = null)
        }
    }
    fun onLoadStateUpdate(loadState: CombinedLoadStates) {
        val showLoading = loadState.refresh is LoadState.Loading

        val error = when (val refresh = loadState.refresh) {
            is LoadState.Error -> refresh.error.message
            else -> null
        }

        _uiState.update { it.copy(showLoading = showLoading, errorMessage = error) }
    }

    fun onItemClick(name: String) {

        _navigationState.tryEmit(HistoryScreenNavigationState.DocumentInfoScreen(
            documentId = _documents.value.firstOrNull { it.name == name }?.id ?: 0,
            imagePath = _documents.value.firstOrNull { it.name == name }?.createdImage
        ))
    }

    private fun observeNetworkStatus() {
        networkMonitor.networkState
            .onEach { if (it.shouldRefresh) onRefresh() }
            .launchIn(viewModelScope)
    }
    fun onRefresh() = launch {
        _refreshListState.emit(Unit)
    }

    fun getHistoryByType(type: String) {
        currentType = type
        viewModelScope.launch {
            _uiState.update { it.copy(showLoading = true) }
            val result = if (type == "All") {
                Logger.i("HistoryScreenViewModel", "Loading all created images")
                documentRepository.getAllCreatedImages()
            } else {
                Logger.i("HistoryScreenViewModel", "Loading created images for type: $type")
                documentRepository.getCreatedImagesByType(type)
            }
            result.onSuccess { entities ->
                _documents.value = entities
                _uiState.update { it.copy(showLoading = false, errorMessage = null) }
                Logger.i("HistoryScreenViewModel", "Loaded ${entities.size} images for type: $type")
            }.onError { error ->
                _uiState.update { it.copy(showLoading = false, errorMessage = error.message) }
                _documents.value = emptyList()
                Logger.e("HistoryScreenViewModel", "Error loading history: ${error.message}")
            }
        }
    }

    fun onLongClickItem(id: Int) {
        _selectedImageId.value = id
        _showDeleteDialog.value = true
    }

    fun hideDeleteDialog() {
        _showDeleteDialog.value = false
        _selectedImageId.value = null
    }

    fun deleteSelectedImage() {
        viewModelScope.launch {
            _selectedImageId.value?.let { id ->
                documentRepository.deleteCreatedImage(id)
                getHistoryByType(currentType) // Refresh data
            }
            hideDeleteDialog()
        }
    }

    fun deleteAllImages() {
        viewModelScope.launch {
            documentRepository.deleteAllCreatedImages()
            getHistoryByType(currentType) // Refresh data
        }
    }

    private fun observeRefreshEvents() {
        viewModelScope.launch {
            documentRepository.getRefreshEvent().collect {
                getHistoryByType(currentType) // Refresh with the current tab type
            }
        }
    }

}