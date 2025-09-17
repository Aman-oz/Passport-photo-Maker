package com.ots.aipassportphotomaker.presentation.ui.onboarding

import androidx.lifecycle.SavedStateHandle
import javax.inject.Inject

data class OnboardingScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,

)

class OnboardingScreenBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

}

sealed class OnboardingScreenNavigationState {
    data class HomeScreen(val value: Int) : OnboardingScreenNavigationState()

}
