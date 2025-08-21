package com.ots.aipassportphotomaker.presentation.ui.createid

data class PhotoIDDetailScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
    val title: String = "",
)

sealed class PhotoIDDetailScreenNavigationState {
    data class SelectPhotoScreen(val documentId: Int) : PhotoIDDetailScreenNavigationState()
}