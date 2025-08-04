package com.ots.aipassportphotomaker.presentation.ui.home

data class HomeScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
)

sealed class HomeScreenNavigationState {
    data class PhotoID(val name: String) : HomeScreenNavigationState()
}