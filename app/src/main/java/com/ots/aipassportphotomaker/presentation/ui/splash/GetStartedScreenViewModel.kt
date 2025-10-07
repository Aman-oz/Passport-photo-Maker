package com.ots.aipassportphotomaker.presentation.ui.splash

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.ots.aipassportphotomaker.App
import com.ots.aipassportphotomaker.BuildConfig
import com.ots.aipassportphotomaker.adsmanager.admob.MyAdsManager
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.adsmanager.openad.ResumeAdManager
import com.ots.aipassportphotomaker.adsmanager.openad.delay.InitialDelay
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.managers.AdsConsentManager
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.managers.PreferencesHelper
import com.ots.aipassportphotomaker.common.screens.Screens
import com.ots.aipassportphotomaker.common.utils.AdsConstants
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Currency
import java.util.Locale
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
        analyticsManager.sendAnalytics(AnalyticsConstants.OPENED, "GetStartedScreen")
    }

    private fun loadState(isLoading: Boolean) {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = isLoading, errorMessage = null)
        }
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

                        ResumeAdManager.initialize(
                            preferencesHelper,
                            App.getInstance(),
                            InitialDelay.NONE,
                            AdIdsFactory.getResumeAdId(),
                            AdRequest.Builder().build()
                        )

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

    fun onGetStartedClicked(activity: Activity, onComplete: () -> Unit) {
        launch {
            coroutineScope {
                val dialog = createProgressDialog(activity)
                dialog.show()

                val timeoutJob = launch {
                    delay(10000L)
                    dialog.dismiss()
                    onComplete()
                }

                adsManager.loadAndShowInterstitialAd(
                    activity = activity,
                    job = timeoutJob,
                    adUnitId = AdIdsFactory.getWelcomeInterstitialAdId(),
                    interstitialAdScreen = Screens.OTHER,
                    onSuccessListener = object : OnSuccessListener<Boolean> {
                        override fun onSuccess(success: Boolean) {
                            dialog.dismiss()
                            timeoutJob.cancel()
                            onComplete()
                        }
                    },
                    progressDialog = dialog
                )
            }
        }
    }

    private fun createProgressDialog(activity: Activity): Dialog {
        val builder = AlertDialog.Builder(activity)
        val progressBar = ProgressBar(activity, null, android.R.attr.progressBarStyle)
        val textView = TextView(activity).apply {
            text = "Loading ad..."
            gravity = Gravity.CENTER
            setPadding(50, 50, 50, 50)
        }
        val linearLayout = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(progressBar)
            addView(textView)
            setPadding(50, 50, 50, 50)
        }
        builder.setView(linearLayout)
        builder.setCancelable(false)
        return builder.create()
    }

    fun getAnalyticsManager(): AnalyticsManager {
        return analyticsManager
    }

    fun sendEvent(eventName: String, eventValue: String) {
        analyticsManager.sendAnalytics(eventName, eventValue)
    }

    fun logAdRevenue(adType: String, adValue: Double) {
        val price = adValue / 1000000
        val currency = Currency.getInstance(Locale.US)

        val adRevenueParameters = Bundle()
        adRevenueParameters.putDouble(FirebaseAnalytics.Param.VALUE, price)
        adRevenueParameters.putString(FirebaseAnalytics.Param.CURRENCY, currency.currencyCode)
        adRevenueParameters.putString("ad_format", adType)
        adRevenueParameters.putString("ad_network", "admob")
        analyticsManager.sendEvent("ad_revenue_sdk", adRevenueParameters)

        Logger.d("WelcomeInterstitialAdRevenue", "logAdRevenue adType : $adType")
        Logger.d("WelcomeInterstitialAdRevenue", "logAdRevenue Currency : $currency")
        Logger.d("WelcomeInterstitialAdRevenue", "logAdRevenue Price : $adValue")
        Logger.d("WelcomeInterstitialAdRevenue", "logAdRevenue Price divide by 1m : $price")
    }

}