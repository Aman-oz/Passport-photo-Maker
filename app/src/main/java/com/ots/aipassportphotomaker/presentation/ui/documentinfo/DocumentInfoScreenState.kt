package com.ots.aipassportphotomaker.presentation.ui.documentinfo

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.model.CustomDocumentData
import javax.inject.Inject
import kotlin.text.get

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
    val imagePath: String? = savedStateHandle.toRoute<Page.DocumentInfoScreen>().imagePath

    val documentName: String? = savedStateHandle.toRoute<Page.DocumentInfoScreen>().documentName
    val documentSize: String? = savedStateHandle.toRoute<Page.DocumentInfoScreen>().documentSize
    val documentUnit: String? = savedStateHandle.toRoute<Page.DocumentInfoScreen>().documentUnit
    val documentPixels: String? = savedStateHandle.toRoute<Page.DocumentInfoScreen>().documentPixels
    val documentResolution: String? = savedStateHandle.toRoute<Page.DocumentInfoScreen>().documentResolution
    val documentImage: String? = savedStateHandle.toRoute<Page.DocumentInfoScreen>().documentImage
    val documentType: String? = savedStateHandle.toRoute<Page.DocumentInfoScreen>().documentType
    val documentCompleted: String? = savedStateHandle.toRoute<Page.DocumentInfoScreen>().documentCompleted
}

sealed class DocumentInfoScreenNavigationState {
    data class SelectPhotoScreen(val documentId: Int, val type: String?) : DocumentInfoScreenNavigationState()
    data class TakePhotoScreen(val documentId: Int) : DocumentInfoScreenNavigationState()
    data class ProcessingScreen(
        val documentId: Int,
        val imagePath: String? = null,// content uri path
        val filePath: String? = null,// local file path /storage
        val documentName: String,
        val documentSize: String,
        val documentUnit: String,
        val documentPixels: String,
        val selectedDpi: String = "300",
        val selectedBackgroundColor: Color? = null,
        val sourceScreen: String
    ) : DocumentInfoScreenNavigationState()

}
