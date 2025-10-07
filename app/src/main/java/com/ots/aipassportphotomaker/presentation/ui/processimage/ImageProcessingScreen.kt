package com.ots.aipassportphotomaker.presentation.ui.processimage

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdSize
import com.ots.aipassportphotomaker.adsmanager.admob.AdMobCollapsableBanner
import com.ots.aipassportphotomaker.adsmanager.admob.CollapseDirection
import com.ots.aipassportphotomaker.adsmanager.admob.NativeAdViewCompose
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.common.ext.animatedBorder
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.ProcessingStage
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.ImageWithLottieScan
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import kotlinx.coroutines.delay

// Created by amanullah on 27/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@SuppressLint("ContextCastToActivity")
@Composable
fun ImageProcessingPage(
    mainRouter: MainRouter,
    viewModel: ImageProcessingScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "ImageProcessingPage"

    val uiState by viewModel.uiState.collectAsState()

    // Collect the processing stage
    val processingStage by viewModel.processingStage.collectAsState()
    val isPortrait = viewModel.isPortrait
    val imagePath = viewModel.imagePath
    val selectedColor = viewModel.selectedColor

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "PhotoIDDetailPage: Navigation State: $navigationState")
        when (navigationState) {
            is ImageProcessingScreenNavigationState.EditImageScreen -> mainRouter.navigateToEditImageScreen(
                documentId = navigationState.documentId,
                imageUrl = uiState.finalImageUrl,
                documentName = navigationState.documentName,
                documentSize = navigationState.documentSize,
                documentUnit = navigationState.documentUnit,
                documentPixels = navigationState.documentPixels,
                selectedBackgroundColor = uiState.selectedColor,
                editPosition = 0,
                selectedDpi = viewModel.selectedDpi,
                sourceScreen = "ImageProcessingScreen"
            )
        }
    }

    ImageProcessingScreen(
        uiState = uiState,
        processingStage = processingStage,
        isPremium = viewModel.isPremiumUser(),
        isPortrait = isPortrait,
        selectedColor = selectedColor,
        imagePath = imagePath,
        onBackClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "backPress_ImageProcessingScreen")
            mainRouter.goBack()
                      },
        onGetProClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "getPro_ImageProcessingScreen")
            mainRouter.navigateToPremiumScreen()
        }
    )
}

