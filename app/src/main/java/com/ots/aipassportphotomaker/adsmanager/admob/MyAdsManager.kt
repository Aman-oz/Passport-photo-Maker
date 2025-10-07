package com.ots.aipassportphotomaker.adsmanager.admob

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory.getInterstitialAdId
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

// Created by amanullah on 21/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class MyAdsManager(
    val context: Context,
    val analyticsManager: AnalyticsManager,
    val preferencesHelper: PreferencesHelper
) {

    private val TAG: String = MyAdsManager::class.java.simpleName

    private var mInterstitialAd: InterstitialAd? = null
    private var mInterstitialAdLoadAndShow: InterstitialAd? = null
    var isPremium: Boolean = false

    var rewardAd: RewardedAd? = null
    var rewardedInterstitialAd: RewardedInterstitialAd? = null

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

    fun showInterstitial(activity: Activity?, shouldShow: Boolean, callback: Consumer<Boolean?>) {
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
            if (TimeManager.getInstance().getElapsedTimeInSecs() >= getInterstitialAdDelay() || shouldShow) {
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

    fun loadAndShowInterstitialAd(
        activity: Activity,
        job: Job? = null,
        adUnitId: String,
        progressDialog: Dialog? = null,
        interstitialAdScreen: Screens = Screens.OTHER,
        onSuccessListener: OnSuccessListener<Boolean>,
    ) {
        isOtherAdShowing = false

        if (isPremium || !isNetworkAvailable(activity)) {
            onSuccessListener.onSuccess(false)
            return
        }

        kotlin.runCatching {
            // show progress dialog
            val dialog = progressDialog ?: run {
                val builder = AlertDialog.Builder(activity)
                val inflater = LayoutInflater.from(activity)
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
                builder.create()
            }
            dialog.show()

            val adRequest = AdRequest.Builder().build()

            InterstitialAd.load(activity, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Logger.d(
                        TAG,
                        "InterstitialAd - onAdLoaded: ${interstitialAd.adUnitId}"
                    )
                    dialog.dismiss()

                    job?.cancel()
                    mInterstitialAdLoadAndShow = interstitialAd
                    interstitialAd.show(activity)

                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Logger.d(
                                    TAG,
                                    "InterstitialAd - onAdDismissedFullScreenContent: "
                                )
                                job?.cancel()
                                isOtherAdShowing = false
                                onSuccessListener.onSuccess(true)
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                Logger.d(
                                    TAG,
                                    "InterstitialAd -onAdFailedToShowFullScreenContent: "
                                )
                                mInterstitialAdLoadAndShow = null
                                job?.cancel()
                                isOtherAdShowing = false
                                onSuccessListener.onSuccess(false)
                            }

                            override fun onAdShowedFullScreenContent() {
                                Logger.d(
                                    TAG,
                                    "InterstitialAd - onAdShowedFullScreenContent: "
                                )
                                mInterstitialAdLoadAndShow = null
                                job?.cancel()
                                isOtherAdShowing = true
                            }
                        }

                    mInterstitialAdLoadAndShow?.onPaidEventListener = OnPaidEventListener { adValue ->
                        logAdRevenue(
                            adUnitId,
                            "interstitial",
                            adValue.valueMicros.toDouble()
                        )
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Logger.d(TAG, "onAdFailedToLoad: ")

                    dialog.dismiss()
                    job?.cancel()
                    onSuccessListener.onSuccess(false)
                }
            })


        }.getOrElse {
            Logger.e(TAG, "InterstitialAd: Exception-> $it")
            FirebaseCrashlytics.getInstance().recordException(it)
        }
    }

    /**
     * ---------------Rewarded Ads---------------------*
     * */

    fun loadRewardedAd(activityContext: Activity) {
        isOtherAdShowing = false
        if (!isNetworkAvailable(context) || isPremium) return
        if (rewardAd == null) {
            val adRequest = AdRequest.Builder().build()
            Logger.d(TAG, "loadRewardedAd: AdID: " + AdIdsFactory.getRewardedAdId())
            RewardedAd.load(
                activityContext,
                AdIdsFactory.getRewardedAdId(),
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Logger.d(TAG, "loadRewardedAd: onAdFailedToLoad, error: ${loadAdError.message}")
                        isOtherAdShowing = false
                        rewardAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        rewardAd = rewardedAd
                        Logger.d(TAG, "loadRewardedAd: onAdLoaded")
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

        rewardAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Logger.d(TAG, "onAdShowedFullScreenContent")
                isOtherAdShowing = true
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
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

    val isRewardedInterstitialAdNotLoaded: Boolean
        get() = rewardedInterstitialAd == null

    fun loadRewardedInterstitialAd(activityContext: Activity?) {
        isOtherAdShowing = false
        if (!isNetworkAvailable(context) || isPremium) return
        if (rewardedInterstitialAd == null) {
            RewardedInterstitialAd.load(
                activityContext!!, AdIdsFactory.getRewardedInterstitialAdId(),
                AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Logger.d("$TAG Rewarded", loadAdError.toString())
                        AdsManager.Companion.isOtherAdShowing = false
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
                Logger.d(TAG, "onAdShowedFullScreenContent")
                isOtherAdShowing = true
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {

                Logger.d(TAG, "onAdFailedToShowFullScreenContent")
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

    // Load and show rewarded ad
    fun loadAndShowRewardedAd(
        activityContext: Activity?,
        adUnitId: String,
        listener: RewardAdCallback
    ) {
        isOtherAdShowing = false
        if (!isNetworkAvailable(context) || isPremium) {
            listener.onRewardEarned(false)
            return
        }

        if (rewardAd == null) {
            // show progress dialog
            val builder = AlertDialog.Builder(activityContext)
            val inflater = LayoutInflater.from(activityContext)
            val dialogView = inflater.inflate(android.R.layout.simple_list_item_1, null)
            val progressBar = ProgressBar(activityContext, null, android.R.attr.progressBarStyle)
            val textView = TextView(activityContext).apply {
                text = "Loading ad..."
                gravity = Gravity.CENTER
                setPadding(50, 50, 50, 50)
            }
            val linearLayout = LinearLayout(activityContext).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                addView(progressBar)
                addView(textView)
                setPadding(50, 50, 50, 50)
            }
            builder.setView(linearLayout)
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()

            val adRequest = AdRequest.Builder().build()
            Logger.d(TAG, "loadRewardedAd: AdID: $adUnitId")
            RewardedAd.load(
                activityContext!!,
                adUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Logger.d(TAG, "loadRewardedAd: onAdFailedToLoad, error: ${loadAdError.message}")
                        dialog.dismiss()
                        isOtherAdShowing = false
                        rewardAd = null
                        listener.onRewardEarned(false)
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        dialog.dismiss()
                        rewardAd = rewardedAd
                        Logger.d(TAG, "loadRewardedAd: onAdLoaded")
                        showRewardedAd(activityContext, listener)
                    }
                })
        } else {
            showRewardedAd(activityContext, listener)
        }
    }

    /**------------------------------------------------*/


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
}