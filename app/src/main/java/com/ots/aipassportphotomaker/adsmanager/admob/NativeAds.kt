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
fun NativeAdComposable1(
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
                val headlineTextView = TextView(ctx).apply { id = R.id.ad_headline }
                val mediaView = MediaView(ctx).apply { id = R.id.ad_media }
                val bodyTextView = TextView(ctx).apply { id = R.id.ad_body }
                val callToActionButton = Button(ctx).apply { id = R.id.ad_call_to_action }
                val adIconImageView = ImageView(ctx).apply { id = R.id.ad_icon }

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

@Composable
fun NativeAdComposable(
    modifier: Modifier = Modifier,
    adUnitId: String
) {
    val context = LocalContext.current
    var nativeAdState by remember { mutableStateOf<NativeAd?>(null) }
    var adErrorState by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(adUnitId) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad: NativeAd ->
                nativeAdState?.destroy()
                nativeAdState = ad
                adErrorState = null
                Log.d("NativeAdComposable", "Native ad loaded successfully")
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
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { ctx ->
                    val nativeAdView = NativeAdView(ctx)

                    // Create views for the native ad components
                    val headlineTextView = TextView(ctx).apply {
                        textSize = 18f
                        setTextColor(android.graphics.Color.BLACK)
                        id = android.R.id.text1 // Unique ID for headline
                    }
                    val mediaView =
                        MediaView(ctx).apply { id = android.R.id.icon } // Unique ID for media
                    val bodyTextView = TextView(ctx).apply {
                        textSize = 14f
                        setTextColor(android.graphics.Color.GRAY)
                        id = android.R.id.text2 // Unique ID for body
                    }
                    val callToActionButton = Button(ctx).apply {
                        setBackgroundColor(android.graphics.Color.parseColor("#6200EE"))
                        setTextColor(android.graphics.Color.WHITE)
                        id = android.R.id.button1 // Unique ID for CTA
                    }
                    val adIconImageView = ImageView(ctx).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        id = android.R.id.icon2 // Unique ID for icon
                    }

                    // Create a layout to hold the ad views
                    val adContentLayout = LinearLayout(ctx).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(16, 16, 16, 16)

                        // Add media view at the top
                        addView(
                            mediaView, LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                200 // Fixed height for media, adjust as needed
                            )
                        )

                        // Headline
                        addView(
                            headlineTextView, LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply { topMargin = 8 })

                        // Body text
                        addView(
                            bodyTextView, LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply { topMargin = 4 })

                        // Icon and CTA button in a horizontal layout
                        val bottomLayout = LinearLayout(ctx).apply {
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER_VERTICAL

                            // Icon
                            addView(
                                adIconImageView, LinearLayout.LayoutParams(
                                    40, 40 // Fixed size for icon
                                ).apply { marginEnd = 8 })

                            // CTA button
                            addView(
                                callToActionButton, LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                            )
                        }
                        addView(
                            bottomLayout, LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply { topMargin = 8 })
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
                    (adView.headlineView as? TextView)?.text = ad.headline ?: "Advertisement"
                    (adView.bodyView as? TextView)?.text = ad.body ?: ""
                    (adView.callToActionView as? Button)?.text = ad.callToAction ?: "Learn More"
                    ad.icon?.drawable?.let {
                        (adView.iconView as? ImageView)?.setImageDrawable(it)
                        adView.iconView?.visibility = View.VISIBLE
                    } ?: run {
                        adView.iconView?.visibility = View.GONE
                    }
                    ad.mediaContent?.let { adView.mediaView?.setMediaContent(it) }
                }
            )
        }
    } ?: run {
        // Optional: Show a placeholder or loading indicator
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.LightGray, RoundedCornerShape(12.dp))
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading ad...", color = Color.Gray)
        }
    }

    adErrorState?.let {
        Log.e("NativeAdComposable", it)
        // Optionally show an error UI
    }
}

@Composable
fun CallNativeAd(nativeAd: NativeAd) {
    NativeAdView(ad = nativeAd) { ad, view ->
        LoadAdContent(ad, view)
    }
}

