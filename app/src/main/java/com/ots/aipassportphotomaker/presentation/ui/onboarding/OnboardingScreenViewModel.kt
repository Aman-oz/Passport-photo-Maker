package com.ots.aipassportphotomaker.presentation.ui.onboarding

import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.managers.PreferencesHelper
import com.ots.aipassportphotomaker.common.utils.AdsConstants
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class OnboardingScreenViewModel @Inject constructor(
    getStartedScreenBundle: OnboardingScreenBundle,
    private val preferencesHelper: PreferencesHelper,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel() {

    private val _uiState: MutableStateFlow<OnboardingScreenUiState> = MutableStateFlow(
        OnboardingScreenUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<OnboardingScreenNavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    init {

        onInitialState()
        loadState(false)
    }

    private fun onInitialState() = launch {
        analyticsManager.sendAnalytics(AnalyticsConstants.OPENED, "OnboardingScreen")
    }

    private fun loadState(isLoading: Boolean) {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = isLoading, errorMessage = null)
        }
    }

    fun onOpenCameraClicked() {
//        _navigationState.tryEmit(PhotoIDScreenNavigationState.TakePhotoScreen(documentId))
    }

    fun isPremiumUser(): Boolean {
        return preferencesHelper.getBoolean(AdsConstants.IS_NO_ADS_ENABLED, false)
    }

    fun sendEvent(eventName: String, eventValue: String) {
        analyticsManager.sendAnalytics(eventName, eventValue)
    }

}