@Composable
private fun ImageProcessingScreen(
    uiState: ImageProcessingScreenUiState,
    processingStage: ProcessingStage = ProcessingStage.NONE,
    isPremium: Boolean = false,
    isPortrait: Boolean = true,
    selectedColor: String? = null,
    imagePath: String? = null,
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
) {
    val TAG = "ImageProcessingScreen"

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    Surface(
        modifier = Modifier
            .padding(bottom = systemBarsPadding.calculateBottomPadding())
    ) {

        val context = LocalContext.current
        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage
        val documentImage = uiState.documentImage
        val backgroundColor = uiState.selectedColor

        if (errorMessage != null) Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

        // Get appropriate message based on processing stage
        val currentMessage = when (processingStage) {
            ProcessingStage.UPLOADING -> "🔄 Uploading Photo..."
            ProcessingStage.PROCESSING -> {
                // Alternate between these messages during processing
                val processingMessages = listOf(
                    "🔮 AI Magic in progress… just a moment!",
                    "🎨 Crafting the best fit for your document!",
                    "☺ Head centered"
                )
                val messageIndex by produceState(initialValue = 0) {
                    while (processingStage == ProcessingStage.PROCESSING) {
                        delay(3000)
                        value = (value + 1) % processingMessages.size
                    }
                }
                processingMessages[messageIndex]
            }

            ProcessingStage.CROPPING_IMAGE -> "💫 Cropping image..."
            ProcessingStage.COMPLETED -> {
                "✅ Process completed successfully"

            }

            ProcessingStage.NONE -> ""
            ProcessingStage.NO_NETWORK_AVAILABLE -> "❌ No network connection"
            ProcessingStage.ERROR -> "❌ Something went wrong"
            ProcessingStage.DOWNLOADING -> "⬇️ Applying Changes..."
            ProcessingStage.SAVING_IMAGE -> "💾 Saving image..."
            ProcessingStage.BACKGROUND_REMOVAL -> "🖼️ Removing background..."
        }

        Column(
            modifier = Modifier
                .background(colors.background)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {

                        })
                }
        ) {
            CommonTopBar(
                title = "Processing",
                showGetProButton = !isPremium,
                onBackClick = {
                    onBackClick.invoke()

                },
                onGetProClick = {
                    onGetProClick.invoke()
                }
            )

            if (isLoading) {
                LoaderFullScreen()
            } else {

                Column(
                    modifier = Modifier
                        .background(colors.background)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {

                                })
                        }
                ) {

                    ImageWithLottieScan(
                        modifier = Modifier
                            .padding(16.dp)
                            .animatedBorder(
                                borderColors = listOf(Color.Red, Color.Green, Color.Blue),
                                backgroundColor = Color.White,
                                shape = RoundedCornerShape(16.dp),
                                borderWidth = 4.dp,
                                animationDurationInMillis = 2500
                            )
                            .align(Alignment.CenterHorizontally),
                        imagePath = uiState.currentImagePath ?: imagePath,
//                        isPortrait = isPortrait,
                        onTap = {}
                    )

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {

                        val colorList = listOf(
                            Color(0xFF60DDAD), // Teal
                            Color(0xFF4285F4), // Blue
                            Color(0xFFDB4437), // Red
                            Color(0xFFF4B400), // Yellow
                            Color(0xFF0F9D58)  // Green
                        )

                        val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")

                        // Animate the progress (0f to 1f) to cycle through colors
                        val colorProgress by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = colorList.size.toFloat(),
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "color progress"
                        )

                        // Calculate the current color based on progress
                        val animatedColor = deriveColorFromProgress(colorList, colorProgress)

                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            text = "Processing Image",
                            color = animatedColor,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .animateContentSize(),
                            text = currentMessage,
                            color = colors.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                }

                Spacer(modifier = Modifier.weight(1f))

                if (!isPremium) {
                    var adLoadState by remember { mutableStateOf(false) }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (!adLoadState) {
                                Text(
                                    text = "Advertisement",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.onSurfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateContentSize()
                                        .wrapContentSize(align = Alignment.Center)
                                )
                            }

                            NativeAdViewCompose(
                                context = context,
                                nativeID = AdIdsFactory.getNativeAdId(),
                                onAdLoaded = {
                                    adLoadState = it
                                }
                            )

                            /*AdMobCollapsableBanner(
                                adUnit = AdIdsFactory.getBannerAdId(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                                    .align(Alignment.Center),
                                adSize = AdSize.LARGE_BANNER, // or adaptive size if needed
                                collapseDirection = CollapseDirection.BOTTOM,
                                onAdLoaded = { isLoaded ->
                                    adLoadState = isLoaded
                                    Logger.d(TAG, "AdMobBanner: onAdLoaded: $isLoaded")
                                }
                            )*/
                        }
                    }
                }


                /*var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

                LaunchedEffect(null) {
                    loadNativeAd(context, AdIdsFactory.getNativeAdId()) {
                        nativeAd = it
                    }
                }

                nativeAd?.let {
                    CallNativeAd(nativeAd = it)
                }*/


                /*NativeAdComposable(
                    modifier = Modifier
                        .fillMaxWidth(),
                    adUnitId = AdIdsFactory.getNativeAdId() // Sample AdMob native ad unit ID
                )*/

            }
        }
    }
}

// Helper function to interpolate between colors based on progress
@Composable
fun deriveColorFromProgress(colors: List<Color>, progress: Float): Color {
    val size = colors.size
    val index = progress % size
    val lowerIndex = index.toInt() % size
    val upperIndex = (lowerIndex + 1) % size
    val fraction = index - index.toInt()

    return lerp(colors[lowerIndex], colors[upperIndex], fraction)
}

// Extension function to lerp between two colors
fun lerp(start: Color, stop: Color, fraction: Float): Color {
    return Color(
        red = lerp(start.red, stop.red, fraction),
        green = lerp(start.green, stop.green, fraction),
        blue = lerp(start.blue, stop.blue, fraction),
        alpha = lerp(start.alpha, stop.alpha, fraction)
    )
}

// Helper function to lerp a single component
private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun ImageProcessingScreenPreview() {

    PreviewContainer {
        ImageProcessingScreen(
            uiState = ImageProcessingScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            onBackClick = {},
            onGetProClick = {}
        )
    }
}