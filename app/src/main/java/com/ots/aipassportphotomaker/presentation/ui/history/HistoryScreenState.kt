package com.ots.aipassportphotomaker.presentation.ui.history

data class HistoryScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedType: String = "All",

    val documentId: Int = 0,
    val documentName: String = "",
    val documentSize: String = "",
    val documentUnit: String = "",
    val documentPixels: String = "",
    val documentResolution: String = "",
    val documentImage: String? = null,
    val documentType: String = "",
    val createdImage: String = "", // Path to the final saved image
)

sealed class HistoryScreenNavigationState {
    data class DocumentInfoScreen(
        val documentId: Int,
        val imagePath: String? = null,
    ) : HistoryScreenNavigationState()
}