package com.ots.aipassportphotomaker.adsmanager.admob

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.ots.aipassportphotomaker.adsmanager.admob.AdsManager.Companion.isOtherAdShowing
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory.getInterstitialAdId
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.managers.PreferencesHelper
import com.ots.aipassportphotomaker.common.managers.TimeManager
import com.ots.aipassportphotomaker.common.utils.AdsConstants
import com.ots.aipassportphotomaker.common.utils.AdsConstants.getInterstitialAdDelay
import com.ots.aipassportphotomaker.common.utils.Logger
import java.util.Currency
import java.util.Locale
import java.util.function.Consumer

// Created by amanullah on 21/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class MyAdsManager(
    val context: Context,
    val analyticsManager: AnalyticsManager,
    val preferencesHelper: PreferencesHelper
) {

    private val TAG: String = MyAdsManager::class.java.simpleName

    private var mInterstitialAd: InterstitialAd? = null
    var isPremium: Boolean = false

    fun initialize(callback: () -> Unit) {

        isPremium = preferencesHelper.getBoolean(AdsConstants.IS_NO_ADS_ENABLED)

        if (isPremium) {
            callback.invoke()
            return
        }
        MobileAds.initialize(
            context,
            OnInitializationCompleteListener {
                loadInterstitialAd()
                callback.invoke()
            })

    }

    fun setEnabledNoAds(enabledNoAds: Boolean) {
        preferencesHelper.setBoolean(AdsConstants.IS_NO_ADS_ENABLED, enabledNoAds)
        isPremium = enabledNoAds
        if (enabledNoAds) {
            return
        }
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun loadInterstitial(onAdLoaded: (Boolean) -> Unit) {

        if (!isNetworkAvailable(context) || isPremium) {
            onAdLoaded.invoke(false)
            return
        }

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            getInterstitialAdId(),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    Logger.i(TAG, "InterstitialAd onAdLoaded")
                    onAdLoaded.invoke(true)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Logger.i(TAG, " InterstitialAd " + loadAdError.message)
                    mInterstitialAd = null
                    onAdLoaded.invoke(false)
                }
            })
    }

    private fun loadInterstitialAd() {
        isOtherAdShowing = false
        if (!isNetworkAvailable(context) || isPremium) return

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            getInterstitialAdId(),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    Logger.i(TAG, "InterstitialAd onAdLoaded")

                    if (mInterstitialAd == null) return
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Logger.i(TAG, " InterstitialAd " + loadAdError.message)
                    mInterstitialAd = null
                    isOtherAdShowing = false
                }
            })
    }

    fun showInterstitial(activity: Activity?, callback: Consumer<Boolean?>) {
        TimeManager.getInstance().start()

        if (activity == null) {
            Logger.d(TAG, "showInterstitial: Activity is null")
            callback.accept(false)
            return
        }

        if (!isNetworkAvailable(context) || isPremium) {
            callback.accept(false)
            return
        }

        Logger.e(TAG, "AdCount time delay " + TimeManager.getInstance().getElapsedTimeInSecs())

        if (mInterstitialAd != null) {
            Logger.d(TAG, "showInterstitial: mInterstitialAd != null")
            if (TimeManager.getInstance().getElapsedTimeInSecs() >= getInterstitialAdDelay()) {
                Logger.d(
                    TAG,
                    "showInterstitial: mInterstitialAdCount > getInterstitialAdCount() || TimeManager.getInstance().elapsedTimeInSecs >= getInterstitialAdDelay()"
                )
                mInterstitialAd?.show(activity)
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        callback.accept(true)
                        isOtherAdShowing = false
                        Logger.d(TAG, "InterstitialAd  The ad was dismissed.")

                        TimeManager.getInstance().stop()
                        TimeManager.getInstance().start()
                        loadInterstitialAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        callback.accept(false)
                        isOtherAdShowing = false
                        mInterstitialAd = null
                        Logger.d(TAG, "InterstitialAd The ad failed to show.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        isOtherAdShowing = true
                        mInterstitialAd = null
                        Logger.d(TAG, "InterstitialAd The ad was shown.")
                    }
                }

                mInterstitialAd?.onPaidEventListener = OnPaidEventListener { adValue ->
                    logAdRevenue(
                        getInterstitialAdId(),
                        "interstitial",
                        adValue.valueMicros.toDouble()
                    )
                }
            } else {

                callback.accept(false)
            }
        } else {
            loadInterstitialAd()
            callback.accept(false)
            Logger.d(TAG, "The interstitial ad wasn't ready yet.")
        }
    }


    private fun logAdRevenue(adId: String, adType: String, adValue: Double) {
        val price = adValue / 1000000
        val currency = Currency.getInstance(Locale.US)

        val adRevenueParameters = Bundle()
        adRevenueParameters.putDouble(FirebaseAnalytics.Param.VALUE, price)
        adRevenueParameters.putString(FirebaseAnalytics.Param.CURRENCY, currency.currencyCode)
        adRevenueParameters.putString("ad_format", adType)
        adRevenueParameters.putString("ad_network", "admob")
        analyticsManager.sendEvent("ad_revenue_sdk", adRevenueParameters)

        Logger.d(TAG, "logAdRevenue adType : $adType")
        Logger.d(TAG, "logAdRevenue Currency : $currency")
        Logger.d(TAG, "logAdRevenue Price : $adValue")
        Logger.d(TAG, "logAdRevenue Price divide by 1m : $price")
    }
}