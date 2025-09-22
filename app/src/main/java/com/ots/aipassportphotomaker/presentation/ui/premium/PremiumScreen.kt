package com.ots.aipassportphotomaker.presentation.ui.premium

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.iab.AppBillingClient
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.common.utils.SharedPrefUtils
import com.ots.aipassportphotomaker.common.utils.UrlFactory
import com.ots.aipassportphotomaker.common.utils.ViewsUtils.premiumHorizontalGradientBrush
import com.ots.aipassportphotomaker.common.utils.ViewsUtils.premiumHorizontalOppositeGradientBrush
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom100

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun PremiumPage(
    mainRouter: MainRouter,
    viewModel: PremiumScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel
) {
    val TAG = "PremiumPage"

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activityContext = context as ComponentActivity

    Logger.d(TAG, "PremiumPage: UI State: $uiState")

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "PremiumPage: Navigation State: $navigationState")
        when (navigationState) {

            is PremiumScreenNavigationState.HomeScreen -> {
                Log.d(TAG, "PremiumPage: Navigate to Select Photo Screen")
                /*mainRouter.navigateToSelectPhotoScreen(
                    documentId = navigationState.documentId,

                )*/
            }
        }
    }


    PremiumScreen(
        uiState = uiState,
        onCloseClick = {
            mainRouter.goBack()
        },
        onSubscribeWeekly = {
            viewModel.purchaseSubscription(activityContext, AppBillingClient.SKU_ITEM_ONE_WEEK)
        },
        onSubscribeMonthly = {
            viewModel.purchaseSubscription(activityContext, AppBillingClient.SKU_ITEM_ONE_MONTH)
        },
        onTermsClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(UrlFactory.TERMS_AND_CONDITIONS_URL))
            activityContext.startActivity(intent)
        },
        onPrivacyClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(UrlFactory.PRIVACY_POLICY_URL))
            activityContext.startActivity(intent)
        }
    )

}


@Composable
private fun PremiumScreen(
    uiState: PremiumScreenUiState,
    onCloseClick: () -> Unit,
    onSubscribeWeekly: () -> Unit = {},
    onSubscribeMonthly: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
) {
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    val TAG = "PremiumScreen"

    val context = LocalContext.current
    val uiScope = rememberCoroutineScope()

    val sharedPreferences =
        context.getSharedPreferences(SharedPrefUtils.PREF_KEY, Context.MODE_PRIVATE)
    val isDarkMode = remember {
        mutableStateOf(
            sharedPreferences.getBoolean(
                SharedPrefUtils.DARK_MODE,
                false
            )
        )
    }

    Surface(
        modifier = Modifier
            .padding(bottom = systemBarsPadding.calculateBottomPadding())
            .fillMaxSize(),
        color = Color.Transparent
    ) {

        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage

        // Show Toast when errorMessage changes
        LaunchedEffect(errorMessage) {
            if (!isLoading && errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

//        if (errorMessage != null) Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

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
                modifier = Modifier.fillMaxSize()
            ) {
                val screenBg = if (isDarkMode.value) {
                    R.drawable.premium_bg_dark
                } else {
                    R.drawable.premium_bg
                }

                val illustrationImg = if (isDarkMode.value) {
                    R.drawable.premium_illustration_dark
                } else {
                    R.drawable.premium_illustration
                }

                Image(
                    painter = painterResource(id = screenBg),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = 0.99F },
                    contentScale = ContentScale.FillBounds
                )

                // opacity of 8%
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 40.dp)
                        .background(
                            color = colors.onBackground.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text(
                        text = "Restore",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = colors.onBackground,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable(
                                onClick = {
                                    // onRestoreClick()
                                }
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.close_circled_icon),
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 40.dp, end = 28.dp)
                        .clickable(
                            onClick = {
                                onCloseClick()
                            }
                        ),
                    tint = colors.onBackground
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(50.dp))

                    Image(
                        painter = painterResource(id = illustrationImg),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier
                            .padding(horizontal = 28.dp)
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .graphicsLayer { alpha = 0.99F },
                        contentScale = ContentScale.Fit
                    )


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(

                            modifier = Modifier
                                .padding(start = 16.dp, top = 16.dp)
                                .align(Alignment.Start)
                        ) {
                            val gradientColors = listOf(
                                Color(0xFF4D50FB),
                                Color(0xFFEF15AA),
                                Color(0xFFFA7451)
                            )
                            Text(
                                text = stringResource(id = R.string.get_premium),
                                style = TextStyle(
                                    brush = Brush.horizontalGradient(
                                        colors = gradientColors
                                    ),
                                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                    lineHeight = MaterialTheme.typography.headlineMedium.lineHeight,
                                    letterSpacing = MaterialTheme.typography.headlineMedium.letterSpacing
                                ),
                                fontWeight = FontWeight.Bold,
                                color = colors.onBackground,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .scale(textAnimatedScale)
                                    .align(Alignment.CenterVertically),
                            )

                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                        .background(
                                            brush = premiumHorizontalGradientBrush,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .align(Alignment.CenterVertically),
                                ) {

                                    Text(
                                        text = "PRO",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 12.dp, vertical = 4.dp)
                                    )

                                }
                                Image(
                                    painter = painterResource(id = R.drawable.star_with_pluses),
                                    contentDescription = stringResource(id = R.string.app_name),
                                    modifier = Modifier
                                        .align(Alignment.Top),
                                    contentScale = ContentScale.Fit
                                )
                            }

                        }



                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Unlock all premium benefits and features.",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurfaceVariant,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .scale(textAnimatedScale)
                                .padding(horizontal = 20.dp),
                        )

                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.unlimited_ai_usage),
                                        contentDescription = "Icon",
                                        tint = colors.onBackground,
                                        modifier = Modifier
                                            .size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Unlimited Ai Usage",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.onCustom100,
                                    )
                                }


                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.no_ads),
                                        contentDescription = "Icon",
                                        tint = colors.onBackground,
                                        modifier = Modifier
                                            .size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "No Annoying Ads",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.onCustom100,
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.fast_processing),
                                        contentDescription = "Icon",
                                        tint = colors.onBackground,
                                        modifier = Modifier
                                            .size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Faster Processing",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.onCustom100,
                                    )
                                }


                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.no_watermark),
                                        contentDescription = "Icon",
                                        tint = colors.onBackground,
                                        modifier = Modifier
                                            .size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "No Watermark",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.onCustom100,
                                    )
                                }
                            }

                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Weekly Subscription Button
                        val weeklyItem =
                            uiState.subscriptionItems.find { it.sku == AppBillingClient.SKU_ITEM_ONE_WEEK }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 16.dp)
                                .background(
                                    brush = premiumHorizontalGradientBrush,
                                    shape = RoundedCornerShape(32.dp)
                                )
                                .clickable(
                                    onClick = {
                                        onSubscribeWeekly()
                                    }
                                )

                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(
                                        color = colors.background,
                                        shape = RoundedCornerShape(32.dp)
                                    )

                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp, horizontal = 16.dp)
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .weight(1f),
                                        text = "Subscribe Weekly",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.onBackground
                                    )
                                    Text(
                                        modifier = Modifier,
                                        text = weeklyItem?.pricingPhase?.formattedPrice
                                            ?: "Rs. 850.00",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.onBackground
                                    )
                                }

                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Monthly Subscription Button
                        val monthlyItem =
                            uiState.subscriptionItems.find { it.sku == AppBillingClient.SKU_ITEM_ONE_MONTH }
                        PremiumMonthlyButton(
                            buttonText = "Subscribe Monthly",
                            buttonPrice = monthlyItem?.pricingPhase?.formattedPrice
                                ?: "Rs. 1,499.00",
                            onSubscribeMonthly = {
                                onSubscribeMonthly()
                            },
                        )

                        Text(
                            text = "Auto renew, cancel anytime",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = colors.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .scale(textAnimatedScale)
                                .padding(horizontal = 20.dp),
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Terms of Service",
                                style = MaterialTheme.typography.labelMedium,
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onBackground,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clickable(
                                        onClick = {
                                            onTermsClick()
                                        }
                                    )
                            )

                            VerticalDivider(
                                color = colors.onSurfaceVariant,
                                thickness = 1.dp,
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .height(14.dp)
                                    .align(Alignment.CenterVertically)
                            )

                            Text(
                                text = "Privacy policy",
                                style = MaterialTheme.typography.labelMedium,
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onBackground,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clickable(
                                        onClick = {
                                            onPrivacyClick()
                                        }
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                    }

                }


            }

        }
    }
}

