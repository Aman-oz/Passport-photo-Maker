package com.ots.aipassportphotomaker.presentation.ui.onboarding

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.ads.AdSize
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.AdMobBanner
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.common.utils.SharedPrefUtils
import com.ots.aipassportphotomaker.domain.model.OnBoardingItem
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun OnboardingPage(
    mainRouter: MainRouter,
    viewModel: OnboardingScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
    onFinishClick: () -> Unit
) {
    val TAG = "OnboardingPage"

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activityContext = context as ComponentActivity

    Logger.d(TAG, "GetStartedPage: UI State: $uiState")

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "GetStartedPage: Navigation State: $navigationState")
        when (navigationState) {
            is OnboardingScreenNavigationState.HomeScreen -> {
//                mainRouter.navigateToPhotoIDDetailScreen(navigationState.type)
            }
        }
    }


    OnboardingScreen(
        uiState = uiState,
        isPremium = viewModel.isPremiumUser(),
        onBackClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "backPress_OnboardingScreen")
            mainRouter.goBack()
        },
        onFinishClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "finish_OnboardingScreen")
            viewModel.setBooleanPreference(SharedPrefUtils.FIRST_LAUNCH, false)
            onFinishClick()
        }
    )

    BackHandler {
        viewModel.sendEvent(AnalyticsConstants.CLICKED, "backPress_OnboardingScreen")
        mainRouter.goBack()
    }

}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun OnboardingScreen(
    uiState: OnboardingScreenUiState,
    onBackClick: () -> Unit,
    onFinishClick: () -> Unit,
    isPremium: Boolean = false,
    isCrossVisible: Boolean = false,
) {

    val TAG = "OnboardingScreen"
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .padding(bottom = systemBarsPadding.calculateBottomPadding())
            .fillMaxSize(),
        color = colors.background
    ) {

        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage

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

            Box(
                modifier = Modifier
                    .background(colors.background)
                    .fillMaxSize(),
            ) {

                val scope = rememberCoroutineScope()

                Column(
                    Modifier.fillMaxSize()
                        .background(colors.background)
                ) {

                    val items = OnBoardingItem.get()
                    val statePager = rememberPagerState()

                    HorizontalPager(
                        state = statePager,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.7f),
                        count = items.size
                    ) { page ->

                        val item = items[page]

                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = item.Image),
                                contentDescription = "Screen1",
                                modifier = Modifier
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

                            if(statePager.currentPage == 0) {
                                Image(
                                    painter = painterResource(id = R.drawable.list_images),
                                    contentDescription = "Screen1",
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .animateContentSize()
                                        ,
                                    contentScale = ContentScale.Fit,
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.27f),
                    ) {

                        BottomSection(
                            size = items.size,
                            index = statePager.currentPage,
                            isPremium = isPremium,
                            statePager
                        ) {
                            if (statePager.currentPage + 1 < items.size) {

                                scope.launch {
                                    statePager.animateScrollToPage(statePager.currentPage + 1)
                                }
                            } else {
                                onFinishClick()
                            }
                        }
                    }
                }

                AnimatedVisibility(isCrossVisible) {
                    Icon(
                        painter = painterResource(id = R.drawable.close_circled_icon),
                        contentDescription = "Back",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 40.dp, end = 28.dp)
                            .clickable(
                                onClick = {
                                    onFinishClick()
                                }
                            )
                    )
                }


            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomSection(
    size: Int,
    index: Int,
    isPremium: Boolean = false,
    statePager: PagerState,
    item: OnBoardingItem = OnBoardingItem.get()[index],
    onNextClicked: () -> Unit
) {
    val TAG = "OnboardingScreen"
    Box(
        modifier = Modifier
            .background(colors.background)
              .fillMaxSize()
            .padding(12.dp)
    ) {
        val buttonText = if (size == index + 1) "Finish" else "Next"
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        )
        {
            Text(
                text = stringResource(id = item.title),
                style = MaterialTheme.typography.headlineSmall,
                color = colors.onBackground,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(id = item.text),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            HorizontalPagerIndicator(
                pagerState = statePager,
                activeColor = colors.primary,
                inactiveColor = colors.outline,

                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Button(
                onClick = {
                    onNextClicked()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            ) {
                Text(
                    modifier = Modifier
                        .padding(vertical = 4.dp),
                    text = buttonText,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onPrimary)
            }
            if (!isPremium) {
                var adLoadState by remember { mutableStateOf(false) }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .height(54.dp) // match banner height
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (!adLoadState) {
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

                        AdMobBanner(
                            adUnit = AdIdsFactory.getOnboardingBannerAdId(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .align(Alignment.Center),
                            adSize = AdSize.BANNER, // or adaptive size if needed
                            onAdLoaded = { isLoaded ->
                                adLoadState = isLoaded
                                Logger.d(TAG, "OnboardingScreen: Ad Loaded: $isLoaded")
                            }
                        )
                    }
                }
            }

        }
        
    }
}

@Preview("Light", device = "id:pixel_5", showSystemUi = true)
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun OnboardingScreenPreview() {
    PreviewContainer {
        OnboardingScreen(
            uiState = OnboardingScreenUiState(
                showLoading = false,
                errorMessage = null
            ),
            onBackClick = {},
            onFinishClick = {}
        )
    }
}