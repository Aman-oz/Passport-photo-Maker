package com.ots.aipassportphotomaker.adsmanager.openad

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.ots.aipassportphotomaker.adsmanager.admob.MyAdsManager
import com.ots.aipassportphotomaker.adsmanager.openad.delay.InitialDelay
import com.ots.aipassportphotomaker.common.managers.PreferencesHelper
import com.ots.aipassportphotomaker.common.managers.TimeManager
import com.ots.aipassportphotomaker.common.utils.AdsConstants
import com.ots.aipassportphotomaker.common.utils.Logger

// Created by amanullah on 03/10/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class ResumeAdManager @JvmOverloads constructor(
    private val preferencesHelper: PreferencesHelper,
    application: Application,
    initialDelay: InitialDelay,
    private var adUnitId: String,
    override var adRequest: AdRequest = AdRequest.Builder().build()

) : BaseManager(application),
    LifecycleObserver {

    private val TAG = ResumeAdManager::class.java.simpleName
    var startTime = 0L

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        this.initialDelay = initialDelay
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var resumeAdManager: ResumeAdManager? = null
        fun initialize(
            preferencesHelper: PreferencesHelper,
            myApplication: Application,
            @NonNull initialDelay: InitialDelay,
            @NonNull adUnitId: String,
            adRequest: AdRequest = AdRequest.Builder().build()
        ) {
            if (resumeAdManager == null)
                resumeAdManager =
                    ResumeAdManager(
                        preferencesHelper,
                        myApplication,
                        initialDelay,
                        adUnitId,
                        adRequest
                    )
            resumeAdManager?.fetchAd()
        }


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        //fetchAd()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        if (!preferencesHelper.getBoolean(AdsConstants.IS_NO_ADS_ENABLED, false)) {

            val currentTime = System.currentTimeMillis()
            if (startTime > 0) {
                val diff = (currentTime - startTime) / 1000
                if (
                    !currentActivity?.javaClass?.simpleName?.contains("PremiumActivity")!! &&
                    currentActivity?.javaClass?.simpleName != AdActivity::class.java.simpleName &&
                    diff >= 10
                ) {

                    showAdIfAvailable()

                } else {

                    startTime = 0
                    if (!isAdAvailable()) {
                        Log.d(TAG, "onResume: ad is not available fetchAd ")
                        fetchAd()
                    }
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        Log.d(TAG, "TimeManager Resume App paused at" + System.currentTimeMillis())
        startTime = System.currentTimeMillis()

    }


    /* @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
     private fun onStop() {
         appOpenAd = null
         isShowingAd = false
         fetchAd()
     }*/


    // Let's fetch the Ad
    fun fetchAd() {
        if (isAdAvailable() && !preferencesHelper.getBoolean(AdsConstants.IS_NO_ADS_ENABLED, false)
        ) return
        loadAd()
        Logger.d(TAG, "A pre-cached Ad was not available, loading one.")
    }

    // Show the Ad if the conditions are met.
    fun showAdIfAvailable() {
        if (!isShowingAd &&
            isAdAvailable() &&
            isInitialDelayOver() &&
            !MyAdsManager.isOtherAdShowing
        ) {
            Log.d(TAG, "loading.......: ")
            appOpenAd?.fullScreenContentCallback = getFullScreenContentCallback()

            currentActivity?.let {
                if (it.componentName?.className != "com.las.collage.maker.ui.StartActivity") {
                    appOpenAd?.show(it)
                } else {
                    Log.d(TAG, "not showing Ad because on StartActivity")
                }
            }

        } else {
            Log.d(TAG, "ResumeOpenAd Ad not available fetching ad")
            if (!isInitialDelayOver()) Logger.d(TAG, "The Initial Delay period is not over yet.")
            /**
             *If the next session happens after the delay period is over
             * & under 4 Hours, we can show a cached Ad.
             * However the above will only work for DelayType.HOURS.
             */


            Log.d("TimeComplexityTAG", "ResumeOpenAdManager: ${System.currentTimeMillis()}")
            fetchAd()
        }
    }

    private fun loadAd() {
        loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(p0: AppOpenAd) {
                this@ResumeAdManager.appOpenAd = p0
                this@ResumeAdManager.loadTime = getCurrentTime()

                Logger.d(TAG, "MediationModule Ad Loaded")
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Logger.e(TAG, "MediationModule Ad Failed To Load, Reason: ${p0.responseInfo}")
            }
        }
        AppOpenAd.load(
            getApplication(), adUnitId, adRequest, /*orientation,*/
            loadCallback as AppOpenAd.AppOpenAdLoadCallback
        )
    }

    // Handling the visibility of App Open Ad
    private fun getFullScreenContentCallback(): FullScreenContentCallback {
        return object : FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                TimeManager.getInstance().stop()
                TimeManager.getInstance().start()
                fetchAd()
                Log.d(
                    TAG,
                    "onAdDismissedFullScreenContent: current Activity: $currentActivity"
                )
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {

                Logger.e(TAG, "MediationModule Ad Failed To Show Full-Screen Content: ${adError.message}")
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }
    }
}