@Composable
fun NativeAdView(
    ad: NativeAd,
    adContent: @Composable (ad: NativeAd, contentView: View) -> Unit,
) {
    val rootViewId by remember { mutableIntStateOf(View.generateViewId()) } // ID for LinearLayout
    val adViewId by remember { mutableIntStateOf(View.generateViewId()) }
    val mediaViewId by remember { mutableIntStateOf(View.generateViewId()) }
    val composeViewId by remember { mutableIntStateOf(View.generateViewId()) } // Separate ID for ComposeView

    AndroidView(
        factory = { context ->
            val linearLayout = LinearLayout(context).apply {
                id = rootViewId
                orientation = LinearLayout.VERTICAL
                setPadding(0, 0, 0, 0) // Optional: Adjust padding
                minimumHeight = 300
            }

            val nativeMediaView = MediaView(context).apply {
                id = mediaViewId
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    height = 300 // Minimum height for video (adjustable)
                    width = LinearLayout.LayoutParams.MATCH_PARENT
                }
            }
            linearLayout.addView(nativeMediaView)

            val composeView = ComposeView(context).apply {
                id = composeViewId
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setContent { adContent(ad, this) }
            }
            linearLayout.addView(composeView)

            NativeAdView(context).apply {
                id = adViewId
                addView(linearLayout)
                mediaView = mediaView
            }
        },
        update = { view ->
            val adView = view.findViewById<NativeAdView>(adViewId)
            val linearLayout =
                view.findViewById<LinearLayout>(rootViewId) // Use rootViewId for LinearLayout
            val mediaView = linearLayout?.findViewById<MediaView>(mediaViewId)
            val composeView = linearLayout?.findViewById<ComposeView>(composeViewId)

            if (mediaView != null && composeView != null) {
                adView.setNativeAd(ad)
                adView.mediaView = mediaView
                adView.callToActionView = composeView // Use composeView as the CTA view
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LoadAdContent(nativeAd: NativeAd?, composeView: View) {

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(CardDefaults.shape)
            .combinedClickable {
                composeView.performClick()
            },
        colors = CardDefaults.cardColors(containerColor = colors.custom400)
    ) {
        nativeAd?.let {
            Column {
                Text(
                    text = "Ad",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onCustom400.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .align(Alignment.End)
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val icon: Drawable? = it.icon?.drawable
                    icon?.let { drawable ->
                        Image(
                            painter = rememberAsyncImagePainter(model = drawable),
                            contentDescription = "Ad"/*it.icon?.contentDescription*/,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(52.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = it.headline ?: "Headline",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onCustom400,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )

                }

                Text(
                    text = it.headline ?: "Body fadfadf adafasdfa sasdf",
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
                    it.mediaContent?.let { mediaContent ->
                        AndroidView(
                            factory = { context ->
                                MediaView(context).apply {
                                    setMediaContent(mediaContent)
                                }
                            }
                        )
                    } ?: run {
                        Box(
                            modifier = Modifier
                                .background(Color.LightGray)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No media available", color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                it.callToAction?.let { cta ->
                    androidx.compose.material3.Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(colors.nativeAdButtonColor),
                        onClick = {
                            composeView.performClick()
                        },
                        content = {
                            Text(
                                text = cta.uppercase(),
                                color = colors.onSecondary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    )
                }
            }
        } ?: run {
            // Placeholder for loading state or error state
            Text("Loading ad...")
        }
    }
}

fun loadNativeAd(context: Context, adUnitId: String, callback: (NativeAd?) -> Unit) {
    val builder = AdLoader.Builder(context, adUnitId)
        .forNativeAd { nativeAd ->
            callback(nativeAd)
        }

    val adLoader = builder
        .withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                callback(null)
            }
        })
        .withNativeAdOptions(NativeAdOptions.Builder().build())
        .build()

    adLoader.loadAd(AdRequest.Builder().build())
}


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
fun AdCard(nativeAd: NativeAd?) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .animateContentSize()
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = nativeAd?.icon?.uri,
                contentDescription = "Ad Icon",
                placeholder = painterResource(id = R.drawable.gallery_icon),
                error = painterResource(id = R.drawable.gallery_icon),
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Fit
            )

            Column(modifier = Modifier.weight(1f)) {
                nativeAd?.headline?.let {
                    Text(
                        text = it, fontWeight = FontWeight.Bold, fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFDCE775), shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Ad",
                            color = Color(0xFF33691E),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row {
                        nativeAd?.starRating?.let {
                            repeat(it.toInt()) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            androidx.compose.material3.Button(
                onClick = { /* Handle install click */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                modifier = Modifier.height(40.dp)
            ) {
                nativeAd?.callToAction?.let {
                    Text(
                        text = it, color = Color.White, fontWeight = FontWeight.Bold
                    )
                }
            }
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
//                NativeAdLayoutWithoutMedia(nativeAd!!)
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
fun OnboardingAdLayout(nativeAd: NativeAd?) {

    var adViewState by remember { mutableStateOf<NativeAdView?>(null) }

    Row {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp)
                .weight(1f)
                .height(130.dp) // Adjust height as needed
        ) {
            nativeAd?.mediaContent?.let { mediaContent ->

                AndroidView(
                    modifier = Modifier
                        .fillMaxSize(),
                    factory = { context ->
                        val nativeAdView = NativeAdView(context)
                        val mediaView = MediaView(context).apply {
                            id = View.generateViewId()
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                200 // or any fixed height
                            )
                        }

                        nativeAdView.addView(
                            mediaView, LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
                        )
                        nativeAdView
                    },
                    update = { adView ->
                        adViewState = adView
                        adView.setNativeAd(nativeAd)
                        nativeAd.mediaContent?.let { adView.mediaView?.mediaContent = it }
                    }
                )

            } ?: run {
                Box(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No media available", color = Color.Gray)
                }
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
                        color = colors.onCustom100,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                nativeAd?.headline?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onCustom400,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }

            }

            nativeAd?.body?.let {

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

            androidx.compose.material3.Button(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(colors.nativeAdButtonColor),
                onClick = {
                    adViewState?.performClick()
                },
                content = {
                    nativeAd?.callToAction?.let {
                        Text(
                            text = it,
                            color = colors.onSecondary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ImageProcessingAdLayout(nativeAd: NativeAd?) {

    var adViewState by remember { mutableStateOf<NativeAdView?>(null) }

    Column {

        Row {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
                    .weight(1f)
                    .height(130.dp) // Adjust height as needed
            ) {
                nativeAd?.mediaContent?.let { mediaContent ->

                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize(),
                        factory = { context ->
                            val nativeAdView = NativeAdView(context)
                            val mediaView = MediaView(context)

                            nativeAdView.addView(
                                mediaView, LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT
                                )
                            )
                            nativeAdView
                        },
                        update = { adView ->
                            adViewState = adView
                            adView.setNativeAd(nativeAd)
                            nativeAd.mediaContent?.let { adView.mediaView?.mediaContent = it }
                        }
                    )

                } ?: run {
                    Box(
                        modifier = Modifier
                            .background(Color.LightGray)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No media available", color = Color.Gray)
                    }
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
                            color = colors.onCustom100,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    nativeAd?.headline?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onCustom400,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }

                }

                nativeAd?.body?.let {

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
        }

        androidx.compose.material3.Button(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(colors.nativeAdButtonColor),
            onClick = {
                adViewState?.performClick()
            },
            content = {
                nativeAd?.callToAction?.let {
                    Text(
                        text = it,
                        color = colors.onSecondary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        )
    }
}

@Composable
fun NativeAdLayoutWithoutMedia(nativeAd: NativeAd) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(colors.custom400, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .height(150.dp)
    ) {
        // Call the NativeAdView composable to display the native ad.
        com.ots.aipassportphotomaker.adsmanager.admob.views.NativeAdView(nativeAd) {
            // Inside the NativeAdView composable, display the native ad assets.
            Column(
                Modifier
                    .align(Alignment.TopStart)
                    .wrapContentHeight(Alignment.Top)
            ) {
                // Display the ad attribution.
                NativeAdAttribution(
                    text = context.getString(R.string.attribution),
                    modifier = Modifier
                        .background(color = colors.custom100, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    nativeAd.icon?.let { icon ->
                        NativeAdIconView(
                            Modifier
                                .size(64.dp)
                                .padding(5.dp)
                        ) {
                            icon.drawable?.toBitmap()?.let { bitmap ->
                                Image(bitmap = bitmap.asImageBitmap(), "Icon")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(
                        Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
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
fun LanguageAdLayoutView(nativeAd: NativeAd) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(colors.custom400, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .height(230.dp)
    ) {
        // Call the NativeAdView composable to display the native ad.
//        com.ots.aipassportphotomaker.adsmanager.admob.views.NativeAdView(nativeAd) {
            // Inside the NativeAdView composable, display the native ad assets.
            Column(
                Modifier
                    .align(Alignment.TopStart)
                    .wrapContentHeight(Alignment.Top)
            ) {
                // Display the ad attribution.
                NativeAdAttribution(
                    text = context.getString(R.string.attribution),
                    modifier = Modifier
                        .background(color = colors.custom100, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
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
//        }
    }
}

@Composable
fun OnboardingAdLayoutView(nativeAd: NativeAd) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(colors.custom400, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .height(180.dp)
    ) {
        // Call the NativeAdView composable to display the native ad.
        com.ots.aipassportphotomaker.adsmanager.admob.views.NativeAdView(nativeAd) {
            // Inside the NativeAdView composable, display the native ad assets.
            Column(
                Modifier
                    .align(Alignment.TopStart)
                    .wrapContentHeight(Alignment.Top)
            ) {
                // Display the ad attribution.
                NativeAdAttribution(
                    text = context.getString(R.string.attribution),
                    modifier = Modifier
                        .background(color = colors.custom100, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
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