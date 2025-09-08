package com.ots.aipassportphotomaker.presentation.ui.documentinfo

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import javax.inject.Inject

data class DocumentInfoScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
    val documentId: Int = 0,
    val documentName: String = "",
    val documentSize: String = "",
    val documentUnit: String = "",
    val documentPixels: String = "",
    val documentResolution: String = "",
    val documentImage: String? = null,
    val documentType: String = "",
    val documentCompleted: String? = null,
    val backgroundOption: BackgroundOption = BackgroundOption.KEEP_ORIGINAL

)

enum class BackgroundOption {
    KEEP_ORIGINAL,
    CHANGE_BACKGROUND
}

class DocumentDetailsBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {
    val documentId: Int = savedStateHandle.toRoute<Page.DocumentInfoScreen>().documentId
}

sealed class DocumentInfoScreenNavigationState {
    data class SelectPhotoScreen(val documentId: Int, val type: String?) : DocumentInfoScreenNavigationState()
    data class TakePhotoScreen(val documentId: Int) : DocumentInfoScreenNavigationState()
    data class ProcessingScreen(
        val documentId: Int,
        val imagePath: String? = null,// content uri path
        val filePath: String? = null,// local file path /storage
        val selectedDpi: String = "300",
        val selectedBackgroundColor: Color? = null
    ) : DocumentInfoScreenNavigationState()

}
