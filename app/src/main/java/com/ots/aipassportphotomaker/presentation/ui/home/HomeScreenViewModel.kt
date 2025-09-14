package com.ots.aipassportphotomaker.presentation.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.image_picker.model.AssetInfo
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDScreenNavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel  @Inject constructor(
    val networkMonitor: NetworkMonitor,
) : BaseViewModel() {

    private val _uiState: MutableStateFlow<HomeScreenUiState> = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<HomeScreenNavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _refreshListState: MutableSharedFlow<Unit> = singleSharedFlow()
    val refreshListState = _refreshListState.asSharedFlow()

    val selectedImagesList = mutableStateListOf<AssetInfo>()
    val selectedImage: String? = null

    init {
        observeNetworkStatus()
        loadData()
    }

    fun updateImagePath(imagePath: String) {

        _uiState.value = _uiState.value.copy(imagePath = imagePath)
//        _uiState.update { it.copy(imagePath = imagePath) }
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

        when(name) {
            "PhotoID" -> _navigationState.tryEmit(HomeScreenNavigationState.PhotoID(name))
            "Cutout" -> {
                _navigationState.tryEmit(
                    HomeScreenNavigationState.CutOutScreen(
                        imagePath = selectedImage,
                        sourceScreen = "HomeScreen"
                    )
                )
            }
            "ChangeBG" -> {
                _uiState.value = _uiState.value.copy(editPosition = 0)
                _navigationState.tryEmit(
                    HomeScreenNavigationState.EditImageScreen(
                        documentId = 0,
                        imageUrl = selectedImage,
                        selectedBackgroundColor = Color.Unspecified,
                        editPosition = 0,
                        sourceScreen = "HomeScreen"
                    )
                )
            }
            "AddSuits" -> {
                _uiState.value = _uiState.value.copy(editPosition = 1)
                _navigationState.tryEmit(
                    HomeScreenNavigationState.EditImageScreen(
                        documentId = 0,
                        imageUrl = selectedImage,
                        selectedBackgroundColor = Color.Unspecified,
                        editPosition = 1,
                        sourceScreen = "HomeScreen"
                    )
                )
            }



            "SocialProfile" -> {
                _navigationState.tryEmit(HomeScreenNavigationState.PhotoIDDetails("Profile"))
            }
        }
    }

    private fun observeNetworkStatus() {
        networkMonitor.networkState
            .onEach { if (it.shouldRefresh) onRefresh() }
            .launchIn(viewModelScope)
    }
    fun onRefresh() = launch {
        _refreshListState.emit(Unit)
    }

}