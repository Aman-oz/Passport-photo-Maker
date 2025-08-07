package com.ots.aipassportphotomaker.presentation.ui.createid

data class PhotoIDScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
)

sealed class PhotoIDScreenNavigationState {
    data class PhotoID(val name: String) : PhotoIDScreenNavigationState()
    data class PhotoIDDetails(val name: String) : PhotoIDScreenNavigationState()
}