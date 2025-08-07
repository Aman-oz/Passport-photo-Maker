package com.ots.aipassportphotomaker.presentation.ui.history

data class HistoryScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
)

sealed class HistoryScreenNavigationState {
    data class PhotoID(val name: String) : HistoryScreenNavigationState()
    data class PhotoIDDetails(val name: String) : HistoryScreenNavigationState()
}