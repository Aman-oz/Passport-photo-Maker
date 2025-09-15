package com.ots.aipassportphotomaker.presentation.ui.home

import androidx.compose.ui.graphics.Color
import com.ots.aipassportphotomaker.domain.model.CustomDocumentData
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.editimage.EditImageScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.processimage.ImageProcessingScreenNavigationState

data class HomeScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
    val imagePath: String? = null,
    val editPosition: Int = 0,
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
        val editPosition: Int = 0,
        val sourceScreen: String
    ) : HomeScreenNavigationState()


    data class PhotoIDDetails(val type: String) : HomeScreenNavigationState()
// Todo: implement when needed
//    data class DocumentInfoScreen(val customData: CustomDocumentData) : HomeScreenNavigationState()
}