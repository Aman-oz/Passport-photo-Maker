package com.ots.aipassportphotomaker.presentation.ui.createid

data class PhotoIDScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
    val showNoDocumentsFound: Boolean = false,
)

sealed class PhotoIDScreenNavigationState {
    data class PhotoIDDetails(val type: String) : PhotoIDScreenNavigationState()
    data class SelectPhotoScreen(val documentId: Int) : PhotoIDScreenNavigationState()

}