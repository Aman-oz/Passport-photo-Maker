package com.ots.aipassportphotomaker.presentation.ui.createid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.domain.usecase.photoid.SearchDocuments
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDScreenViewModel.Companion
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDScreenViewModel.Companion.KEY_SEARCH_QUERY
import com.ots.aipassportphotomaker.presentation.ui.mapper.toDocumentListItem
import com.ots.aipassportphotomaker.presentation.ui.usecase.photoid.GetDocumentsWithSeparators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PhotoIDScreen2ViewModel @Inject constructor(
    val networkMonitor: NetworkMonitor,
    private val searchDocuments: SearchDocuments,
    private val savedStateHandle: SavedStateHandle,
    photoIDScreen2Bundle: PhotoIDScreen2Bundle,
    getDocumentsWithSeparators: GetDocumentsWithSeparators,
) : BaseViewModel() {

    val documents: Flow<PagingData<DocumentListItem>> = getDocumentsWithSeparators.documents(
        pageSize = 40
    ).cachedIn(viewModelScope)

    private val _uiState: MutableStateFlow<PhotoIDScreen2UiState> = MutableStateFlow(PhotoIDScreen2UiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<PhotoIDScreen2NavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _refreshListState: MutableSharedFlow<Unit> = singleSharedFlow()
    val refreshListState = _refreshListState.asSharedFlow()

    val imagePath: String? = photoIDScreen2Bundle.imagePath

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    var searchedDocuments: Flow<PagingData<DocumentListItem>> = savedStateHandle.getStateFlow(
        PhotoIDScreenViewModel.KEY_SEARCH_QUERY, "")
        .debounce(if (_uiState.value.showDefaultState) 0 else 500)
        .onEach { query ->
            _uiState.value = if (query.isNotEmpty()) {
                PhotoIDScreen2UiState(showDefaultState = false, showLoading = false)
            } else {
                PhotoIDScreen2UiState(showDefaultState = true, showLoading = false)
            }
        }
        .filter { it.isNotEmpty() }
        .flatMapLatest { query ->
            searchDocuments(query, 30).map { pagingData ->
                pagingData.map { documentEntity -> documentEntity.toDocumentListItem() }
            }
        }.cachedIn(viewModelScope)

    init {
        loadData()
        observeNetworkStatus()
        observeLoadState()
    }

    private fun loadData() {
        launch {
            imagePath?.let {
                Logger.i("PhotoIDScreen2ViewModel", "Received image path: $it")
                _uiState.update { currentState ->
                    currentState.copy(imagePath = it)
                }
                Logger.i("PhotoIDScreen2ViewModel", "UI state updated with image path: ${_uiState.value.imagePath}")
            }
        }
    }

    private fun observeNetworkStatus() {
        networkMonitor.networkState
            .onEach { if (it.shouldRefresh) onRefresh() }
            .launchIn(viewModelScope)
    }

    private fun observeLoadState() {
        launch {
            documents.collect {
                Logger.i("PhotoIDScreen2ViewModel", "Documents data received. imagePath: $imagePath")
                _uiState.update { currentState ->
                    currentState.copy(
                        showLoading = false,
                        imagePath = imagePath ?: ""
                    )
                }
            }
        }
    }

    fun onDocumentClicked(documentId: Int) {
        val imagePathToUse = _uiState.value.imagePath ?: imagePath
        Logger.i("PhotoIDScreen2ViewModel", "Document clicked with ID: $documentId and imagePath: $imagePathToUse")
        _navigationState.tryEmit(PhotoIDScreen2NavigationState.DocumentInfoScreen(
            documentId = documentId, imagePath = imagePathToUse!!
        ))
    }

    fun onSeeAllClicked(type: String){

        val imagePathToUse = _uiState.value.imagePath ?: imagePath
        Logger.i("PhotoIDScreen2ViewModel", "See All clicked with type: $type and imagePath: $imagePathToUse")
        _navigationState.tryEmit(PhotoIDScreen2NavigationState.PhotoIDDetails(
            type = type,
            imagePath = imagePathToUse!!
        ))
    }

    fun resetImagePath() {
        _uiState.update { currentState ->
            currentState.copy(imagePath = "")
        }
    }

    fun resetAllData() {
        _uiState.update { currentState ->
            currentState.copy(
                imagePath = "",
                showDefaultState = true,
                showLoading = false,
                errorMessage = null
            )
        }
    }

    fun onSearch(query: String) {
        savedStateHandle[KEY_SEARCH_QUERY] = query
    }

    fun onLoadStateUpdate(loadState: CombinedLoadStates) {
        val showLoading = loadState.refresh is LoadState.Loading

        val error = when (val refresh = loadState.refresh) {
            is LoadState.Error -> refresh.error.message
            else -> null
        }

        Logger.i("PhotoIDScreen2ViewModel", "LoadState updated. imagePath: ${_uiState.value.imagePath}, showLoading: $showLoading, error: $error")
        _uiState.update { it.copy(showLoading = showLoading, errorMessage = error) }
        Logger.i("PhotoIDScreen2ViewModel", "UI state after LoadState update: ${_uiState.value.imagePath}")
    }
    fun onRefresh() = launch {
        _refreshListState.emit(Unit)
    }

    companion object {
        const val KEY_SEARCH_QUERY = "search_query"
    }


}