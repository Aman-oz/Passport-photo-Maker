package com.ots.aipassportphotomaker.presentation.ui.permission

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
class PermissionScreenViewModel @Inject constructor(
    permissionScreenBundle: PermissionScreenBundle,
) : BaseViewModel() {

    private val _uiState: MutableStateFlow<PermissionScreenUiState> = MutableStateFlow(
        PermissionScreenUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<PermissionScreenNavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    //*********************Permission***************//
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun  dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    //********************************************//

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