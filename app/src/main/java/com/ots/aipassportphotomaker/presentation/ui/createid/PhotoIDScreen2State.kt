package com.ots.aipassportphotomaker.presentation.ui.createid

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import javax.inject.Inject

data class PhotoIDScreen2UiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
    val showDefaultState: Boolean = true,
    val showNoDocumentsFound: Boolean = false,
    val imagePath: String = "",
)

class PhotoIDScreen2Bundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {
    val imagePath: String = savedStateHandle.toRoute<Page.PhotoID2>().imagePath ?: ""
}

sealed class PhotoIDScreen2NavigationState {

    data class PhotoIDDetails(
        val type: String,
        val imagePath: String = ""
    ) : PhotoIDScreen2NavigationState()

    data class DocumentInfoScreen(
        val documentId: Int,
        val imagePath: String = ""
    ) : PhotoIDScreen2NavigationState()

    data class SelectPhotoScreen(val documentId: Int) : PhotoIDScreen2NavigationState()

}