@Composable
fun PremiumMonthlyButton(
    modifier: Modifier = Modifier,
    buttonText: String = "Subscribe Monthly",
    buttonPrice: String = "Rs. 1,499.00",
    onSubscribeMonthly: () -> Unit = {},
) {
    val constraints = ConstraintSet {
        val button = createRefFor("button")
        val tag = createRefFor("tag")

        constrain(button) {
            centerTo(parent)
            width = Dimension.matchParent
            height = Dimension.preferredWrapContent
        }

        constrain(tag) {
            end.linkTo(button.end, margin = 16.dp) // Align to top-right with margin
            top.linkTo(button.top) // Align to top with margin
            width = Dimension.preferredWrapContent // Let it wrap content
            height = Dimension.preferredWrapContent // Let it wrap content
        }
    }

    ConstraintLayout(constraints, modifier = modifier) {
        // Main Button
        Box(
            modifier = Modifier
                .layoutId("button")
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .background(
                    brush = premiumHorizontalGradientBrush,
                    shape = RoundedCornerShape(50.dp)
                )
                .fillMaxWidth()
                .animateContentSize()
                .clickable(
                    onClick = {
                        onSubscribeMonthly()
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 24.dp, horizontal = 20.dp)
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = buttonText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    modifier = Modifier,
                    text = buttonPrice,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Tag at top-right corner
        Box(
            modifier = Modifier
                .layoutId("tag")
                .background(
                    brush = premiumHorizontalOppositeGradientBrush,
                    shape = RoundedCornerShape(16.dp)
                ) // Smaller radius for tag
                .border(
                    width = 2.dp,
                    color = colors.background,
                    shape = RoundedCornerShape(16.dp)
                ), // Adjust padding for tag size
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bolt_icon),
                    contentDescription = "Recommended",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "Recommended",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview("Light", device = "id:pixel_5", showSystemUi = true)
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PremiumScreenPreview() {
    PreviewContainer {
        PremiumScreen(
            uiState = PremiumScreenUiState(
                showLoading = false,
                errorMessage = null
            ),
            onCloseClick = {}
        )
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PremiumButtonPreview() {
    PreviewContainer {
        PremiumMonthlyButton()
    }
}