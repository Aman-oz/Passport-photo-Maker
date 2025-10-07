package com.ots.aipassportphotomaker.adsmanager.admob

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory.getInterstitialAdId
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory.getNativeAdId
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory.getNativeAdIdLanguage
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory.getNativeAdIdOnboarding
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory.getWelcomeInterstitialAdId
import com.ots.aipassportphotomaker.adsmanager.callbacks.RewardAdCallback
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.managers.PreferencesHelper
import com.ots.aipassportphotomaker.common.managers.TimeManager
import com.ots.aipassportphotomaker.common.screens.Screens
import com.ots.aipassportphotomaker.common.utils.AdsConstants
import com.ots.aipassportphotomaker.common.utils.AdsConstants.getInterstitialAdDelay
import com.ots.aipassportphotomaker.common.utils.Logger
import kotlinx.coroutines.Job
import java.util.Currency
import java.util.Locale
import java.util.function.Consumer

// Created by amanullah on 20/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class AdsManager(
    val context: Context,
    val analyticsManager: AnalyticsManager,
    val preferencesHelper: PreferencesHelper
) {

    private val TAG: String = AdsManager::class.java.simpleName

    private var mInterstitialAd: InterstitialAd? = null
    var isPremium: Boolean = false
    var rewardAd: RewardedAd? = null
    var rewardedInterstitialAd: RewardedInterstitialAd? = null
    var isStartScreen: Boolean = false
    var loadedLanguageNativeAd: NativeAd? = null
    var loadedOnBoardingNativeAd: NativeAd? = null
    var loadedOtherNativeAd: NativeAd? = null

    fun initialize(callback: () -> Unit) {

        isPremium = preferencesHelper.getBoolean(AdsConstants.IS_NO_ADS_ENABLED)

        if (isPremium) {
            callback.invoke()
            return
        }
        MobileAds.initialize(
            context,
            OnInitializationCompleteListener {
                loadInterstitial()
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

    /*fun getNativeAd(
        nativeAdScreen: FromScreen = FromScreen.OTHER,
        callBack: (Boolean) -> Unit
    ) {

        if (!isNetworkAvailable(context) || isPremium) {
            callBack(false)
            return
        }

        val adsId = when (nativeAdScreen) {
            FromScreen.LANGUAGE -> AdIdsFactory.getNativeAdIdLanguage()
            FromScreen.ONBOARDING -> AdIdsFactory.getNativeAdIdOnboarding()
            FromScreen.OTHER -> AdIdsFactory.getNativeAdId()
        }

        val builder = context.let {
            AdLoader.Builder(
                it,
                adsId
            )
        }

        builder.forNativeAd { nativeAd: NativeAd ->
            Logger.d(TAG, "Monetization :- getNativeAd -> nativeAd: $nativeAd")
            Logger.d(TAG, "Monetization :- getNativeAd -> nativeAdScreen: $nativeAdScreen")
            when (nativeAdScreen) {
                FromScreen.LANGUAGE -> loadedLanguageNativeAd = nativeAd
                FromScreen.ONBOARDING -> loadedOnBoardingNativeAd = nativeAd
                FromScreen.OTHER -> loadedOtherNativeAd = nativeAd
            }
            callBack(true)
        }

        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()

        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdLoaded() {
                Logger.d(TAG, "Monetization :- getNativeAd -> onAdLoaded: $nativeAdScreen")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Logger.d(TAG, "Monetization :- getNativeAd -> onAdFailedToLoad: ")
                callBack.invoke(false)
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }*/

    fun loadAndShowInterstitialAd(
        activity: Activity,
        job: Job? = null,
        interstitialAdScreen: Screens = Screens.OTHER,
        onSuccessListener: OnSuccessListener<Boolean>,
    ) {
        isOtherAdShowing = false

        if (isPremium || !isNetworkAvailable(activity)) {
            onSuccessListener.onSuccess(true)
            return
        }

        val adId = getWelcomeInterstitialAdId()

        kotlin.runCatching {
            val adRequest = AdRequest.Builder().build()

            InterstitialAd.load(activity, adId, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Logger.d(
                        TAG,
                        "Monetization :- InterstitialAd - onAdLoaded: ${interstitialAd.adUnitId}"
                    )
                    job?.cancel()
                    mInterstitialAd = interstitialAd
                    if (!isStartScreen) {
                        return
                    }

                    interstitialAd.show(activity)
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Logger.d(
                                    TAG,
                                    "Monetization :- InterstitialAd - onAdDismissedFullScreenContent: "
                                )
                                job?.cancel()
                                isOtherAdShowing = false
                                onSuccessListener.onSuccess(true)
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                Logger.d(
                                    TAG,
                                    "Monetization :- InterstitialAd -onAdFailedToShowFullScreenContent: "
                                )
                                mInterstitialAd = null
                                job?.cancel()
                                isOtherAdShowing = false
                                onSuccessListener.onSuccess(false)
                            }

                            override fun onAdShowedFullScreenContent() {
                                Logger.d(
                                    TAG,
                                    "Monetization :- InterstitialAd - onAdShowedFullScreenContent: "
                                )
                                mInterstitialAd = null
                                job?.cancel()
                                isOtherAdShowing = true
                            }
                        }

                    mInterstitialAd?.onPaidEventListener = OnPaidEventListener { adValue ->
                        logAdRevenue(
                            AdIdsFactory.getInterstitialAdId(),
                            "interstitial",
                            adValue.valueMicros.toDouble()
                        )
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Logger.d(TAG, "Monetization :- onAdFailedToLoad: ")

                    job?.cancel()
                    onSuccessListener.onSuccess(false)
                }
            })


        }.getOrElse {
            Logger.e(TAG, "InterstitialAd: Exception-> $it")
            FirebaseCrashlytics.getInstance().recordException(it)
        }
    }

    private fun loadInterstitial() {
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

    fun showInterstitialBack(activity: Activity?, callback: Consumer<Boolean?>) {
        TimeManager.getInstance().start()

        if (activity == null) {
            Logger.d(TAG, "showInterstitial: Activity is null")
            return
        }

        if (!isNetworkAvailable(context) || isPremium) {
            callback.accept(false)
            return
        }

        Logger.e(TAG, "AdCount time delay " + TimeManager.getInstance().getElapsedTimeInSecs())
        Logger.e(TAG, "AdCount remote delay " + getInterstitialAdDelay())

        if (mInterstitialAd != null) {
            Logger.d(TAG, "showInterstitial: mInterstitialAd != null")
            if (TimeManager.getInstance().getElapsedTimeInSecs() >= getInterstitialAdDelay()) {
                Logger.d(
                    TAG,
                    "showInterstitial:TimeManager.getInstance().elapsedTimeInSecs >= getInterstitialAdDelay()"
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

    fun showInterstitialWithoutCondition(activity: Activity?, callback: Consumer<Boolean?>) {

        if (activity == null) {
            Logger.d(TAG, "showInterstitial: Activity is null")
            return
        }

        if (!isNetworkAvailable(context) || isPremium) {
            callback.accept(false)
            return
        }

        if (mInterstitialAd != null) {
            Logger.d(TAG, "showInterstitial: mInterstitialAd != null")
            mInterstitialAd?.show(activity)
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    callback.accept(true)
                    isOtherAdShowing = false
                    Logger.d(TAG, "InterstitialAd  The ad was dismissed.")
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
            loadInterstitialAd()
            callback.accept(false)
            Logger.d(TAG, "The interstitial ad wasn't ready yet.")
        }
    }

    fun showOnboardingInterstitial(activity: Activity?, callback: Consumer<Boolean?>) {
        TimeManager.getInstance().start()

        if (!isNetworkAvailable(activity) || isPremium) {
            callback.accept(false)
            return
        }

        Logger.e(TAG, "AdCount time delay " + TimeManager.getInstance().getElapsedTimeInSecs())
        Logger.e(TAG, "AdCount remote delay " + getInterstitialAdDelay())

        if (mInterstitialAd != null) {

            mInterstitialAd?.show(activity!!)
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    callback.accept(true)
                    isOtherAdShowing = false
                    // Called when fullscreen content is dismissed.
                    Logger.d(TAG, "InterstitialAd  The ad was dismissed.")

                    TimeManager.getInstance().stop()
                    TimeManager.getInstance().start()
                    loadInterstitialAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    callback.accept(false)
                    isOtherAdShowing = false
                    // Called when fullscreen content failed to show...
                    Logger.d(TAG, "InterstitialAd The ad failed to show.")
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.

                    isOtherAdShowing = true
                    mInterstitialAd = null
                    Logger.d(TAG, "InterstitialAd The ad was shown.")
                }
            }

            //                AdConstants.setMInterstitialAdCount(1);
            mInterstitialAd?.onPaidEventListener = OnPaidEventListener { adValue ->
                logAdRevenue(
                    getInterstitialAdId(),
                    "interstitial",
                    adValue.valueMicros.toDouble()
                )
            }
        } else {
            loadInterstitialAd()
            callback.accept(false)
            Logger.d(TAG, "The interstitial ad wasn't ready yet.")
        }
    }

    /*fun showNativeAd(
        frameLayout: FrameLayout?,
        shimmerFbAd: ShimmerFrameLayout?,
        layoutInflater: LayoutInflater,
        layout: Int,
        fromScreen: FromScreen,
        isMediaEnable: Boolean = true,
    ) {

        if (!isNetworkAvailable(context) || isPremium || frameLayout == null) {
            return
        }

        val nativeAd = when (fromScreen) {
            FromScreen.LANGUAGE -> loadedLanguageNativeAd
            FromScreen.ONBOARDING -> loadedOnBoardingNativeAd
            FromScreen.OTHER -> loadedOtherNativeAd
        }
        val adsId = when (fromScreen) {
            FromScreen.LANGUAGE -> getNativeAdIdLanguage()
            FromScreen.ONBOARDING -> getNativeAdIdOnboarding()
            FromScreen.OTHER -> getNativeAdId()
        }
        val adView = layoutInflater.inflate(layout, null) as NativeAdView
        nativeAd?.let { populateNativeAdViewNew(it, adView, isMediaEnable, fromScreen) }
        frameLayout.removeAllViews()
        frameLayout.addView(adView)
        if (frameLayout.visibility == View.GONE) {
            frameLayout.visibility = View.VISIBLE
        }
        shimmerFbAd?.visibility = View.GONE
        nativeAd?.setOnPaidEventListener { adValue ->
            logAdRevenue(
                adsId,
                "native",
                adValue.valueMicros.toDouble()
            )
        }

    }

    fun loadAndshowNativeAd(
        frameLayout: FrameLayout,
        context: Context,
        layoutInflater: LayoutInflater,
        layout: Int,
        shimmerFbAd: ShimmerFrameLayout,
        fromScreen: FromScreen,
        isMediaEnable: Boolean
    ) {
        if (!isNetworkAvailable(context) || isPremium) {
            shimmerFbAd.visibility = View.GONE
            shimmerFbAd.showShimmer(false)
            return
        }
        val nativeAdId = when (fromScreen) {
            FromScreen.ONBOARDING -> getNativeAdIdOnboarding()
            FromScreen.LANGUAGE -> getNativeAdIdLanguage()
            else -> getNativeAdId()
        }

        Logger.d(TAG, "showNativeAd: adId -> $nativeAdId")
        val builder = AdLoader.Builder(context, nativeAdId)
        shimmerFbAd.visibility = View.VISIBLE
        shimmerFbAd.showShimmer(true)
        // OnLoadedListener implementation.
        builder.forNativeAd { nativeAd: NativeAd ->
            val adView =
                layoutInflater.inflate(
                    layout,
                    null
                ) as NativeAdView
            populateNativeAdViewNew(nativeAd, adView, isMediaEnable, fromScreen)
            frameLayout.addView(adView)
            if (frameLayout.visibility == View.GONE) {
                frameLayout.visibility = View.VISIBLE
            }

            nativeAd.setOnPaidEventListener { adValue ->
                logAdRevenue(
                    nativeAdId,
                    "native",
                    adValue.valueMicros.toDouble()
                )
            }
        }

        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()

        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                shimmerFbAd.visibility = View.GONE
                shimmerFbAd.showShimmer(false)
                //                                        refresh.setEnabled(true);
                @SuppressLint("DefaultLocale") val error = String.format(
                    "domain: %s, code: %d, message: %s",
                    loadAdError.domain,
                    loadAdError.code,
                    loadAdError.message
                )
            }

            override fun onAdLoaded() {
                shimmerFbAd.visibility = View.GONE
                shimmerFbAd.showShimmer(false)
            }

            override fun onAdClicked() {
                super.onAdClicked()
            }

            override fun onAdClosed() {
                super.onAdClosed()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }*/

    /*private fun populateNativeAdViewNew(
        nativeAd: NativeAd,
        adView: NativeAdView,
        isMediaEnable: Boolean,
        fromScreen: FromScreen = FromScreen.OTHER
    ) {
        kotlin.runCatching {
            val clAdBg = adView.findViewById<ConstraintLayout>(R.id.adBackgroundLayout)
            val btnCTA = adView.findViewById<AppCompatButton>(R.id.ad_call_to_action)
            val adTitle = adView.findViewById<TextView>(R.id.ad_headline)
            val adBody = adView.findViewById<TextView>(R.id.ad_body)

            val drawableAdBG = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setStroke(1, Color.GRAY)
                setColor(Color.parseColor(getAdBackgroundColor()))
            }
            clAdBg.background = drawableAdBG

            val drawableAdBTN = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 64f
                setColor(Color.parseColor(getAdCTAButtonColor()))
            }
            btnCTA.background = drawableAdBTN
            btnCTA.setTextColor(Color.parseColor(getAdCTATextColor()))
            adTitle.setTextColor(Color.parseColor(getAdHeadingColor()))
            adBody.setTextColor(Color.parseColor(getAdBodyColor()))

            adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView
            if (isMediaEnable) {
                nativeAd.mediaContent?.let { adView.mediaView?.setMediaContent(it) }
            }

            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            (adView.headlineView as TextView).text = nativeAd.headline

            adView.bodyView?.apply {
                visibility = if (nativeAd.body == null) View.INVISIBLE else View.VISIBLE
                (this as TextView).text = nativeAd.body
            }

            adView.callToActionView?.apply {
                visibility = if (nativeAd.callToAction == null) View.INVISIBLE else View.VISIBLE
                (this as Button).text = nativeAd.callToAction
            }

            adView.iconView?.apply {
                visibility = if (nativeAd.icon == null) View.GONE else View.VISIBLE
                (this as ImageView).setImageDrawable(nativeAd.icon?.drawable)
            }


            if (fromScreen in listOf(FromScreen.LANGUAGE, FromScreen.ONBOARDING)) {
                adView.iconView?.isVisible = false
                adView.bodyView?.isVisible = false
            }

            adView.setNativeAd(nativeAd)

            nativeAd.mediaContent?.videoController?.let { vc ->
                if (vc.hasVideoContent()) {
                    vc.videoLifecycleCallbacks =
                        object : VideoController.VideoLifecycleCallbacks() {
                            override fun onVideoEnd() {
                                super.onVideoEnd()
                            }
                        }
                }
            }
        }.getOrElse {
            Logger.e(TAG, "NativeAd - populateNativeAdView: " + it.message)
        }
    }*/

    /*fun loadAdaptiveBanner(activity: Activity?, frameLayout: FrameLayout) {
        if (!isNetworkAvailable(context) || isPremium) return

        val adView = AdView(activity!!)
        adView.adUnitId = getBannerAdId()
        frameLayout.removeAllViews()
        frameLayout.addView(adView)

        //        AdSize adSize = getAdSize(activity);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE)

        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
    }

    fun showAdMobBanner(activity: Activity, frameLayout: FrameLayout?) {
        if (!isNetworkAvailable(context) || isPremium) return

        if (frameLayout == null) return

        val adView = AdView(activity)
        adView.adUnitId = getBannerAdId()
        frameLayout.removeAllViews()
        frameLayout.addView(adView)

        val adSize = getAdSize(activity)
        adView.setAdSize(adSize)

        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Logger.d(TAG, "showAdMobBanner 1 onAdFailedToLoad: " + loadAdError.message)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Logger.d(TAG, "showAdMobBanner 1 onAdLoaded: ")
            }
        }
    }

    fun showAdmobCollapseBanner(activity: Activity, frameLayout: FrameLayout?, adRoot: View) {
        if (!isNetworkAvailable(context) || isPremium) {
            return
        }

        if (frameLayout == null) {
            return
        }

        val adView = AdView(activity)
        adView.adUnitId = getBannerAdId()
        frameLayout.removeAllViews()
        frameLayout.addView(adView)

        val adSize = getAdSize(activity)
        adView.setAdSize(adSize)
        val extras = Bundle()
        extras.putString("collapsible", "bottom")
        val adRequest = AdRequest.Builder().addNetworkExtrasBundle(
            AdMobAdapter::class.java, extras
        ).build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)

        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                adRoot.visibility = View.GONE
                Logger.d(TAG, "showAdMobBanner onAdFailedToLoad: " + loadAdError.message)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                adRoot.visibility = View.VISIBLE
                Logger.d(TAG, "showAdMobBanner onAdLoaded: ")
            }
        }
    }

    fun showAdMobBanner(activity: Activity, frameLayout: FrameLayout?, adRoot: View) {
        if (!isNetworkAvailable(context) || isPremium) {
            adRoot.visibility = View.GONE
            return
        }

        if (frameLayout == null) {
            adRoot.visibility = View.GONE
            return
        }

        adRoot.visibility = View.VISIBLE

        val adView = AdView(activity)
        adView.adUnitId = getBannerAdId()
        frameLayout.removeAllViews()
        frameLayout.addView(adView)

        val adSize = getAdSize(activity)
        adView.setAdSize(adSize)

        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)

        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                adRoot.visibility = View.GONE
                Logger.d(TAG, "showAdMobBanner onAdFailedToLoad: " + loadAdError.message)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                adRoot.visibility = View.VISIBLE
                Logger.d(TAG, "showAdMobBanner onAdLoaded: ")
            }
        }

        adView.onPaidEventListener = OnPaidEventListener { adValue ->
            logAdRevenue(
                getBannerAdId(),
                "banner",
                adValue.valueMicros.toDouble()
            )
        }
    }

    private fun getAdSize(activity: Activity): AdSize {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density

        val adWidth = (widthPixels / density).toInt()

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    fun showRectangularBanner(activity: Activity, frameLayout: FrameLayout?) {
        if (!isNetworkAvailable(context) || isPremium) return

        if (frameLayout == null) return

        val adView = AdView(activity)
        adView.adUnitId = getBannerAdId()
        frameLayout.removeAllViews()
        frameLayout.addView(adView)

        val adSize = getAdSize(activity)
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE)

        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)

        adView.onPaidEventListener = OnPaidEventListener { adValue ->
            logAdRevenue(
                getBannerAdId(),
                "banner",
                adValue.valueMicros.toDouble()
            )
        }
    }

    fun showRectangularBanner(
        activity: Activity,
        adFrame: FrameLayout?,
        shimmer: ShimmerFrameLayout,
        adRootLayout: View
    ) {
        if (!isNetworkAvailable(context) || isPremium) {
            adRootLayout.visibility = View.GONE
            shimmer.visibility = View.GONE
            return
        }

        if (adFrame == null) {
            adRootLayout.visibility = View.GONE
            shimmer.visibility = View.GONE
            return
        }

        val adView = AdView(activity)
        adView.adUnitId = getBannerAdId()
        adFrame.removeAllViews()
        adFrame.addView(adView)

        val adSize = getAdSize(activity)
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE)

        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)

        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)

                adRootLayout.visibility = View.GONE
                shimmer.visibility = View.GONE
            }

            override fun onAdLoaded() {
                super.onAdLoaded()

                adRootLayout.visibility = View.VISIBLE
                shimmer.visibility = View.GONE
            }
        }
    }*/

    fun loadRewardedAd(activityContext: Activity?) {
        isOtherAdShowing = false
        if (!isNetworkAvailable(context) || isPremium) return
        if (rewardAd == null) {
            val adRequest = AdRequest.Builder().build()
            Logger.d(TAG, "loadRewardedAd: AdID: " + AdIdsFactory.getRewardedAdId())
            RewardedAd.load(
                activityContext!!,
                AdIdsFactory.getRewardedAdId(),
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Logger.d("Molts", loadAdError.toString())
                        isOtherAdShowing = false
                        rewardAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        rewardAd = rewardedAd
                        Logger.d("Molts", "onAdLoaded")
                    }
                })
        }
    }

    fun showRewardedAd(activityContext: Activity?, listener: RewardAdCallback) {
        if (!isNetworkAvailable(context) || isPremium) return
        if (rewardAd == null) {
            Logger.d(TAG, "The rewarded ad wasn't ready yet.")
            return
        }

        rewardAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Logger.d(TAG, "onAdShowedFullScreenContent")
                isOtherAdShowing = true
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Logger.d(TAG, "onAdFailedToShowFullScreenContent")
                isOtherAdShowing = false
                rewardAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                rewardAd = null
                isOtherAdShowing = false
                Logger.d(TAG, "onAdDismissedFullScreenContent")
                listener.onDismissRewardAd()
            }
        }

        rewardAd?.onPaidEventListener = OnPaidEventListener { adValue ->
            logAdRevenue(
                AdIdsFactory.getRewardedAdId(),
                "rewardedVideo",
                adValue.valueMicros.toDouble()
            )
        }

        rewardAd?.show(activityContext!!) {
            Logger.d(TAG, "The user earned the reward.")
            listener.onRewardEarned(true)
        }
    }

    val isRewardedAdNotLoaded: Boolean
        get() = rewardAd == null

    val isInterstitialNotLoaded: Boolean
        get() = mInterstitialAd == null

    fun loadInterstitialAd() {
        if (!isNetworkAvailable(context) || isPremium || mInterstitialAd != null) return
        loadInterstitial()
    }

    fun loadRewardedInterstitialAd(activityContext: Activity?) {
        isOtherAdShowing = false
        if (!isNetworkAvailable(context) || isPremium) return
        if (rewardedInterstitialAd == null) {
            RewardedInterstitialAd.load(
                activityContext!!, AdIdsFactory.getRewardedInterstitialAdId(),
                AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Logger.d("$TAG Rewarded", loadAdError.toString())
                        isOtherAdShowing = false
                        rewardedInterstitialAd = null
                    }

                    override fun onAdLoaded(ad: RewardedInterstitialAd) {
                        rewardedInterstitialAd = ad

                        Logger.d("$TAG Rewarded", "onAdLoaded")
                    }
                })
        }
    }

    fun showRewardedInterstitialAd(activityContext: Activity?, listener: RewardAdCallback) {
        if (!isNetworkAvailable(context) || isPremium) {
            listener.onRewardEarned(false)
            return
        }
        if (rewardedInterstitialAd == null) {
            Logger.d(TAG, "The rewarded ad wasn't ready yet.")
            listener.onRewardEarned(false)
            return
        }

        rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Logger.d(TAG, "onAdShowedFullScreenContent")
                isOtherAdShowing = true
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Logger.d(TAG, "onAdFailedToShowFullScreenContent")
                // Don't forget to set the ad reference to null so you
                // don't show the ad a second time.
                rewardedInterstitialAd = null
                isOtherAdShowing = false
            }

            override fun onAdDismissedFullScreenContent() {
                rewardedInterstitialAd = null
                isOtherAdShowing = false
                Logger.d(TAG, "onAdDismissedFullScreenContent")
                loadRewardedInterstitialAd(activityContext)
                listener.onDismissRewardAd()
            }
        }

        rewardedInterstitialAd?.onPaidEventListener = OnPaidEventListener { adValue ->
            logAdRevenue(
                AdIdsFactory.getRewardedInterstitialAdId(),
                "rewardedInterstitial",
                adValue.valueMicros.toDouble()
            )
        }


        rewardedInterstitialAd?.show(activityContext!!) {
            Logger.d(TAG, "The user earned the reward.")
            listener.onRewardEarned(true)
        }
    }

    fun isRewardedInterstialAd(activity: Activity?): Boolean {
        if (rewardedInterstitialAd == null) {
            loadRewardedInterstitialAd(activity)
            return false
        }
        return true
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

    companion object {

        var isOtherAdShowing: Boolean = false

    }

    enum class FromScreen {
        OTHER, ONBOARDING, LANGUAGE
    }
}