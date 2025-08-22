package com.ots.aipassportphotomaker.presentation.ui.createid

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import javax.inject.Inject
import kotlin.text.get

data class PhotoIDDetailScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
    val type: String = "",
)

sealed class PhotoIDDetailScreenNavigationState {
    data class SelectPhotoScreen(val documentId: Int) : PhotoIDDetailScreenNavigationState()
}

class PhotoIDDetailBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

    // Extract the page object
    val route: Page.PhotoIDDetailScreen = savedStateHandle.toRoute()

    // Extract the type directly from the route object
    val type: String = route.type
//    val type: String = savedStateHandle.toRoute<Page.PhotoIDDetailScreen>().type
}