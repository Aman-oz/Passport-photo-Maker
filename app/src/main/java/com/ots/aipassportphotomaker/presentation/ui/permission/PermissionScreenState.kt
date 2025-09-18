package com.ots.aipassportphotomaker.presentation.ui.permission

import androidx.lifecycle.SavedStateHandle
import javax.inject.Inject

data class PermissionScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,

)

class PermissionScreenBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

}

sealed class PermissionScreenNavigationState {
    data class HomeScreen(val value: Int) : PermissionScreenNavigationState()

}
