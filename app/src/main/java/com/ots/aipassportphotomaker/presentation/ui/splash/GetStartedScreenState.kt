package com.ots.aipassportphotomaker.presentation.ui.splash

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.model.CustomDocumentData
import javax.inject.Inject
import kotlin.text.get

data class GetStartedScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,

)

class GetStartedScreenBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

}

sealed class GetStartedScreenNavigationState {
    data class OnboardingScreen(val type: String?) : GetStartedScreenNavigationState()
    data class HomeScreen(val value: Int) : GetStartedScreenNavigationState()

}
