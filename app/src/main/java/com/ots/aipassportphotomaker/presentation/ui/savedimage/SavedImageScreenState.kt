package com.ots.aipassportphotomaker.presentation.ui.savedimage

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import javax.inject.Inject

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
data class SavedImageScreenUiState(
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
    val imagePath: String? = null,
    val ratio: Float = 1f,

)

class SavedImageScreenBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {
    val documentId: Int = savedStateHandle.toRoute<Page.SavedImageScreen>().documentId
    val imagePath: String? = savedStateHandle.toRoute<Page.SavedImageScreen>().imagePath
}

sealed class SavedImageScreenNavigationState {
    data class DocumentInfoScreen(
        val documentId: Int
    ) : SavedImageScreenNavigationState()

}