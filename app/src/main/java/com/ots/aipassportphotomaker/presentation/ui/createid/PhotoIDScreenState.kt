package com.ots.aipassportphotomaker.presentation.ui.createid

data class PhotoIDScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
)

sealed class PhotoIDScreenNavigationState {
    data class PhotoIDDetails(val documentId: Int) : PhotoIDScreenNavigationState()
}