package com.ots.aipassportphotomaker.presentation.ui.processimage

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import javax.inject.Inject

// Created by amanullah on 27/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

data class ImageProcessingScreenUiState(
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
    val documentCompleted: String? = null

)


class ImageProcessingBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {
    val documentId: Int = savedStateHandle.toRoute<Page.ImageProcessingScreen>().documentId
    val imagePath: String? = savedStateHandle.toRoute<Page.ImageProcessingScreen>().imagePath
    val selectedDpi: String = savedStateHandle.toRoute<Page.ImageProcessingScreen>().selectedDpi
    val selectedColor: String? = savedStateHandle.toRoute<Page.ImageProcessingScreen>().selectedBackgroundColor
}

sealed class ImageProcessingScreenNavigationState {
    data class EditImageScreen(val documentId: Int, val type: String?) : ImageProcessingScreenNavigationState()

}