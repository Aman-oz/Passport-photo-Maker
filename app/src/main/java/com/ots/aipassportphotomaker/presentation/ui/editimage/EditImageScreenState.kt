package com.ots.aipassportphotomaker.presentation.ui.editimage

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import javax.inject.Inject

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
data class EditImageScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
    val showNoSuitsFound: Boolean = false,
    val documentId: Int = 0,
    val documentName: String = "",
    val documentSize: String = "",
    val documentUnit: String = "",
    val documentPixels: String = "",
    val documentResolution: String = "",
    val documentImage: String? = null,
    val documentType: String = "",
    val documentCompleted: String? = null,
    val selectedColor: Color = Color.Unspecified,
    val imageUrl: String? = null,
    val imagePath: String? = null,
    val ratio: Float = 1f,
    val editPosition: Int = 0,
    val sourceScreen: String = "",

)

class EditImageScreenBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {
    val documentId: Int = savedStateHandle.toRoute<Page.EditImageScreen>().documentId
    val imageUrl: String? = savedStateHandle.toRoute<Page.EditImageScreen>().imageUrl
    val selectedColor: String? = savedStateHandle.toRoute<Page.EditImageScreen>().selectedBackgroundColor
    val sourceScreen: String = savedStateHandle.toRoute<Page.EditImageScreen>().sourceScreen
    val editPosition: Int = savedStateHandle.toRoute<Page.EditImageScreen>().editPosition
    val selectedDpi: String = savedStateHandle.toRoute<Page.EditImageScreen>().selectedDpi
}

sealed class EditImageScreenNavigationState {
    data class CutOutScreen(
        val documentId: Int,
        val imageUrl: String? = null,
        val selectedBackgroundColor: Color? = null,
        val sourceScreen: String = ""
    ) : EditImageScreenNavigationState()

    data class SavedImageScreen(
        val documentId: Int,
        val imagePath: String? = null,
        val selectedDpi: String
    ) : EditImageScreenNavigationState()

}