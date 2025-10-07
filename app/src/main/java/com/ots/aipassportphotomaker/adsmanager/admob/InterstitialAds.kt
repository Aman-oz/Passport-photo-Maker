package com.ots.aipassportphotomaker.adsmanager.admob

import android.app.Activity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.utils.Logger
import java.util.Currency
import java.util.Locale

// Created by amanullah on 20/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

fun Activity.loadFullScreenAd(
    analyticsManager: AnalyticsManager,
    adUnitId: String,
    adRequest: AdRequest = AdRequest.Builder().build(),
    onAdLoaded: (InterstitialAd) -> Unit,
    onAdFailedToLoad: (LoadAdError) -> Unit,
    onAdDismissed: () -> Unit
) {
    InterstitialAd.load(
        this,
        adUnitId,
        adRequest,
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                super.onAdLoaded(interstitialAd)
                onAdLoaded(interstitialAd)
                interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        onAdDismissed()
                    }
                }

                interstitialAd.setOnPaidEventListener { adValue ->
                    /*logAdRevenue(
                        analyticsManager,
                        adType = "interstitial",
                        adValue = adValue.valueMicros.toDouble()
                    )*/
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                onAdFailedToLoad(loadAdError)
            }
        }
    )
}

// Show the interstitial ad
