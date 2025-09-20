package com.ots.aipassportphotomaker.adsmanager.admob

// Created by amanullah on 20/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.ots.aipassportphotomaker.R

@Composable
fun NativeAdComposable(
    modifier: Modifier = Modifier,
    adUnitId: String
) {
    val context = LocalContext.current
    var nativeAdState by remember { mutableStateOf<NativeAd?>(null) }
    var adErrorState by remember { mutableStateOf<String?>(null) }
    val density = LocalDensity.current

    LaunchedEffect(adUnitId) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad: NativeAd ->
                nativeAdState?.destroy()
                nativeAdState = ad
                adErrorState = null
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    nativeAdState?.destroy()
                    nativeAdState = null
                    adErrorState = "Failed to load ad: ${loadAdError.message}"
                    Log.e("NativeAdComposable", "Native ad failed to load: ${loadAdError.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    DisposableEffect(Unit) {
        onDispose {
            nativeAdState?.destroy()
            nativeAdState = null
        }
    }

    nativeAdState?.let { ad ->
        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { ctx ->
                val nativeAdView = NativeAdView(ctx)

                // Create views for the native ad components
                val headlineTextView = TextView(ctx).apply { id = R.id.ad_headline  }
                val mediaView = MediaView(ctx).apply { id = R.id.ad_media  }
                val bodyTextView = TextView(ctx).apply { id = R.id.ad_body  }
                val callToActionButton = Button(ctx).apply { id = R.id.ad_call_to_action  }
                val adIconImageView = ImageView(ctx).apply { id = R.id.ad_icon  }

                // Create a layout to hold the ad views
                val adContentLayout = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    // ... add all your views to this layout ...
                }
                nativeAdView.addView(adContentLayout)

                // Register views with the NativeAdView
                nativeAdView.headlineView = headlineTextView
                nativeAdView.mediaView = mediaView
                nativeAdView.bodyView = bodyTextView
                nativeAdView.callToActionView = callToActionButton
                nativeAdView.iconView = adIconImageView

                nativeAdView
            },
            update = { adView ->
                adView.setNativeAd(ad)
                (adView.headlineView as? TextView)?.text = ad.headline
                (adView.bodyView as? TextView)?.text = ad.body
                (adView.callToActionView as? Button)?.text = ad.callToAction
                ad.icon?.drawable?.let {
                    (adView.iconView as? ImageView)?.setImageDrawable(it)
                    adView.iconView?.visibility = View.VISIBLE
                } ?: run {
                    adView.iconView?.visibility = View.GONE
                }
                ad.mediaContent?.let { adView.mediaView?.setMediaContent(it) }
            }
        )
    } ?: run {
        // Optional: Show a placeholder or loading indicator
    }
}