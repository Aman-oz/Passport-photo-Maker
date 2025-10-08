package com.ots.aipassportphotomaker.presentation.ui.savedimage

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.ads.AdSize
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.AdMobBanner
import com.ots.aipassportphotomaker.adsmanager.admob.AdaptiveBannerAd
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.ext.segmentedShadow
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.FinalScreenTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom100
import com.ots.aipassportphotomaker.presentation.ui.theme.custom300
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400
import com.ots.aipassportphotomaker.presentation.ui.theme.customError
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom300
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun SavedImagePage(
    mainRouter: MainRouter,
    viewModel: SavedImageScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "EditImagePage"

    val uiState by viewModel.uiState.collectAsState()

    val imagePath = viewModel.imagePath
    val context = LocalContext.current
    val activityContext = context as ComponentActivity
    val sourceScreen = viewModel.sourceScreen

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "SavedImagePage: Navigation State: $navigationState, imagePath: $imagePath")
        /*when (navigationState) {
            is EditImageScreenNavigationState.CutOutScreen -> mainRouter.navigateToCutOutScreen(
                documentId = navigationState.documentId
            )
        }*/
    }

    SavedImageScreen(
        uiState = uiState,
        sourceScreen = sourceScreen,
        isPremium = viewModel.isPremiumUser(),
        onBackClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "backPress_SavedImageScreen")
            mainRouter.goBack()

            viewModel.showInterstitialAd(activityContext) {  }
                      },
        onGetProClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "getPro_SavedImageScreen")
            mainRouter.navigateToPremiumScreen()
                        },
        onGoToHomeClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "goToHome_SavedImageScreen")
            mainRouter.navigateToHomeScreen()

            viewModel.showInterstitialAd(activityContext) {  }
        },
        onDeleteClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "deleteImage_SavedImageScreen")

            viewModel.showInterstitialAd(activityContext) {
            viewModel.deleteImage(
                imagePath = uiState.imagePath,
                onSuccess = {
                    Toast.makeText(context,
                        context.getString(R.string.image_deleted_successfully), Toast.LENGTH_SHORT).show()
                    mainRouter.navigateToHomeScreen()
                },
                onError = { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
        },
        onWhatsAppShare = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "shareWhatsApp_SavedImageScreen")
            viewModel.shareToWhatsApp(context,it)
        },
        onInstagramShare = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "shareInstagram_SavedImageScreen")
            viewModel.shareToInstagram(context,it)
        },
        onFacebookShare = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "shareFacebook_SavedImageScreen")
            viewModel.shareToFacebook(context,it)
        },
        onShare = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "shareOther_SavedImageScreen")
            viewModel.shareToOthers(context,it)
        },

        )

    BackHandler {
        viewModel.sendEvent(AnalyticsConstants.CLICKED, "backPress_SavedImageScreen")
        mainRouter.goBack()
        viewModel.showInterstitialAd(activityContext) {  }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UseKtx")
@Composable
private fun SavedImageScreen(
    uiState: SavedImageScreenUiState,
    sourceScreen: String = "",
    isPremium: Boolean = false,
    onDeleteClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
    onGoToHomeClick: () -> Unit = {},
    onWhatsAppShare: (String) -> Unit = {},
    onInstagramShare: (String) -> Unit = {},
    onFacebookShare: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
) {

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    val TAG = "SavedImageScreen"
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val socialItemList = listOf(
        R.drawable.whatsapp_icons to stringResource(R.string.whatsapp),
        R.drawable.insta_icon to stringResource(R.string.instagram),
        R.drawable.facebook_icon to stringResource(R.string.facebook),
        R.drawable.share_icon to stringResource(R.string.others),
    )

    Surface(
        modifier = Modifier
        .padding(bottom = systemBarsPadding.calculateBottomPadding())
    ) {

        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage
        val imagePath = uiState.imagePath

        Logger.i("SavedImagePage", "imagePath: $imagePath")

        if (errorMessage != null) Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

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

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(stringResource(R.string.delete_image)) },
                    text = { Text(stringResource(R.string.are_you_sure_you_want_to_delete_this_image)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                onDeleteClick()
                            }
                        ) {
                            Text(stringResource(R.string.delete), color = colors.customError)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }

            FinalScreenTopBar(
                title = stringResource(R.string.image_saved),
                showGetProButton = !isPremium,
                onBackClick = {
                    onBackClick.invoke()

                },
                onGetProClick = {
                    onGetProClick.invoke()

                },
                onDeleteClick = {
                    showDeleteDialog = true
                }
            )

            if (isLoading) {
                LoaderFullScreen()
            } else {

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

                    Box(
                        modifier = Modifier
                            .height(250.dp)
                            .aspectRatio(uiState.ratio)
                            .background(
                                color = colors.background,
                            )
                            .segmentedShadow(
                                color = colors.background,
                                shadowWidth = 6.dp
                            )

                            .shadow(
                                elevation = 4.dp,
                                ambientColor = colors.background,
                                spotColor = colors.onBackground,
                            )
                            .align(Alignment.CenterHorizontally)

                    ) {
                        var isImageLoading by remember { mutableStateOf(true) } // Start with loading state

                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imagePath)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Final Image",
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxHeight()
                                .background(
                                    color = colors.background,
                                )
                                .aspectRatio(uiState.ratio)
                                .align(Alignment.Center)
                                .clipToBounds(),
                            contentScale = ContentScale.Fit,
                            onState = { state ->
                                isImageLoading = state is AsyncImagePainter.State.Loading
                            },
                            /*placeholder = painterResource(id = R.drawable.sample_image_square),*/
                        )

                        if (isImageLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(48.dp),
                                color = colors.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Checklist Card
                    if (/*(sourceScreen != "CutOutImageScreen" || sourceScreen != "EditImageScreen") && */uiState.documentName.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(colors.custom400)
                                .border(1.dp, colors.custom100, RoundedCornerShape(16.dp)),
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                // Title with Icon
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.passport_united_state), // Replace with passport icon
                                        contentDescription = "Passport Icon",
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = uiState.documentName,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = colors.onCustom400,
                                            modifier = Modifier
                                        )
                                        Text(
                                            text = uiState.documentType,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colors.onSurfaceVariant,
                                            modifier = Modifier
                                        )
                                    }

                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Checklist Items
                                ChecklistItem(
                                    uiState,
                                    text = stringResource(R.string.printable)
                                )

                                ChecklistItem(
                                    uiState,
                                    text = stringResource(R.string.compressed)
                                )

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        items(socialItemList.size) { index ->
                            val (iconRes, platformName) = socialItemList[index]
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .clickable {
                                        when (platformName) {
                                            context.getString(R.string.whatsapp) -> {
                                                onWhatsAppShare.invoke(imagePath ?: "")
                                            }

                                            context.getString(R.string.instagram) -> {
                                                onInstagramShare.invoke(imagePath ?: "")
                                            }

                                            context.getString(R.string.facebook) -> {
                                                onFacebookShare.invoke(imagePath ?: "")
                                            }

                                            context.getString(R.string.others) -> {
                                                onShare.invoke(imagePath ?: "")
                                                /*imagePath?.let {
                                                    ShareCompat.IntentBuilder(context)
                                                        .setType("image/jpeg")
                                                        .addStream(it.toUri())
                                                        .setChooserTitle("Share image")
                                                        .setSubject("Shared image")
                                                        .startChooser()
                                                }*/
                                            }
                                        }
                                    }
                            ) {
                                if (platformName == stringResource(R.string.others)) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = colors.custom300,
                                        border = BorderStroke(1.dp, colors.custom100),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                painter = painterResource(id = iconRes),
                                                contentDescription = "$platformName Icon",
                                                tint = colors.onCustom300,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Image(
                                        painter = painterResource(id = iconRes),
                                        contentDescription = "$platformName Icon",
                                        modifier = Modifier.size(42.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = platformName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(8.dp)
                            .clickable {
                                onGoToHomeClick.invoke()
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.home_icon),
                            contentDescription = "Home Icon",
                            modifier = Modifier,
                            tint = colors.onBackground
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.go_to_home),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = colors.onBackground,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onBackClick.invoke()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text(
                            text = stringResource(R.string.create_again),
                            color = colors.onPrimary,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    if (!isPremium) {
                        var adLoadState by remember { mutableStateOf(false) }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .heightIn(min = 54.dp) // match banner height
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

                                AdaptiveBannerAd(
                                    adUnit = AdIdsFactory.getBannerAdId(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateContentSize()
                                        .align(Alignment.Center),
                                    onAdLoaded = { isLoaded ->
                                        adLoadState = true
                                        Logger.d(TAG, "AdaptiveBannerAd: onAdLoaded: $isLoaded")
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
                                        Logger.d(TAG, "OnboardingScreen: Ad Loaded: $isLoaded")
                                    }
                                )*/
                            }
                        }

                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun ChecklistItem(
    uiState: SavedImageScreenUiState,
    text: String,
) {

    var selectedDpi by rememberSaveable { mutableStateOf(uiState.documentResolution) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {

        Image(
            painter = painterResource(id = R.drawable.check_circle),
            contentDescription = "Checked",
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .animateContentSize()
                .weight(1f)
        )

        Box(
            modifier = Modifier
                .background(colors.custom300, shape = RoundedCornerShape(6.dp))
                .border(1.dp, colors.custom100, shape = RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = when (text) {
                    stringResource(R.string.printable) -> "${selectedDpi.uppercase()} DPI" ?: "300 DPI"
                    stringResource(R.string.compressed) -> "${uiState.fileSize}" ?: "400 KB"
                    else -> ""
                },
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onCustom300
            )
        }

    }
}


@Preview("Light",showSystemUi = true, device = "id:pixel_5")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES,showSystemUi = true, device = "id:pixel_5")
@Composable
fun ImageProcessingScreenPreview() {
    PreviewContainer {
        SavedImageScreen(
            uiState = SavedImageScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            onBackClick = {},
            onGetProClick = {},
            onDeleteClick = {},
            onWhatsAppShare = {},
            onInstagramShare = {},
            onFacebookShare = {},
            onShare = {}
        )
    }
}