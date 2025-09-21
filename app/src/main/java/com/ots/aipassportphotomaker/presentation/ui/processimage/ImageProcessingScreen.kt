package com.ots.aipassportphotomaker.presentation.ui.processimage

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.nativead.NativeAd
import com.ots.aipassportphotomaker.adsmanager.admob.AdMobBanner
import com.ots.aipassportphotomaker.adsmanager.admob.AdMobCollapsableBanner
import com.ots.aipassportphotomaker.adsmanager.admob.CallNativeAd
import com.ots.aipassportphotomaker.adsmanager.admob.CollapseDirection
import com.ots.aipassportphotomaker.adsmanager.admob.NativeAdComposable
import com.ots.aipassportphotomaker.adsmanager.admob.NativeAdPreview
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.adsmanager.admob.loadNativeAd
import com.ots.aipassportphotomaker.common.ext.animatedBorder
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
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
        isPortrait = isPortrait,
        selectedColor = selectedColor,
        imagePath = imagePath,
        onBackClick = { mainRouter.goBack() },
        onGetProClick = { mainRouter.navigateToPremiumScreen() }
    )
}

@Composable
private fun ImageProcessingScreen(
    uiState: ImageProcessingScreenUiState,
    processingStage: ProcessingStage = ProcessingStage.NONE,
    isPortrait: Boolean = true,
    selectedColor: String? = null,
    imagePath: String? = null,
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
) {
    val TAG = "ImageProcessingScreen"

    Surface {

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
                showGetProButton = false,
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
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            text = "Processing Image",
                            color = colors.onBackground,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            text = currentMessage,
                            color = colors.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                }

                Spacer(modifier = Modifier.weight(1f))

                var adLoadState by remember { mutableStateOf(false) }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp) // match banner height
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (!adLoadState) {
                            Text(
                                text = "Advertisement",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = colors.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .wrapContentSize(align = Alignment.Center)
                            )
                        }

                        AdMobCollapsableBanner(
                            adUnit = AdIdsFactory.getSplashBannerAdId(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            adSize = AdSize.FULL_BANNER, // or adaptive size if needed
                            collapseDirection = CollapseDirection.TOP
                            ,
                            onAdLoaded = { isLoaded ->
                                adLoadState = isLoaded
                                Logger.d(TAG, "AdMobBanner: onAdLoaded: $isLoaded")
                            }
                        )
                    }
                }


                Spacer(modifier = Modifier.height(12.dp))

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

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
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