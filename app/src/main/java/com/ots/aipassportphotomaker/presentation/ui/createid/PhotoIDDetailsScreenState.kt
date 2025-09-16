package com.ots.aipassportphotomaker.presentation.ui.createid

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import javax.inject.Inject
import kotlin.text.get

data class PhotoIDDetailScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
    val showDefaultState: Boolean = true,
    val showNoDocumentsFound: Boolean = false,
    val type: String = "",
    val imagePath: String? = null,
)

sealed class PhotoIDDetailScreenNavigationState {
    data class SelectPhotoScreen(val documentId: Int) : PhotoIDDetailScreenNavigationState()

    data class DocumentInfoScreen(
        val documentId: Int,
        val imagePath: String?
    ) : PhotoIDDetailScreenNavigationState()
}

class PhotoIDDetailBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

    // Extract the page object
    val route: Page.PhotoIDDetailScreen = savedStateHandle.toRoute()

    val type: String = route.type
    val imagePath: String? = route.imagePath
}