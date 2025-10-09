package com.ots.aipassportphotomaker.presentation.ui.languages

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import javax.inject.Inject

// Created by amanullah on 09/10/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

data class LanguagesScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,

)


class LanguagesScreenBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {
    val sourceScreen: String = savedStateHandle.toRoute<Page.LanguagesScreen>().sourceScreen
}

sealed class LanguagesScreenNavigationState {

    data class OnboardingScreen(
        val sourceScreen: String
    ) : LanguagesScreenNavigationState()

    data class HomeScreen(
        val sourceScreen: String
    ) : LanguagesScreenNavigationState()

}