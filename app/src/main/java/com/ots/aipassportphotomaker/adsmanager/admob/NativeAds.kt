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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom100
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400
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
                    val mediaView = MediaView(ctx).apply { id = android.R.id.icon } // Unique ID for media
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
                        addView(mediaView, LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            200 // Fixed height for media, adjust as needed
                        ))

                        // Headline
                        addView(headlineTextView, LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { topMargin = 8 })

                        // Body text
                        addView(bodyTextView, LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { topMargin = 4 })

                        // Icon and CTA button in a horizontal layout
                        val bottomLayout = LinearLayout(ctx).apply {
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER_VERTICAL

                            // Icon
                            addView(adIconImageView, LinearLayout.LayoutParams(
                                40, 40 // Fixed size for icon
                            ).apply { marginEnd = 8 })

                            // CTA button
                            addView(callToActionButton, LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ))
                        }
                        addView(bottomLayout, LinearLayout.LayoutParams(
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
            val linearLayout = view.findViewById<LinearLayout>(rootViewId) // Use rootViewId for LinearLayout
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
                        colors = ButtonDefaults.buttonColors(colors.primary),
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
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(CardDefaults.shape),
        colors = CardDefaults.cardColors(containerColor = colors.custom400)
    ) {
        Column {
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
                        colors = ButtonDefaults.buttonColors(colors.primary),
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
                    colors = ButtonDefaults.buttonColors(colors.primary),
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
//        AdPreviewLarge()
        AdPreviewSmall()
    }
}

/*
@Composable
fun NativeAdComposable(
    modifier: Modifier = Modifier,
    adUnitId: String
) {
    val context = LocalContext.current
    var nativeAdState by remember { mutableStateOf<NativeAd?>(null) }
    var adErrorState by remember { mutableStateOf<String?>(null) } // For displaying errors
    val density = LocalDensity.current

    LaunchedEffect(adUnitId) {
        Log.d("NativeAdComposable", "LaunchedEffect: Loading ad for $adUnitId")
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad: NativeAd ->
                nativeAdState?.destroy() // Destroy previous ad if any
                nativeAdState = ad
                adErrorState = null
                Log.d(
                    "NativeAdComposable",
                    "Ad loaded. Headline: '${ad.headline}', Body: '${ad.body}'"
                )
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    nativeAdState?.destroy()
                    nativeAdState = null
                    adErrorState =
                        "Failed to load ad: ${loadAdError.message} (Code: ${loadAdError.code})"
                    Log.e("NativeAdComposable", "Native ad failed to load: ${loadAdError.message}")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.d("NativeAdComposable", "Ad clicked")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    Log.d("NativeAdComposable", "Ad impression")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("NativeAdComposable", "DisposableEffect: Destroying ad: ${nativeAdState != null}")
            nativeAdState?.destroy()
            nativeAdState = null
        }
    }

    if (adErrorState != null) {
        // Optionally display an error message in your UI if the ad fails to load
        // Text(adErrorState!!, color = ComposeColor.Red, modifier = modifier)
        Log.e("NativeAdComposable", "Not rendering AdView due to error: $adErrorState")
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground


    nativeAdState?.let { ad ->
        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { ctx ->
                Log.d("NativeAdComposable", "Factory: Creating views")
                val nativeAdView = NativeAdView(ctx)


                val headlineTextView = TextView(ctx).apply {
                    id = R.id.ad_headline // Assign an ID (optional but good practice)
                    textSize = 18f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(onBackgroundColor.toArgb())
                    val padding = with(density) { 5.dp.toPx().toInt() }
                    setPadding(
                        padding,
                        padding,
                        padding,
                        padding
                    )
                    Log.d("NativeAdViewFactory", "Headline TextView created")
                }
                val mediaView = MediaView(ctx).apply {
                    id = R.id.ad_media // Assign an ID
                    setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    Log.d("NativeAdViewFactory", "MediaView created")
                }
                val bodyTextView = TextView(ctx).apply {
                    id = R.id.ad_body // Assign an ID
                    textSize = 14f
                    setTextColor(onBackgroundColor.toArgb())
                    val padding = with(density) { 5.dp.toPx().toInt() }
                    setPadding(
                        padding,
                        padding,
                        padding,
                        padding
                    )
                    Log.d("NativeAdViewFactory", "Body TextView created")
                }
                val callToActionButton = Button(ctx).apply {
                    id = R.id.ad_call_to_action // Assign an ID
                    textSize = 16f
                    setBackgroundResource(R.drawable.btn_bg)
                    setTextColor(backgroundColor.toArgb())

                    Log.d("NativeAdViewFactory", "CTA Button created")
                }
                val advertiserTextView = TextView(ctx).apply {
                    // Style it appropriately
                    textSize = 12f
                    maxLines = 1
                    setTextColor(Color.Black.toArgb())
                    setBackgroundResource(R.drawable.native_ad_background)
                    val padding = with(density) { 5.dp.toPx().toInt() }
                    setPadding(
                        padding,
                        padding,
                        padding,
                        padding
                    )
                    Log.d("NativeAdViewFactory", "Advertiser TextView created")
                }
                val adIconImageView = ImageView(ctx).apply {
                    id = R.id.ad_icon // If you have ids.xml
                    // Basic styling, adjust as needed
                    layoutParams = ViewGroup.LayoutParams(
                        with(density) { 48.dp.toPx().toInt() },
                        with(density) { 48.dp.toPx().toInt() }
                    )
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
                val cardView = CardView(ctx).apply  {
                    addView(
                        mediaView, FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    )
                    addView(
                        advertiserTextView, FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            gravity = Gravity.TOP or Gravity.END
                            marginEnd = with(density) { 3.dp.toPx().toInt() }
                            topMargin = with(density) { 3.dp.toPx().toInt() }
                        }
                    )
                    radius = with(density) { 20.dp.toPx() }
                }

                val headlineRow = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    gravity = Gravity.CENTER_VERTICAL // Align icon and headline text vertically
                    addView(adIconImageView, LinearLayout.LayoutParams(
                        with(density) { 40.dp.toPx().toInt() }, // Icon size
                        with(density) { 40.dp.toPx().toInt() }
                    ).apply {
                        marginStart = with(density) { 8.dp.toPx().toInt() }
                        rightMargin = with(density) { 8.dp.toPx().toInt() }
                    })
                    addView(headlineTextView, LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, // Let headline wrap
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply{
                        weight = 1f // If you want headline to take remaining space
                    })
                }

                val adContentLayout = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    // Add views in order
                    addView(
                        cardView, LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            with(density) { 0.dp.toPx().toInt() }
                        ).apply {
                            val basicMargin = with(density) { 4.dp.toPx().toInt() }
                            topMargin = basicMargin
                            marginStart = basicMargin
                            marginEnd = basicMargin
                            bottomMargin = with(density) { 8.dp.toPx().toInt() }
                            weight = 1f
                        }
                    )

                    addView(
                        headlineRow, LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = with(density) { 4.dp.toPx().toInt() } })




                    addView(
                        bodyTextView, LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = with(density) { 8.dp.toPx().toInt() } })

                    addView(
                        callToActionButton, LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            val vertical = with(density) { 4.dp.toPx().toInt() }
                            val horizontal = with(density) { 10.dp.toPx().toInt() }
                            topMargin = vertical
                            marginStart = horizontal
                            marginEnd = horizontal
                            bottomMargin = horizontal
                        })
                    Log.d("NativeAdViewFactory", "adContentLayout populated")
                }
                nativeAdView.addView(adContentLayout)

                // IMPORTANT: Assign views to NativeAdView properties
                nativeAdView.headlineView = headlineTextView
                nativeAdView.mediaView = mediaView
                nativeAdView.bodyView = bodyTextView
                nativeAdView.callToActionView = callToActionButton
                nativeAdView.advertiserView = advertiserTextView
                nativeAdView.iconView = adIconImageView
                // If you add other views like iconView, starRatingView, storeView, advertiserView,
                // make sure to assign them here as well.
                // e.g., nativeAdView.iconView = adIconImageView

                Log.d(
                    "NativeAdComposable",
                    "Factory: Views created and NativeAdView properties assigned."
                )
                nativeAdView
            },
            update = { adView ->
                Log.d("NativeAdComposable", "Update block: Assigning native ad to adView.")
                // The NativeAd object itself handles populating the child views
                // once they have been registered with setHeadlineView, setBodyView, etc.
                adView.setNativeAd(ad)

                // You generally DO NOT need to set text/content manually here if the views
                // are correctly registered and setNativeAd is called.
                // The NativeAd SDK populates these.
                // However, to be absolutely sure what the ad object contains at this point:
                Log.d("NativeAdComposable", "Update - Ad headline: '${ad.headline}'")
                Log.d("NativeAdComposable", "Update - Ad body: '${ad.body}'")
                Log.d("NativeAdComposable", "Update - Ad CTA: '${ad.callToAction}'")
                (adView.headlineView as? TextView)?.let {
                    Log.d(
                        "NativeAdComposable",
                        "Update - HeadlineView text before SDK populates: '${it.text}'"
                    )
                }
                (adView.iconView as? ImageView)?.apply {
                    val icon = ad.icon?.drawable
                    visibility = if (icon != null) {
                        setImageDrawable(icon)
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
                (adView.headlineView as? TextView)?.text = ad.headline // Usually not needed
                (adView.bodyView as? TextView)?.text = ad.body         // Usually not needed
                (adView.callToActionView as? Button)?.text = ad.callToAction // Usually not needed
                (adView.advertiserView as? TextView)?.text =
                    if (ad.advertiser.isNullOrEmpty()) "AD" else ad.advertiser
                ad.mediaContent?.let { mediaContent ->
                    adView.mediaView?.let { mv ->
                        if (mv.mediaContent != mediaContent) { // Avoid redundant calls
                            mv.mediaContent = mediaContent
                            Log.d("NativeAdComposable", "Update: Media content set.")
                        }
                    }
                } ?: Log.d("NativeAdComposable", "Update: Ad has no media content.")
                Log.d("NativeAdComposable", "Update block: setNativeAd(ad) called on adView.")
            }
        )
    } ?: run {
        // This block runs when nativeAdState is null (ad not loaded or error)
        // You can put a placeholder Composable here if you want.
        if (adErrorState == null) {
            Log.d("NativeAdComposable", "nativeAdState is null, ad still loading or not requested.")
            // Optional: Show a shimmer or loading indicator
            // Box(modifier = modifier.fillMaxWidth().height(250.dp).background(ComposeColor.LightGray)) {
            //     CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            // }
        }
    }
}
* */



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
fun NativeAdViewCompose(context: Context, nativeID: String, onAdLoaded: (Boolean) -> Unit = {}) {

    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    DisposableEffect(Unit) {

        val adLoader = AdLoader.Builder(context, nativeID)
            .forNativeAd { ad: NativeAd ->
                // Show the ad.
                nativeAd = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdClicked() {
                    Logger.d("MyAdsManager", "Ad clicked")
                }
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Logger.d("MyAdsManager", "Ad failed to load: ${loadAdError.message}")
                    onAdLoaded.invoke(false)
                }
                override fun onAdImpression() {
                    Logger.d("MyAdsManager", "Ad impression")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
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
        AdViewSmall(nativeAd)
    } else {
        Text("Loading Ad... ")
    }

}

@Composable
fun AdViewSmall(nativeAd: NativeAd?) {

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
                    /*AndroidView(
                        factory = { context ->
                            MediaView(context).apply {
                                setMediaContent(mediaContent)
                            }
                        }
                    )*/
                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize(),
                        factory = { context ->
                            val nativeAdView = NativeAdView(context)
                            val mediaView = MediaView(context)
                            nativeAdView.mediaView = mediaView
                            nativeAdView.addView(mediaView, LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            ))
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
                    colors = ButtonDefaults.buttonColors(colors.primary),
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

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NativeAdPreview1() {
    val context = LocalContext.current
    val nativeID = "ca-app-pub-3940256099942544/2247696110" // Sample AdMob native ad unit ID
    PreviewContainer {
        NativeAdViewCompose(
            context = context,
            nativeID = nativeID
        )
    }
}