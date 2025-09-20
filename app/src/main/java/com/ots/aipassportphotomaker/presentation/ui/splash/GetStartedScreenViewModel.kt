package com.ots.aipassportphotomaker.presentation.ui.splash

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.ots.aipassportphotomaker.adsmanager.admob.AdsManager
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.CustomDocumentData
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.repository.ColorFactory
import com.ots.aipassportphotomaker.domain.usecase.photoid.GetDocumentDetails
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.ots.aipassportphotomaker.domain.util.Result
import com.ots.aipassportphotomaker.domain.util.onSuccess
import com.ots.aipassportphotomaker.image_picker.model.AssetInfo
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.BackgroundOption
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentDetailsBundle
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentInfoScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentInfoScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class GetStartedScreenViewModel @Inject constructor(
    getStartedScreenBundle: GetStartedScreenBundle,
    private val adsManager: AdsManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel() {

    private val _uiState: MutableStateFlow<GetStartedScreenUiState> = MutableStateFlow(
        GetStartedScreenUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<GetStartedScreenNavigationState> = singleSharedFlow()
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