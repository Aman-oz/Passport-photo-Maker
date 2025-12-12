package com.ots.aipassportphotomaker.presentation.ui.splash

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.AdaptiveBannerAd
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.common.ext.bounceClick
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import kotlinx.coroutines.delay
import java.text.BreakIterator
import java.text.StringCharacterIterator

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
    val consentState by viewModel.consentState.collectAsState()

    val context = LocalContext.current
    val activityContext = context as Activity

    LaunchedEffect(Unit) {
        viewModel.initConsent(activityContext)
    }

    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }
//    var isConsentDone by remember { mutableStateOf(false) }

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

    viewModel.consentState.collectAsEffect { isConsentGiven ->
        if (isConsentGiven) {
            Logger.d(TAG, "$TAG: User has given consent: $isConsentGiven")
        }

    }


    GetStartedScreen(
        uiState = uiState,
        consentState = consentState,
//        isConsentDone = isConsentDone,
        isPremium = viewModel.isPremiumUser(),
        onBackClick = {
            mainRouter.goBack()
        },
        isFirstLaunch = sharedViewModel.isFirstLaunch,
        onGetStartedClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "get_started_splash")

            onGetStartedClick()
            /*viewModel.onGetStartedClicked(activityContext) {
                onGetStartedClick()
            }*/
        }
    )

}


@Composable
private fun GetStartedScreen(
    uiState: GetStartedScreenUiState,
    consentState: Boolean,
//    isConsentDone: Boolean,
    onBackClick: () -> Unit,
    onGetStartedClick: () -> Unit,
    isFirstLaunch: Boolean = false,
    isPremium: Boolean = false,
) {

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    val TAG = "GetStartedScreen"

    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .padding(bottom = systemBarsPadding.calculateBottomPadding())
            .fillMaxSize(),
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

            // removed from v1.0.6
            val isBannerAdEnabled by remember { mutableStateOf(false) }

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
                        painter = painterResource(id = R.drawable.after_image_girl),
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

                    var adViewLoadState by remember { mutableStateOf(true) }
                    var callback by remember { mutableStateOf(false) }

                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(id = R.string.app_name_localized),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = colors.onBackground,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .scale(textAnimatedScale)
                            .padding(horizontal = 16.dp),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val descriptionText = stringResource(R.string.creation_of_professional_identification_photos)
                    val breakIterator = remember(descriptionText) { BreakIterator.getCharacterInstance() }
                    val typingDelayInMs = 20L

                    var substringText by remember {
                        mutableStateOf("")
                    }
                    LaunchedEffect(descriptionText) {
                        // Initial start delay of the typing animation
                        delay(100)
                        breakIterator.text = StringCharacterIterator(descriptionText)

                        var nextIndex = breakIterator.next()
                        // Iterate over the string, by index boundary
                        while (nextIndex != BreakIterator.DONE) {
                            substringText = descriptionText.subSequence(0, nextIndex).toString()
                            // Go to the next logical character boundary
                            nextIndex = breakIterator.next()
                            delay(typingDelayInMs)
                        }
                    }
                    Text(
                        text = substringText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .animateContentSize()
                            .scale(textAnimatedScale)
                            .padding(horizontal = 16.dp),
                    )


                    Spacer(modifier = Modifier.height(10.dp))
                    if (isFirstLaunch) {
                        Button(
                            onClick = {
                                if ((!consentState/* || !callback*/) && !isPremium) return@Button

                                onGetStartedClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .bounceClick()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .scale(buttonAnimatedScale),
                        ) {
                            if ((/*!callback || */!consentState) && !isPremium) {
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
                                    text = stringResource(R.string.get_started),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.onPrimary
                                )
                            }

                        }
                    } else {
                        LaunchedEffect(Unit) {
                            delay(3000L) // 3 seconds
                            onGetStartedClick()
                        }
                    }


                    if (isBannerAdEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!isPremium && consentState) {
                            AnimatedVisibility(adViewLoadState) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 54.dp)
                                        .animateContentSize(
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (!callback) {
                                            Text(
                                                text = stringResource(R.string.advertisement),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = colors.onSurfaceVariant,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentSize(align = Alignment.Center)
                                            )
                                        }

                                        AdaptiveBannerAd(
                                            adUnit = AdIdsFactory.getSplashBannerAdId(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .animateContentSize()
                                                .align(Alignment.Center),
                                            onAdLoaded = { isLoaded ->
                                                callback = true
                                                adViewLoadState = isLoaded
                                                Logger.d(
                                                    TAG,
                                                    "AdaptiveBannerAd: onAdLoaded: $isLoaded"
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

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
            consentState = true,
//            isConsentDone = false,
            onBackClick = {},
            onGetStartedClick = {}
        )
    }
}