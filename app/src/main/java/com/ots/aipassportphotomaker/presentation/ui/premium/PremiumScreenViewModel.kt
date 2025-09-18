package com.ots.aipassportphotomaker.presentation.ui.premium

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class PremiumScreenViewModel @Inject constructor(
    premiumScreenBundle: PremiumScreenBundle,
) : BaseViewModel() {

    private val _uiState: MutableStateFlow<PremiumScreenUiState> = MutableStateFlow(
        PremiumScreenUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<PremiumScreenNavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    init {

        onInitialState()
        loadState(false)
    }

    private fun onInitialState() = launch {

    }

    private fun loadState(isLoading: Boolean) {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = isLoading, errorMessage = null)
        }
    }

    fun onOpenCameraClicked() {
//        _navigationState.tryEmit(PhotoIDScreenNavigationState.TakePhotoScreen(documentId))
    }

}