package com.ots.aipassportphotomaker.adsmanager.admob

// Created by amanullah on 20/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.window.layout.WindowMetricsCalculator
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.ots.aipassportphotomaker.adsmanager.revenue.logAdRevenue
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.utils.Logger
import kotlin.math.max

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnit: String,
    adSize: AdSize = AdSize.FULL_BANNER,
    onAdLoaded: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val adView = remember {
        AdView(context).apply {
            setAdSize(adSize)
            adUnitId = adUnit
        }
    }

    LaunchedEffect(adView) {
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                onAdLoaded(true)
                Logger.d("MyAdsManager", "Banner Ad Loaded")
            }
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                onAdLoaded(false)
                Logger.e("MyAdsManager", "Banner Ad Failed to load: ${p0.message}")
            }
        }
        adView.onPaidEventListener = OnPaidEventListener { adValue ->
            logAdRevenue(
                analyticsManager = AnalyticsManager.getInstance(),
                adType = "banner",
                adValue = adValue.valueMicros.toDouble()
            )
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    AndroidView(
        modifier = modifier,
        factory = { adView }
    )
}


@Composable
fun AdaptiveBannerAd(
    modifier: Modifier = Modifier,
    adUnit: String,
    adSize: AdSize = AdSize.FULL_BANNER,
    onAdLoaded: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    // Compute adaptive size if not overridden (your WindowMetrics logic)
    val computedAdSize = remember(configuration.orientation) {
        if (adSize == AdSize.FULL_BANNER) {  // Or check if adaptive-needed
            val activity = context as? Activity
            val displayMetrics: DisplayMetrics = context.resources.displayMetrics
            var widthPx = displayMetrics.widthPixels.toFloat()

            if (activity != null) {
                val windowMetrics = WindowMetricsCalculator.getOrCreate()
                    .computeCurrentWindowMetrics(activity)
                val boundsWidth = windowMetrics.bounds.width().toFloat()
                if (boundsWidth > 0) widthPx = boundsWidth
            }

            val densityFactor = displayMetrics.density
            val widthDp = if (densityFactor > 0) max((widthPx / densityFactor).toInt(), 1) else 1
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
        } else {
            adSize
        }
    }

    val finalAdSize = remember { computedAdSize }  // Stable reference

    val adView = remember(finalAdSize) {  // Recreate AdView if size changes (e.g., rotation)
        AdView(context).apply {
            setAdSize(finalAdSize)
            adUnitId = adUnit
        }
    }

    LaunchedEffect(adView, finalAdSize, configuration.orientation) {  // Reload on size/orientation change
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                onAdLoaded(true)
                Logger.d("MyAdsManager", "Adaptive Banner Ad Loaded (size: ${finalAdSize.width}dp x ${finalAdSize.height}dp)")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                onAdLoaded(false)
                Logger.e("MyAdsManager", "Adaptive Banner Ad Failed to load: ${error.message}")
            }
        }
        adView.onPaidEventListener = OnPaidEventListener { adValue ->
            logAdRevenue(
                analyticsManager = AnalyticsManager.getInstance(),
                adType = "banner",
                adValue = adValue.valueMicros.toDouble()
            )
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    DisposableEffect(adView) {  // Cleanup on disposal
        onDispose {
            adView.pause()
            adView.destroy()
            Logger.d("MyAdsManager", "Adaptive Banner AdView destroyed")
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { adView }
    )
}


enum class CollapseDirection(val value: String) {
    TOP("top"),
    BOTTOM("bottom")
}

@Composable
fun AdMobCollapsableBanner(
    modifier: Modifier = Modifier,
    adUnit: String,
    adSize: AdSize = AdSize.FULL_BANNER,
    collapseDirection: CollapseDirection = CollapseDirection.TOP,
    onAdLoaded: (Boolean) -> Unit = {},
) {

    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(adSize)
                adUnitId = adUnit
                val extras = Bundle()
                extras.putString("collapsible", collapseDirection.value)
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        onAdLoaded(true)
                        Logger.d("MyAdsManager", "Banner Ad Loaded")
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        onAdLoaded(false)
                        Logger.e("MyAdsManager", "Ad Failed to load: ${p0.message}")
                    }
                }

                onPaidEventListener = OnPaidEventListener { adValue ->
                    logAdRevenue(
                        analyticsManager = AnalyticsManager.getInstance(),
                        adType = "banner",
                        adValue = adValue.valueMicros.toDouble()
                    )
                }

                loadAd(
                    AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                        .build()
                )
            }
        }
    )
}