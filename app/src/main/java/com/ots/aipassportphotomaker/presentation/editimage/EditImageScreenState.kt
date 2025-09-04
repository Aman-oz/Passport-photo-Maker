package com.ots.aipassportphotomaker.presentation.editimage

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
    val imageUrl: String? = null

)

class EditImageScreenBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {
    val documentId: Int = savedStateHandle.toRoute<Page.EditImageScreen>().documentId
    val imageUrl: String? = savedStateHandle.toRoute<Page.EditImageScreen>().imageUrl
    val selectedColor: String? = savedStateHandle.toRoute<Page.EditImageScreen>().selectedBackgroundColor
}

sealed class EditImageScreenNavigationState {
    data class CutOutScreen(
        val documentId: Int,
        val imageUrl: String? = null,
        val selectedBackgroundColor: Color? = null
    ) : EditImageScreenNavigationState()

}