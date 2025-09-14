package com.ots.aipassportphotomaker.presentation.ui.home

import androidx.compose.ui.graphics.Color
import com.ots.aipassportphotomaker.presentation.ui.editimage.EditImageScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.processimage.ImageProcessingScreenNavigationState

data class HomeScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
    val imagePath: String? = null,
)

sealed class HomeScreenNavigationState {
    data class PhotoID(val item: String) : HomeScreenNavigationState()

    data class CutOutScreen(
        val imagePath: String? = null,
        val sourceScreen: String
    ) : HomeScreenNavigationState()

    data class EditImageScreen(
        val documentId: Int,
        val imageUrl: String? = null,
        val selectedBackgroundColor: Color? = null,
        val sourceScreen: String
    ) : HomeScreenNavigationState()
}