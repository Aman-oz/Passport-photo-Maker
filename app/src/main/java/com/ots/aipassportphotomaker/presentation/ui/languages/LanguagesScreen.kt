package com.ots.aipassportphotomaker.presentation.ui.languages

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.NativeAdViewCompose
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

// Created by amanullah on 27/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@SuppressLint("ContextCastToActivity")
@Composable
fun LanguagesPage(
    mainRouter: MainRouter,
    viewModel: LanguagesScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "LanguagesPage"

    val uiState by viewModel.uiState.collectAsState()

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "LanguagesPage: Navigation State: $navigationState")
        /*when (navigationState) {
            is LanguagesScreenNavigationState.OnboardingScreen -> mainRouter.navigateToEditImageScreen(
                sourceScreen = "LanguagesScreen"
            )
        }*/
    }

    LanguagesScreen(
        uiState = uiState,
        isPremium = viewModel.isPremiumUser(),
        onBackClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "backPress_LanguagesScreen")
            mainRouter.goBack()
                      },
    )
}

@Composable
private fun LanguagesScreen(
    uiState: LanguagesScreenUiState,
    isPremium: Boolean = false,
    onBackClick: () -> Unit = {},
) {
    val TAG = "LanguagesScreen"

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    Surface(
        modifier = Modifier
            .padding(bottom = systemBarsPadding.calculateBottomPadding())
    ) {

        val context = LocalContext.current
        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage

        if (errorMessage != null) Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

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
                title = stringResource(R.string.select_language),
                showGetProButton = false,
                onBackClick = {
                    onBackClick.invoke()

                },
                onGetProClick = {
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

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {

                        Spacer(modifier = Modifier.height(8.dp))
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
                                    text = stringResource(R.string.advertisement),
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
                }

            }
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun LanguagesScreenPreview() {

    PreviewContainer {
        LanguagesScreen(
            uiState = LanguagesScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            onBackClick = {},
        )
    }
}