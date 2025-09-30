package com.ots.aipassportphotomaker.presentation.ui.splash

import android.app.Activity
import android.util.Log
import com.ots.aipassportphotomaker.BuildConfig
import com.ots.aipassportphotomaker.adsmanager.admob.MyAdsManager
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.managers.AdsConsentManager
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.managers.PreferencesHelper
import com.ots.aipassportphotomaker.common.utils.AdsConstants
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GetStartedScreenViewModel @Inject constructor(
    getStartedScreenBundle: GetStartedScreenBundle,
    private val adsManager: MyAdsManager,
    private val analyticsManager: AnalyticsManager,
    private val preferencesHelper: PreferencesHelper,
    private val adsConsentManager: AdsConsentManager
) : BaseViewModel() {

    private val TAG = GetStartedScreenViewModel::class.java.simpleName

    private val _uiState: MutableStateFlow<GetStartedScreenUiState> = MutableStateFlow(
        GetStartedScreenUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<GetStartedScreenNavigationState> =
        singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _consentState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val consentState = _consentState.asStateFlow()

    init {
        onInitialState()
        loadState(false)
    }

    private fun onInitialState() = launch {
        analyticsManager.sendAnalytics("screen", "get_started")
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


    fun initConsent(activity: Activity) {
        val canRequestAds: Boolean = adsConsentManager.canRequestAds

        if (!isPremiumUser()) {

            adsConsentManager.canRequestAds.apply {
                if (this == false) {
                    adsConsentManager.showGDPRConsent(
                        activity,
                        BuildConfig.DEBUG
                    ) { consentError ->

                        if (consentError != null) {
                            Logger.e(
                                TAG,
                                "Error during consent gathering: ${consentError.message}"
                            )
                        }
                        Logger.d(TAG, "Consent gathering complete")
                        //Can request ads
                        _consentState.value = true
                        adsManager.initialize {  }
                        analyticsManager.sendAnalytics("consent", "gdpr_consent_given")

                    }
                } else {
                    Logger.d(TAG, "Consent already gathered")
                    //can request ads
                    _consentState.value = true
                    adsManager.initialize {  }
                    analyticsManager.sendAnalytics("consent", "gdpr_consent_already_given")
                }
            }
        } else {
            Log.d(TAG, "Ads can be requested or user is premium")
            //can request ads
            _consentState.value = true
            adsManager.initialize {  }
        }
    }

    fun sendEvent(eventName: String, eventValue: String) {
        analyticsManager.sendAnalytics(eventName, eventValue)
    }

}