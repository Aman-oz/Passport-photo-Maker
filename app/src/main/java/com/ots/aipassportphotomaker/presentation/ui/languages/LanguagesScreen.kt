package com.ots.aipassportphotomaker.presentation.ui.languages

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.NativeAdViewCompose
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.domain.model.language.getLanguageList
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom300

// Created by amanullah on 27/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@SuppressLint("ContextCastToActivity")
@Composable
fun LanguagesPage(
    mainRouter: MainRouter,
    viewModel: LanguagesScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
    onDoneClick: () -> Unit
) {
    val TAG = "LanguagesPage"
    val context = LocalContext.current

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
        onDon = { selectedLanguage ->
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "btnDone_LanguagesScreen")
            viewModel.handleLanguageChange(selectedLanguage)
            onDoneClick.invoke()
        }
    )
}

@Composable
private fun LanguagesScreen(
    uiState: LanguagesScreenUiState,
    isPremium: Boolean = false,
    onBackClick: () -> Unit = {},
    onDon: (String) -> Unit = {},
) {
    val TAG = "LanguagesScreen"
    val context = LocalContext.current
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    val languages = remember { getLanguageList(context) }
    val deviceLanguageCode = context.resources.configuration.locales[0].language

    val defaultLanguage = remember(deviceLanguageCode) {
        languages.find { it.code == deviceLanguageCode } ?: languages.first { it.code == "en" }
    }

    var selectedLanguage by remember { mutableStateOf(defaultLanguage.code) }
    var buttonText by remember { mutableStateOf(defaultLanguage.buttonText) }
    var titleText by remember { mutableStateOf(defaultLanguage.titleText) }
    var defaultText by remember { mutableStateOf(defaultLanguage.defaultText) }
    var otherLanguagesText by remember { mutableStateOf(defaultLanguage.otherLanguages) }

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
                .background(colors.custom300)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {

                        })
                }
        ) {
            CommonTopBar(
                backgroundColor = colors.custom300,
                title = titleText,
                buttonText = buttonText,
                showGetProButton = false,
                showDoneButton = true,
                onBackClick = {
                    onBackClick.invoke()

                },
                onGetProClick = {
                },
                onDoneClick = {
                    Log.d(TAG, "LanguagesScreen: onDoneClick")
                    onDon.invoke(selectedLanguage)
                }
            )

            if (isLoading) {
                LoaderFullScreen()
            } else {

                Column(
                    modifier = Modifier
                        .background(colors.custom300)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {

                                })
                        }
                ) {
                    // Default Section
                    Text(
                        text = defaultText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground,
                        modifier = Modifier
                            .animateContentSize()
                            .padding(
                            top = 24.dp,
                            bottom = 8.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                    )

                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clickable {
                                selectedLanguage = defaultLanguage.code
                                buttonText = defaultLanguage.buttonText
                                titleText = defaultLanguage.titleText
                                defaultText = defaultLanguage.defaultText
                                otherLanguagesText = defaultLanguage.otherLanguages
                            },
                        colors = CardDefaults.cardColors(containerColor = colors.background),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                Spacer(modifier = Modifier.width(8.dp))

                                Image(
                                    painter = painterResource(id = R.drawable.flag_english),
                                    contentDescription = null,
                                    modifier = Modifier.size(34.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Row {

                                    Text(
                                        text = defaultLanguage.nativeName,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = colors.onBackground,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    val annotatedString = buildAnnotatedString {
                                        withStyle(style = SpanStyle(color = colors.onBackground)) {
                                            append("( ")
                                        }
                                        withStyle(
                                            style = SpanStyle(
                                                color = colors.primary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        ) {
                                            append(defaultLanguage.name.lowercase())
                                        }
                                        append(" )")
                                    }

                                    Text(
                                        text = annotatedString,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                    )
                                }
                            }

                            Image(
                                painter = painterResource(
                                    id = if (selectedLanguage == defaultLanguage.code) R.drawable.radio_checked else R.drawable.radio_unchecked
                                ),
                                colorFilter = ColorFilter.tint(colors.primary),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Other Languages Section
                    Text(
                        text = otherLanguagesText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground,
                        modifier = Modifier
                            .animateContentSize()
                            .padding(top = 24.dp, bottom = 8.dp, start = 16.dp)
                    )

                    val otherLanguages =
                        remember(defaultLanguage.code) { languages.filter { it.code != defaultLanguage.code } }

                    LazyColumn(
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            count = otherLanguages.size,
                            key = { index -> otherLanguages[index].id }
                        ) { index ->

                            val language = otherLanguages[index]

                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 2.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLanguage = language.code
                                        buttonText = language.buttonText
                                        titleText = language.titleText
                                        defaultText = language.defaultText
                                        otherLanguagesText = language.otherLanguages
                                    },
                                colors = CardDefaults.cardColors(containerColor = colors.background),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Image(
                                            painter = painterResource(id = language.image),
                                            contentDescription = null,
                                            modifier = Modifier.size(34.dp)
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Row {
                                            Text(
                                                text = language.nativeName,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = colors.onBackground,
                                                fontWeight = FontWeight.Normal,
                                                modifier = Modifier
                                                    .align(Alignment.CenterVertically)
                                            )

                                            Spacer(modifier = Modifier.width(12.dp))
                                            val annotatedString = buildAnnotatedString {
                                                withStyle(style = SpanStyle(color = colors.onBackground)) {
                                                    append("( ")
                                                }
                                                withStyle(
                                                    style = SpanStyle(
                                                        color = colors.primary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                ) {
                                                    append(language.name.lowercase())
                                                }
                                                append(" )")
                                            }

                                            Text(
                                                text = annotatedString,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = colors.onSurfaceVariant,
                                                modifier = Modifier
                                                    .align(Alignment.CenterVertically)
                                            )
                                        }
                                    }

                                    Image(
                                        painter = painterResource(
                                            id = if (selectedLanguage == language.code) R.drawable.radio_checked else R.drawable.radio_unchecked
                                        ),
                                        colorFilter = ColorFilter.tint(if (selectedLanguage == language.code) colors.primary else colors.onBackground),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }

                }

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
            isPremium = true,
            uiState = LanguagesScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            onBackClick = {},
        )
    }
}