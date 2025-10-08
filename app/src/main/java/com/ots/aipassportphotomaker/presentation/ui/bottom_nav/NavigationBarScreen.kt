package com.ots.aipassportphotomaker.presentation.ui.bottom_nav

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdSize
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.AdMobBanner
import com.ots.aipassportphotomaker.adsmanager.admob.AdaptiveBannerAd
import com.ots.aipassportphotomaker.adsmanager.admob.NativeAdViewCompose
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.bottom_nav.route
import com.ots.aipassportphotomaker.presentation.ui.components.TopBar
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.AppColors
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.funnelFamily

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Composable
fun NavigationBarScreen(
    isPremium: Boolean,
    systemBarsPadding: PaddingValues,
    sharedViewModel: NavigationBarSharedViewModel,
    mainRouter: MainRouter,
    darkMode: Boolean,
    onSettingClick: () -> Unit,
    onThemeUpdated: () -> Unit,
    showDeleteIcon: Boolean = false,
    onDeleteClick: () -> Unit = {},
    nestedNavController: NavHostController,
    content: @Composable () -> Unit
) {
    val uiState = NavigationBarUiState()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopBar(
                isPremium = isPremium,
                stringResource(id = R.string.photo_id),
                darkMode,
                fontFamily = funnelFamily,
                onThemeUpdated = onThemeUpdated,
                onSettingsClick = {
                    onSettingClick()
                },
                showDeleteIcon = showDeleteIcon,
                onDeleteClick = {
                    onDeleteClick()
                },
                onGetProClick = {
                    mainRouter.navigateToPremiumScreen()
                }
            )

        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(bottom = systemBarsPadding.calculateBottomPadding())
            ) {

                BottomNavigationBar(
                    /*systemBarsPadding = systemBarsPadding,*/
                    items = uiState.bottomItems,
                    navController = nestedNavController,
                    onItemClick = { bottomItem ->
                        val currentPageRoute = nestedNavController.currentDestination?.route
                        val clickedPageRoute = bottomItem.page
                        val notSamePage = currentPageRoute != clickedPageRoute.route()
                        if (notSamePage) {
                            nestedNavController.navigate(clickedPageRoute) {
                                launchSingleTop = true
                                popUpTo(nestedNavController.graph.findStartDestination().id)
                            }
                        }
                        sharedViewModel.onBottomItemClicked(bottomItem)
                    }
                )
                if (!isPremium) {
                    var adLoadState by remember { mutableStateOf(false) }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .heightIn(min = 54.dp)
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
                                        .wrapContentSize(align = Alignment.Center)
                                )
                            }

                            AdaptiveBannerAd(
                                adUnit = AdIdsFactory.getBannerAdId(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                                    .align(Alignment.Center),
                                onAdLoaded = { isLoaded ->
                                    adLoadState = true
                                    Logger.d("AdmobBanner", "AdaptiveBannerAd: onAdLoaded: $isLoaded")
                                }
                            )

                            /*AdMobBanner(
                                adUnit = AdIdsFactory.getBannerAdId(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                                    .align(Alignment.Center),
                                adSize = AdSize.BANNER, // or adaptive size if needed
                                onAdLoaded = { isLoaded ->
                                    adLoadState = isLoaded
                                    Logger.d("AdMobBanner", "AdMobBanner: onAdLoaded: $isLoaded")
                                }
                            )*/
                        }
                    }

                }
               /* if (!isPremium) {
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
                        }
                    }
                }*/
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize(1f)
                .padding(paddingValues)
                .background(color = colors.background)
        ) {
            content()
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NavigationBarScreenPreview() = PreviewContainer {
    val navController = rememberNavController()
    val mainRouter = MainRouter(navController)
    val darkTheme = isSystemInDarkTheme()

    NavigationBarScreen(
        isPremium = false,
        systemBarsPadding = PaddingValues(0.dp),
        sharedViewModel = hiltViewModel<NavigationBarSharedViewModel>(),
        mainRouter = mainRouter,
        darkMode = darkTheme,
        onSettingClick = {},
        onThemeUpdated = { },
        nestedNavController = navController,
        content = {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color = colors.background)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 20.sp,
                    text = "Page Content"
                )
            }
        }
    )
}