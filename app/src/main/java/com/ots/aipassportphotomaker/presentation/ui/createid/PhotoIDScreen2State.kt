package com.ots.aipassportphotomaker.presentation.ui.createid

data class PhotoIDScreen2UiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
)

sealed class PhotoIDScreen2NavigationState {
    data class PhotoIDDetails(val type: String) : PhotoIDScreen2NavigationState()
    data class SelectPhotoScreen(val documentId: Int) : PhotoIDScreen2NavigationState()

}