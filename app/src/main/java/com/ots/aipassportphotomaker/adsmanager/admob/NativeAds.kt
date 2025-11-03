package com.ots.aipassportphotomaker.adsmanager.admob

// Created by amanullah on 20/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.adtype.NativeAdType
import com.ots.aipassportphotomaker.adsmanager.admob.views.NativeAdAttribution
import com.ots.aipassportphotomaker.adsmanager.admob.views.NativeAdBodyView
import com.ots.aipassportphotomaker.adsmanager.admob.views.NativeAdButton
import com.ots.aipassportphotomaker.adsmanager.admob.views.NativeAdCallToActionView
import com.ots.aipassportphotomaker.adsmanager.admob.views.NativeAdHeadlineView
import com.ots.aipassportphotomaker.adsmanager.admob.views.NativeAdIconView
import com.ots.aipassportphotomaker.adsmanager.admob.views.NativeAdMediaView
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom100
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400
import com.ots.aipassportphotomaker.presentation.ui.theme.nativeAdButtonColor
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom100
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400

@Composable
fun AdPreviewLarge() {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .animateContentSize()
    ) {
        Text(
            text = "Ad",
            style = MaterialTheme.typography.labelSmall,
            color = colors.onCustom400.copy(alpha = 0.6f),
            modifier = Modifier
                .padding(bottom = 4.dp)
                .align(Alignment.Start)
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.pinterest_icon),
                contentDescription = "Ad"/*it.icon?.contentDescription*/,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(52.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Headline",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onCustom400,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )

        }

        Text(
            text = "Body fadfadf adafasdfa sasdf",
            style = MaterialTheme.typography.titleSmall,
            color = colors.onCustom400,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp) // Adjust height as needed
        ) {
            Box(
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No media available", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))


        androidx.compose.material3.Button(
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(colors.nativeAdButtonColor),
            onClick = {

            },
            content = {
                Text(
                    text = "Install",
                    color = colors.onSecondary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}

@Composable
fun AdPreviewSmall() {

    Row {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp)
                .weight(1f)
                .height(130.dp) // Adjust height as needed
        ) {
            Box(
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No media available", color = Color.Gray)
            }
        }

        Spacer(
            modifier = Modifier
                .width(4.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 2.dp)
        ) {

            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(color = colors.custom100, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Ad",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Headline",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onCustom100,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )

            }

            Text(
                text = "Body fadfadf adafasdfa sasdf dafadf dafdfa afdasd",
                style = MaterialTheme.typography.titleSmall,
                color = colors.onCustom400,
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            androidx.compose.material3.Button(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(colors.nativeAdButtonColor),
                onClick = {

                },
                content = {
                    Text(
                        text = "Install",
                        color = colors.onSecondary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NativeAdPreview() {
    PreviewContainer {
        Column {
            AdPreviewLarge()
            AdPreviewSmall()
        }

    }
}

@Composable
fun NativeAdViewCompose(
    context: Context,
    adType: NativeAdType,
    nativeID: String,
    onAdLoaded: (Boolean) -> Unit = {}
) {

    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    DisposableEffect(Unit) {

        val adLoader = AdLoader.Builder(context, nativeID)
            .forNativeAd { ad: NativeAd ->
                // Show the ad.
                nativeAd = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdClicked() {
                    Logger.d("MyAdsManager", "Native Ad clicked")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Logger.d(
                        "MyAdsManager",
                        "Native Ad(${adType.name}) failed to load: ${loadAdError.message}"
                    )
                    onAdLoaded.invoke(false)
                }

                override fun onAdImpression() {
                    Logger.d("MyAdsManager", "Native Ad impression")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Logger.d("MyAdsManager", "Native Ad loaded from ${adType.name}")
                    onAdLoaded.invoke(true)
                }

            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())

        onDispose {
            nativeAd?.destroy()
        }
    }
    if (nativeAd != null) {
        when (adType) {
            NativeAdType.NATIVE_AD_LANGUAGE -> {
                LanguageAdLayoutView(nativeAd!!)
            }

            NativeAdType.NATIVE_AD_ONBOARDING -> {
                OnboardingAdLayoutView(nativeAd!!)
            }

            NativeAdType.NATIVE_AD_IMAGE_PROCESSING -> {
                LanguageAdLayoutView(nativeAd!!)
            }

            NativeAdType.NATIVE_AD_FINALE_SCREEN -> {

                OnboardingAdLayoutView(nativeAd!!)
            }

            else -> {
                OnboardingAdLayoutView(nativeAd!!)

            }
        }
    } else {
//        Text("Loading Ad... ")
    }

}

@Composable
fun LanguageAdLayoutView(nativeAd: NativeAd) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(colors.custom400, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .height(190.dp)
    ) {
        // Call the NativeAdView composable to display the native ad.
        com.ots.aipassportphotomaker.adsmanager.admob.views.NativeAdView(nativeAd) {
            // Inside the NativeAdView composable, display the native ad assets.
            Column(
                Modifier
                    .align(Alignment.TopStart)
                    .wrapContentHeight(Alignment.Top)
            ) {

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Display the media asset.
                    NativeAdMediaView(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(120.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(
                        Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Row {
                            // If available, display the icon asset.
                            nativeAd.icon?.let { icon ->
                                NativeAdIconView(
                                    Modifier
                                        .size(48.dp)
                                        .padding(5.dp)
                                ) {
                                    icon.drawable?.toBitmap()?.let { bitmap ->
                                        Image(bitmap = bitmap.asImageBitmap(), "Icon")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Column {
                                // If available, display the headline asset.
                                nativeAd.headline?.let {
                                    NativeAdHeadlineView {
                                        Text(
                                            text = it, style = MaterialTheme.typography.titleMedium,
                                            color = colors.onCustom400,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .align(Alignment.Start)
                                        )
                                    }
                                }

                                // Display the ad attribution.
                                NativeAdAttribution(
                                    text = context.getString(R.string.attribution),
                                    modifier = Modifier
                                        .background(color = colors.custom100, shape = RoundedCornerShape(8.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // If available, display the body asset.
                        nativeAd.body?.let {
                            NativeAdBodyView {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = colors.onCustom400,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        // If available, display the star rating asset.
                        /*nativeAd.starRating?.let {
                            NativeAdStarRatingView {
                                Text(
                                    text = "Rated $it",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }*/

                    }

                }

                /*Row(
                    Modifier
                        .align(Alignment.End)
                        .padding(5.dp)
                ) {
                    // If available, display the price asset.
                    nativeAd.price?.let {
                        NativeAdPriceView(
                            Modifier
                                .padding(5.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(text = it)
                        }
                    }
                    // If available, display the store asset.
                    nativeAd.store?.let {
                        NativeAdStoreView(
                            Modifier
                                .padding(5.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(text = it)
                        }
                    }
                }*/

                // If available, display the call to action asset.
                // Note: The Jetpack Compose button implements a click handler which overrides the native
                // ad click handler, causing issues. Use the NativeAdButton which does not implement a
                // click handler. To handle native ad clicks, use the NativeAd AdListener onAdClicked
                // callback.
                nativeAd.callToAction?.let { callToAction ->
                    NativeAdCallToActionView(
                        Modifier
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(colors.nativeAdButtonColor, shape = RoundedCornerShape(32.dp)),

                        ) {
                        NativeAdButton(
                            modifier = Modifier
                                .fillMaxSize()
                                .height(52.dp)
                                .clip(shape = RoundedCornerShape(32.dp))
                                .align(Alignment.CenterHorizontally),
                            text = callToAction
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingAdLayoutView(nativeAd: NativeAd) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(colors.custom400, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .height(170.dp)
    ) {
        // Call the NativeAdView composable to display the native ad.
        com.ots.aipassportphotomaker.adsmanager.admob.views.NativeAdView(nativeAd) {
            // Inside the NativeAdView composable, display the native ad assets.
            Column(
                Modifier
                    .align(Alignment.TopStart)
                    .wrapContentHeight(Alignment.Top)
            ) {

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Display the media asset.
                    NativeAdMediaView(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(120.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(
                        Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Row {
                            // If available, display the icon asset.
                            nativeAd.icon?.let { icon ->
                                NativeAdIconView(
                                    Modifier
                                        .size(48.dp)
                                        .padding(5.dp)
                                ) {
                                    icon.drawable?.toBitmap()?.let { bitmap ->
                                        Image(bitmap = bitmap.asImageBitmap(), "Icon")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Column {
                                // If available, display the headline asset.
                                nativeAd.headline?.let {
                                    NativeAdHeadlineView {
                                        Text(
                                            text = it, style = MaterialTheme.typography.titleMedium,
                                            color = colors.onCustom400,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                        )
                                    }
                                }
                                // Display the ad attribution.
                                NativeAdAttribution(
                                    text = context.getString(R.string.attribution),
                                    modifier = Modifier
                                        .background(color = colors.custom100, shape = RoundedCornerShape(8.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }

                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // If available, display the body asset.
                        nativeAd.body?.let {
                            NativeAdBodyView {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = colors.onCustom400,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        // If available, display the star rating asset.
                        /*nativeAd.starRating?.let {
                            NativeAdStarRatingView {
                                Text(
                                    text = "Rated $it",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }*/

                        // If available, display the call to action asset.
                        // Note: The Jetpack Compose button implements a click handler which overrides the native
                        // ad click handler, causing issues. Use the NativeAdButton which does not implement a
                        // click handler. To handle native ad clicks, use the NativeAd AdListener onAdClicked
                        // callback.
                        nativeAd.callToAction?.let { callToAction ->
                            NativeAdCallToActionView(
                                Modifier
                                    .padding(horizontal = 4.dp, vertical = 4.dp)
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .background(colors.nativeAdButtonColor, shape = RoundedCornerShape(32.dp)),

                                ) {
                                NativeAdButton(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .height(50.dp)
                                        .clip(shape = RoundedCornerShape(32.dp))
                                        .align(Alignment.CenterHorizontally),
                                    text = callToAction
                                )
                            }
                        }

                    }

                }

                /*Row(
                    Modifier
                        .align(Alignment.End)
                        .padding(5.dp)
                ) {
                    // If available, display the price asset.
                    nativeAd.price?.let {
                        NativeAdPriceView(
                            Modifier
                                .padding(5.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(text = it)
                        }
                    }
                    // If available, display the store asset.
                    nativeAd.store?.let {
                        NativeAdStoreView(
                            Modifier
                                .padding(5.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(text = it)
                        }
                    }
                }*/
            }
        }
    }
}


@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ImageProcessingAdLayoutPreview() {

    Column {

        Row {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
                    .weight(1f)
                    .height(130.dp) // Adjust height as needed
            ) {

                Box(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No media available", color = Color.Gray)
                }
            }

            Spacer(
                modifier = Modifier
                    .width(4.dp)
            )

            Column(
                modifier = Modifier
                    .padding(end = 2.dp)
                    .weight(1f)
            ) {

                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = colors.custom100, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                            .align(Alignment.CenterVertically),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Ad",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onCustom100,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "it",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onCustom400,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )

                }


                Text(
                    text = "afda",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.onCustom400,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        androidx.compose.material3.Button(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(colors.nativeAdButtonColor),
            onClick = {

            },
            content = {
                Text(
                    text = "it",
                    color = colors.onSecondary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}


@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LanguageAdLayoutPreview() {

    Column {

        Row {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
                    .weight(1f)
                    .height(130.dp) // Adjust height as needed
            ) {

                Box(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No media available", color = Color.Gray)
                }
            }

            Spacer(
                modifier = Modifier
                    .width(4.dp)
            )

            Column(
                modifier = Modifier
                    .padding(end = 2.dp)
                    .weight(1f)
            ) {

                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = colors.custom100, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                            .align(Alignment.CenterVertically),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Ad",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onCustom100,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "it",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onCustom400,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )

                }


                Text(
                    text = "afda",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.onCustom400,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        androidx.compose.material3.Button(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(colors.nativeAdButtonColor),
            onClick = {

            },
            content = {
                Text(
                    text = "it",
                    color = colors.onSecondary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NativeAdPreview1() {
    val context = LocalContext.current
    val nativeID = "ca-app-pub-3940256099942544/2247696110" // Sample AdMob native ad unit ID
    PreviewContainer {
        NativeAdViewCompose(
            context = context,
            adType = NativeAdType.NATIVE_AD_ONBOARDING,
            nativeID = nativeID
        )
    }
}