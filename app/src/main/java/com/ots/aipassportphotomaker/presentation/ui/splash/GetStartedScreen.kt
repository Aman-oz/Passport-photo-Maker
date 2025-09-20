package com.ots.aipassportphotomaker.presentation.ui.splash

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.AdMobBanner
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.adsmanager.admob.adids.TestAdIds
import com.ots.aipassportphotomaker.adsmanager.admob.loadFullScreenAd
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun GetStartedPage(
    mainRouter: MainRouter,
    viewModel: GetStartedScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
    onGetStartedClick: () -> Unit
) {
    val TAG = "GetStartedPage"

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activityContext = context as Activity

    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }
    var isAdLoading by remember { mutableStateOf(true) }

    // Load an interstitial ad
    LaunchedEffect(Unit) {
        context.loadFullScreenAd(
            adUnitId = AdIdsFactory.getWelcomeInterstitialAdId(),
            onAdLoaded = { ad ->
                isAdLoading = false
                Logger.i(TAG, "$TAG: Interstitial ad loaded.")
                interstitialAd = ad
            },
            onAdFailedToLoad = {
                isAdLoading = false
                Logger.e(TAG, "$TAG: Interstitial ad failed to load: $it")
            },
            onAdDismissed = {
                Logger.i(TAG, "$TAG: Interstitial ad dismissed.")
                onGetStartedClick()
                interstitialAd = null
            }
        )
    }

    Logger.d(TAG, "GetStartedPage: UI State: $uiState")

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "GetStartedPage: Navigation State: $navigationState")
        when (navigationState) {
            is GetStartedScreenNavigationState.OnboardingScreen -> {
//                mainRouter.navigateToPhotoIDDetailScreen(navigationState.type)
            }

            is GetStartedScreenNavigationState.HomeScreen -> {
                Log.d(TAG, "GetStartedPage: Navigate to Select Photo Screen")
                /*mainRouter.navigateToSelectPhotoScreen(
                    documentId = navigationState.documentId,

                )*/
            }
        }
    }


    GetStartedScreen(
        uiState = uiState,
        isAdLoading = isAdLoading,
        onBackClick = {
            mainRouter.goBack()
        },
        onGetStartedClick = {
            interstitialAd?.let {
                it.show(context)
            } ?: run {
                onGetStartedClick()
                Logger.e(TAG, "$TAG: Interstitial ad not ready yet.")
            }


        }
    )

}


@Composable
private fun GetStartedScreen(
    uiState: GetStartedScreenUiState,
    isAdLoading: Boolean,
    onBackClick: () -> Unit,
    onGetStartedClick: () -> Unit
) {

    val TAG = "GetStartedScreen"

    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {

        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_white))

        if (errorMessage != null) Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

        if (isLoading) {
            LoaderFullScreen()
        } else {

            var scale0 by remember { mutableFloatStateOf(1f) }
            val imageAnimatedScale by animateFloatAsState(
                targetValue = scale0,
                label = "FloatAnimation"
            )
            var scale1 by remember { mutableFloatStateOf(1f) }
            val textAnimatedScale by animateFloatAsState(
                targetValue = scale1,
                label = "FloatAnimation"
            )
            var scale2 by remember { mutableFloatStateOf(1f) }
            val buttonAnimatedScale by animateFloatAsState(
                targetValue = scale2,
                label = "FloatAnimation"
            )

//            Box(
//                modifier = Modifier
//                    .background(colors.background)
//                    .fillMaxSize(),
//            ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.background),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.after_image_girl1),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { alpha = 0.99F }
                            .drawWithContent {
                                val height = size.height

                                val colors = listOf(Color.Transparent, colors.background)
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = colors,
                                        startY = height, // Start at the bottom
                                        endY = height - (height / 2f) // End halfway up
                                    ),
                                    blendMode = BlendMode.DstIn
                                )
                            },

                        contentScale = ContentScale.Fit
                    )

                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        colors.background
                                    )
                                )
                            )
                            .align(Alignment.BottomCenter)
                    )

                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .scale(textAnimatedScale)
                            .padding(horizontal = 16.dp),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Creation of Professional identification Photos.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .scale(textAnimatedScale)
                            .padding(horizontal = 16.dp),
                    )


                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onGetStartedClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .scale(buttonAnimatedScale),
                    ) {
                        if (isAdLoading) {
                            LottieAnimation(
                                composition = composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .padding(vertical = 4.dp),
                                text = "Get Started",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.onPrimary
                            )
                        }

                    }


                    Spacer(modifier = Modifier.height(8.dp))

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

                            AdMobBanner(
                                adUnit = AdIdsFactory.getSplashBannerAdId(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),
                                adSize = AdSize.BANNER, // or adaptive size if needed
                                onAdLoaded = { isLoaded ->
                                    adLoadState = isLoaded
                                }
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(12.dp))

                }

            }


//            }
        }
    }
}

@Preview("Light", device = "id:pixel_5", showSystemUi = true)
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DocumentInfoScreenPreview() {
    PreviewContainer {
        GetStartedScreen(
            uiState = GetStartedScreenUiState(
                showLoading = false,
                errorMessage = null
            ),
            isAdLoading = false,
            onBackClick = {},
            onGetStartedClick = {}
        )
    }
}