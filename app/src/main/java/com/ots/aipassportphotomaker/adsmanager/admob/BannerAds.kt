package com.ots.aipassportphotomaker.adsmanager.admob

// Created by amanullah on 20/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.ots.aipassportphotomaker.common.utils.Logger

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnit: String,
    adSize: AdSize = AdSize.FULL_BANNER,
    onAdLoaded: (Boolean) -> Unit = {},
) {
    AndroidView(
        modifier = modifier.clip(RoundedCornerShape(10)),
        factory = { context ->
            AdView(context).apply {
                setAdSize(adSize)
                adUnitId = adUnit

                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        onAdLoaded(true)
                        Logger.d("AdMobBanner", "Ad Loaded")
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        onAdLoaded(false)
                        Logger.e("AdMobBanner", "Ad Failed to load: ${p0.message}")
                    }
                }

                loadAd(AdRequest.Builder()
                    .build())
            }
        }
    )
}

@Composable
fun AdMobAdaptiveBanner(
    modifier: Modifier = Modifier,
    margin: Dp = 0.dp,
    strokeColor : Color = MaterialTheme.colorScheme.onBackground,
    strokeWidth: Dp = 0.2.dp,
    @StringRes unitIdRes: Int
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.roundToPx() }

//    val screenWidth = windowInfo.containerSize.width


    val adSize = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(
        context,
        screenWidthPx
    )
    val unitId = stringResource(unitIdRes)
    AndroidView(
        modifier = modifier
            .padding(margin)
            .border(strokeWidth, strokeColor, RoundedCornerShape(10))
            .clip(RoundedCornerShape(10)),
        factory = {
            AdView(context).apply {
                setAdSize(adSize)
                adUnitId = unitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}


enum class CollapseDirection(val value: String) {
    TOP("top"),
    BOTTOM("bottom")
}

@Composable
fun AdMobCollapsableBanner(
    modifier: Modifier = Modifier,
    @StringRes unitIdRes: Int,
    adSize: AdSize = AdSize.FULL_BANNER,
    collapseDirection: CollapseDirection = CollapseDirection.TOP
) {
    val unitId = stringResource(unitIdRes)
    AndroidView(
        modifier = modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(10)),
        factory = { context ->
            AdView(context).apply {
                setAdSize(adSize)
                adUnitId = unitId
                val extras = Bundle()
                extras.putString("collapsible", collapseDirection.value)
                loadAd(
                    AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                        .build()
                )
            }
        }
    )
}