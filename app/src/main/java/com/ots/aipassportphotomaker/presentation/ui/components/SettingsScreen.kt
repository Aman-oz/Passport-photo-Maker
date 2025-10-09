package com.ots.aipassportphotomaker.presentation.ui.components

import android.R.attr.fontWeight
import android.R.style
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.presentation.ui.theme.colors


// Created by amanullah on 16/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    themeSelectedIndex: Int = 0,
    appVersion: String = "1.0.0",
    onCloseClick: () -> Unit,
    onPremiumClick: () -> Unit = {},
    onChangeThemeClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onRateUs: () -> Unit = {},
    onShareApp: () -> Unit = {},
    onPrivacyPolicy: () -> Unit = {}
) {
    val premiumItems = listOf(
        PremiumData(
            title = stringResource(R.string.remove_ads_and_unlock_all_features),
            backgroundImageRes = R.drawable.feature_1, // Replace with your image
            diamondIconRes = R.drawable.diamond_blue, // Replace with your diamond
            adImageRes = R.drawable.premium_ads // Replace with your ad
        ),
        PremiumData(
            title = stringResource(R.string.auto_fit_image_through_advanced_ai),
            backgroundImageRes = R.drawable.feature_1, // Replace with your image
            diamondIconRes = R.drawable.diamond_orange, // Replace with your diamond
            adImageRes = R.drawable.premium_ads // Replace with your ad
        ),
        PremiumData(
            title = stringResource(R.string.remove_ads_and_unlock_all_features),
            backgroundImageRes = R.drawable.feature_1, // Replace with your image
            diamondIconRes = R.drawable.diamond_blue, // Replace with your diamond
            adImageRes = R.drawable.premium_ads // Replace with your ad
        )
    )
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { premiumItems.size })

    // Auto-swipe logic
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Swipe every 3 seconds
            coroutineScope.launch {
                val nextPage = (pagerState.currentPage + 1) % premiumItems.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar with Title and Close Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp, top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onCustom400
            )
            IconButton(onClick = onCloseClick) {
                Icon(
                    painter = painterResource(id = R.drawable.close_circled_icon),
                    contentDescription = stringResource(R.string.close),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Premium Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable(onClick = onPremiumClick)
            ,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.background)
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                HorizontalPager(
                    modifier = Modifier
                        .background(color = colors.background)
                        .clip(RoundedCornerShape(24.dp)),
                    state = pagerState
                ) { page ->
                    PremiumItem(
                        data = premiumItems[page],
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f)
                    )
                }
                // Dots Indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PagerIndicator(
                        pageCount = premiumItems.size,
                        currentPage = pagerState.currentPage,
                        indicatorSize = 8.dp,
                        spacing = 4.dp,
                        activeColor = colors.primary,
                        inactiveColor = colors.outline
                    )
                }
            }
        }

        // Settings Items
        Column {
            SettingsItem(
                title = stringResource(id = R.string.change_theme),
                selectedTheme = when (themeSelectedIndex) {
                    0 -> stringResource(id = R.string.system_default)
                    1 -> stringResource(id = R.string.light_mode)
                    2 -> stringResource(id = R.string.dark_mode)
                    else -> stringResource(id = R.string.light_mode)
                },
                icon = R.drawable.theme_icon,
                onClick = { onChangeThemeClick() }
            )
            SettingsItem(
                title = stringResource(R.string.language),
                icon = R.drawable.language_icon,
                onClick = { onLanguageClick() }
            )
            SettingsItem(
                title = stringResource(R.string.rate_us),
                icon = R.drawable.star_icon,
                onClick = { onRateUs() }
            )
            SettingsItem(
                title = stringResource(R.string.share_app),
                icon = R.drawable.share_app_icon,
                onClick = { onShareApp() }
            )
            SettingsItem(
                title = stringResource(R.string.privacy_policy),
                icon = R.drawable.privacy_icon,
                onClick = { onPrivacyPolicy() }
            )
        }

        // App Version
        Text(
            text = "${stringResource(id = R.string.version)}: $appVersion",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp)
        )
    }
}

data class PremiumData(
    val title: String,
    val backgroundImageRes: Int,
    val diamondIconRes: Int,
    val adImageRes: Int
)

@Composable
fun PremiumItem(
    data: PremiumData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            Image(
                painter = painterResource(id = data.backgroundImageRes),
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
// Diamond Icon at Left Start
                Icon(
                    painter = painterResource(id = data.diamondIconRes),
                    contentDescription = "Diamond Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                        .size(48.dp)
                )
                // Centered Column with Text
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.get_premium),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onPrimaryContainer,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 6.dp)
                    )
                    Text(
                        text = data.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onPrimaryContainer,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start ,
                                modifier = Modifier
                                .align(Alignment.Start)
                    )
                }
                // Ad Image at Bottom
                Image(
                    painter = painterResource(id = data.adImageRes),
                    contentDescription = "Ad Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(48.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: Int = R.drawable.settings_icon,
    selectedTheme: String = stringResource(R.string.light_mode),
    selectedLanguage: String = stringResource(R.string.english),
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.background),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Item Image",
                tint = colors.onBackground,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
                    .size(28.dp)
            )

            Row(
                modifier = modifier
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Normal,
                    modifier = modifier
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                )

                if (title == stringResource(R.string.change_theme) || title == stringResource(R.string.language)) {
                    val text = if (title == stringResource(R.string.change_theme)) selectedTheme else selectedLanguage
                    val annotatedString = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = colors.onBackground)) {
                            append("( ")
                        }
                        withStyle(style = SpanStyle(color = colors.primary,fontWeight = FontWeight.Medium)) {
                            append(text)
                        }
                        append(" )")
                    }

                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }

            }


            Icon(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = "Arrow Next",
                tint = colors.onBackground,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
                    .size(28.dp)
            )

        }

    }

}

@Composable
fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    indicatorSize: Dp,
    spacing: Dp,
    activeColor: Color,
    inactiveColor: Color
) {
    Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
        for (i in 0 until pageCount) {
            Box(
                modifier = Modifier
                    .size(indicatorSize)
                    .background(
                        color = if (i == currentPage) activeColor else inactiveColor,
                        shape = RoundedCornerShape(indicatorSize / 2)
                    )
            )
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenPreview() {
    PreviewContainer {
        SettingsScreen(
            onCloseClick = {},
            appVersion = "1.0.0"
        )
    }
}