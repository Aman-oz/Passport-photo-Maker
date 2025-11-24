package com.ots.aipassportphotomaker.presentation.ui.cutout

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.AdaptiveBannerAd
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.common.ext.animatedBorder
import com.ots.aipassportphotomaker.common.ext.bounceClick
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.common.utils.DrawViewAction
import com.ots.aipassportphotomaker.common.utils.FileUtils.saveBitmapToInternalStorage
import com.ots.aipassportphotomaker.common.utils.GraphicOverlay
import com.ots.aipassportphotomaker.common.utils.ImageUtils.saveBitmapToGallery
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.common.utils.ViewsUtils.premiumHorizontalGradientBrush
import com.ots.aipassportphotomaker.domain.model.ProcessingStage
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.CustomTab
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.TextSwitch
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom300
import com.ots.aipassportphotomaker.presentation.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import android.graphics.Color as AndroidColor

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun CutOutImagePage(
    mainRouter: MainRouter,
    viewModel: CutOutImageScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel
) {
    val TAG = "CutOutImagePage"

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val uiScope = rememberCoroutineScope()

    val imageUrl = viewModel.imageUrl
    val selectedColor = viewModel.selectedColor

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "CutOutImagePage: Navigation State: $navigationState, ImageUrl: $imageUrl")
        when (navigationState) {
            is CutOutImageScreenNavigationState.SavedImageScreen -> mainRouter.navigateFromCutoutToSavedImageScreen(
                documentId = navigationState.documentId,
                imagePath = uiState.imageUrl,
                sourceScreen = "CutOutImageScreen"
            )
        }
    }

    val processingStage by viewModel.processingStage.collectAsState()

    val commonSharedViewModel: SharedViewModel = hiltViewModel(activity)

    CutOutImageScreen(
        uiState = uiState,
        processingStage = processingStage,
        selectedColor = selectedColor,
        isPremium = viewModel.isPremiumUser(),
        onBackClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "btnBack_CutOutImageScreen")
            mainRouter.goBack()

            viewModel.showInterstitialAd(activity) { }
        },
        onGetProClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "btnGetPro_CutOutImageScreen")
            mainRouter.navigateToPremiumScreen()
        },
        onSaveImage = { bitmap ->
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "btnSave_CutOutImageScreen")
            uiScope.launch {
                when (viewModel.sourceScreen) {
                    "EditImageScreen" -> {
                        Logger.i(TAG, "onSaveImage: Navigating back to EditImageScreen")
                        val internalPath =
                            saveBitmapToInternalStorage(context, bitmap.asAndroidBitmap())
                        if (internalPath != null) {
                            Logger.i(TAG, "Image saved successfully: $internalPath")
                            commonSharedViewModel.setEditedImageResult(internalPath)

                            delay(100)
                            mainRouter.goBack()

                            viewModel.showInterstitialAd(activity) { }

                        } else {
                            Logger.e(TAG, "Failed to save image")
                        }
                    }

                    "HomeScreen" -> {
                        Logger.i(TAG, "onSaveImage: Navigating to final screen")
                        val savedUri = saveBitmapToGallery(context, bitmap.asAndroidBitmap())
                        if (savedUri != null) {

                            viewModel.onImageSaved(savedUri.toString())
                            viewModel.showInterstitialAd(activity) { }

                            /*viewModel.loadAndShowRewardedAd(
                                activity,
                                onAdClosed = {
                                    viewModel.onImageSaved(savedUri.toString())
                                },
                                onRewardedEarned = { isEarned ->
                                    if (!isEarned) {
                                        viewModel.onImageSaved(savedUri.toString())
                                        viewModel.showInterstitialAd(activity) { }
                                    }

                                })*/

                            Logger.i(TAG, "Image saved successfully: $savedUri")

                        } else {
                            Logger.e(TAG, "Failed to save image")
                        }
                    }

                    else -> {
                        Logger.i(
                            TAG,
                            "onSaveImage: Unknown sourceScreen, defaulting to EditImageScreen: ${uiState.sourceScreen}"
                        )
                        val savedUri = saveBitmapToGallery(context, bitmap.asAndroidBitmap())
                        if (savedUri != null) {
                            Logger.i(TAG, "Image saved successfully: $savedUri")
                            commonSharedViewModel.setEditedImageResult(savedUri.toString())

                            delay(100)
                            mainRouter.goBack()

                        } else {
                            Logger.e(TAG, "Failed to save image")
                        }
                    }
                }
            }
        },
        onRemoveBackgroundAi = { bitmap ->
            uiScope.launch {
                val internalPath = saveBitmapToInternalStorage(context, bitmap.asAndroidBitmap())
                if (internalPath != null) {
                    Logger.i(TAG, "Image saved successfully: $internalPath")
                    viewModel.removeBackground(File(internalPath))
                    commonSharedViewModel.setRemovedBgResult(true)
                }
            }
        }
    )

    BackHandler {
        viewModel.sendEvent(AnalyticsConstants.CLICKED, "backPress_CutOutImageScreen")
        mainRouter.goBack()
        viewModel.showInterstitialAd(activity) { }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UseKtx")
@Composable
private fun CutOutImageScreen(
    uiState: CutOutImageScreenUiState,
    processingStage: ProcessingStage = ProcessingStage.NONE,
    selectedColor: String? = null,
    isPremium: Boolean,
    onSaveImage: (ImageBitmap) -> Unit = {},
    onRemoveBackgroundAi: (ImageBitmap) -> Unit = {},
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai_erase))
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    val TAG = "CutOutImageScreen"
    Surface(
        modifier = Modifier
            .padding(bottom = systemBarsPadding.calculateBottomPadding())
    ) {

        val context = LocalContext.current
        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage
        val backgroundColor = uiState.selectedColor
        val imageUrl = uiState.imageUrl

        val uiScope = rememberCoroutineScope()

        var graphicOverlay by remember { mutableStateOf<GraphicOverlay?>(null) }
        var currentMode by remember { mutableStateOf(DrawViewAction.ERASE_BACKGROUND) }
        var brushSize by remember { mutableFloatStateOf(50f) }
        var brushOffset by remember { mutableFloatStateOf(0f) }
        var finalBitmap: ImageBitmap? by remember { mutableStateOf(null) }
        var removeBackgroundBitmap: ImageBitmap? by remember { mutableStateOf(null) }
        val (selected, setSelected) = remember { mutableStateOf(0) }

        Logger.i(
            "CutOutImagePage",
            "Rendering with imageUrl: $imageUrl, isLoading: $isLoading, errorMessage: $errorMessage"
        )

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
            CommonTopBar(
                title = stringResource(id = R.string.cut_out),
                showGetProButton = false,
                showDoneButton = true,
                onBackClick = {
                    onBackClick.invoke()

                },
                onGetProClick = {
                    onGetProClick.invoke()

                },
                onDoneClick = {
                    graphicOverlay?.hideBrush()
                    currentMode = DrawViewAction.NONE

                    graphicOverlay?.getCurrentBitmap()?.let { bitmap ->
                        val transparentBitmap = Bitmap.createBitmap(
                            bitmap.width,
                            bitmap.height,
                            Bitmap.Config.ARGB_8888
                        ).apply { eraseColor(AndroidColor.TRANSPARENT) }
                        Canvas(transparentBitmap).drawBitmap(bitmap, 0f, 0f, null)
                        finalBitmap = transparentBitmap.asImageBitmap()
                    }
                }
            )

            if (isLoading) {
                LoaderFullScreen()
            } else {

                val item1 = stringResource(R.string.auto)
                val item2 = stringResource(R.string.manual)
                val items = remember {
                    listOf(
                        item1,
                        item2
                    )
                }
                var selectedIndex by remember {
                    mutableStateOf(0)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

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
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .background(Color.LightGray)
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.transparent_bg),
                                contentDescription = "Transparent Background",
                                modifier = Modifier
                                    .matchParentSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Load the image as a bitmap
                            val imageLoader = ImageLoader.Builder(context).build()
                            var bitmap by remember { mutableStateOf<Bitmap?>(null) }

                            LaunchedEffect(imageUrl) {
                                if (!imageUrl.isNullOrEmpty()) {
                                    try {
                                        val imageLoader = ImageLoader.Builder(context).build()
                                        val request = ImageRequest.Builder(context)
                                            .data(imageUrl)
                                            .target { drawable ->
                                                val newBitmap = when (drawable) {
                                                    is BitmapDrawable -> drawable.bitmap
                                                    else -> {
                                                        val bmp = createBitmap(
                                                            drawable.intrinsicWidth,
                                                            drawable.intrinsicHeight,
                                                            Bitmap.Config.ARGB_8888
                                                        )
                                                        val canvas = Canvas(bmp)
                                                        drawable.setBounds(
                                                            0,
                                                            0,
                                                            canvas.width,
                                                            canvas.height
                                                        )
                                                        drawable.draw(canvas)
                                                        bmp
                                                    }
                                                }

                                                // Update both bitmap state and GraphicOverlay
                                                bitmap = newBitmap
                                                graphicOverlay?.setBitmap(newBitmap)

                                                Logger.i(
                                                    "CutOutImageScreen",
                                                    "Image updated successfully: ${imageUrl}"
                                                )
                                            }
                                            .build()
                                        imageLoader.enqueue(request)
                                    } catch (e: Exception) {
                                        Logger.e(
                                            "CutOutImageScreen",
                                            "Failed to load image: ${e.message}",
                                            e
                                        )
                                    }
                                }
                            }

                            AndroidView(
                                factory = { ctx ->

                                    GraphicOverlay(context, null).apply {
                                        graphicOverlay = this
                                    }
                                },
                                update = { view ->
                                    if (view.getCurrentBitmap() == null && bitmap != null) {
                                        view.setBitmap(bitmap)
                                    }

                                    // Only update brush size if it actually changed
                                    if (view.getBrushSize() != brushSize) {
                                        view.setBrushSize(brushSize)
                                    }

                                    // Update brush offset
                                    if (view.getBrushOffset() != brushOffset) {
                                        view.setBrushOffset(brushOffset)
                                    }

                                    // Only update action if it actually changed
                                    if (view.getCurrentAction() != currentMode) {
                                        view.setAction(currentMode)
                                    }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .matchParentSize()
                            )
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        TextSwitch(
                            modifier = Modifier
                                .width(270.dp)
                                .align(Alignment.CenterHorizontally),
                            selectedIndex = selectedIndex,
                            items = items,
                            onSelectionChange = {
                                selectedIndex = it
                            }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    ) {
                        if (selectedIndex == 0) {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .align(Alignment.CenterHorizontally),
                                text = stringResource(R.string.remove_background_with_ai),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = colors.onSurfaceVariant
                            )

                            Box(
                                modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                                    .background(
                                        brush = premiumHorizontalGradientBrush,
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .bounceClick()
                                    .fillMaxWidth()
                                    .animateContentSize()
                                    .clickable(onClick = {
                                        if (currentMode != DrawViewAction.NONE) {
                                            graphicOverlay?.hideBrush()
                                            currentMode = DrawViewAction.NONE
                                        }

                                        graphicOverlay?.getCurrentBitmap()?.let { bitmap ->
                                            val transparentBitmap = Bitmap.createBitmap(
                                                bitmap.width,
                                                bitmap.height,
                                                Bitmap.Config.ARGB_8888
                                            ).apply { eraseColor(AndroidColor.TRANSPARENT) }
                                            Canvas(transparentBitmap).drawBitmap(
                                                bitmap,
                                                0f,
                                                0f,
                                                null
                                            )
                                            removeBackgroundBitmap =
                                                transparentBitmap.asImageBitmap()
                                        }
                                    }),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 18.dp, horizontal = 20.dp)
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .size(24.dp),
                                        painter = painterResource(id = R.drawable.ai_tab_item_icon),
                                        contentDescription = "Auto Remove Icon",
                                        tint = Color.White,
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        modifier = Modifier,
                                        text = stringResource(R.string.auto_remove),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        } else {

                            Row(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .align(Alignment.End)
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .clickable(onClick = {
                                            graphicOverlay?.undo()
                                        }),
                                    painter = painterResource(id = R.drawable.undo_icon),
                                    contentDescription = "Undo",
                                    tint = colors.onBackground,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .clickable(onClick = {
                                            graphicOverlay?.redo()
                                        }),
                                    painter = painterResource(id = R.drawable.redo_icon),
                                    contentDescription = "Redo",
                                    tint = colors.onBackground,
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            ) {
                                Row {

                                    Text(
                                        text = stringResource(R.string.size),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.onSurfaceVariant,
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Slider(
                                        value = brushSize,
                                        colors = SliderDefaults.colors(
                                            thumbColor = colors.primary, // White color for thumb
                                            activeTrackColor = colors.primary, // Primary fill color for active track
                                            inactiveTrackColor = colors.custom300 // Gray color for inactive track (adjust if custom100 is not gray)
                                        ),


                                        onValueChange = {
                                            brushSize = it
                                            graphicOverlay?.setBrushSize(it)
                                        },
                                        valueRange = 10f..100f,
                                    )
                                }

                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            ) {
                                Row {

                                    Text(
                                        text = stringResource(R.string.offset),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.onSurfaceVariant,
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Slider(
                                        value = brushOffset,
                                        colors = SliderDefaults.colors(
                                            thumbColor = colors.primary, // White color for thumb
                                            activeTrackColor = colors.primary, // Primary fill color for active track
                                            inactiveTrackColor = colors.custom300 // Gray color for inactive track (adjust if custom100 is not gray)
                                        ),
                                        onValueChange = {
                                            brushOffset = it
                                            graphicOverlay?.setBrushOffset(it)
                                        },
                                        valueRange = 0f..150f

                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            //tabs
                            val tabItems = listOf(
                                R.drawable.eraser_icon,
                                R.drawable.brush_tab_item_icon,
                                R.drawable.ic_transform
                            )

                            LaunchedEffect(selected) {
                                when (selected) {
                                    /*0 -> {
                                        if (currentMode != DrawViewAction.NONE) {
                                            graphicOverlay?.hideBrush()
                                            currentMode = DrawViewAction.NONE
                                        }

                                        graphicOverlay?.getCurrentBitmap()?.let { bitmap ->
                                            val transparentBitmap = Bitmap.createBitmap(
                                                bitmap.width,
                                                bitmap.height,
                                                Bitmap.Config.ARGB_8888
                                            ).apply { eraseColor(AndroidColor.TRANSPARENT) }
                                            Canvas(transparentBitmap).drawBitmap(bitmap, 0f, 0f, null)
                                            removeBackgroundBitmap = transparentBitmap.asImageBitmap()
                                        }

                                        setSelected(1) // Reset to eraser mode
                                    }*/

                                    0 -> {
                                        if (currentMode != DrawViewAction.ERASE_BACKGROUND) {
                                            currentMode = DrawViewAction.ERASE_BACKGROUND
                                            graphicOverlay?.setAction(DrawViewAction.ERASE_BACKGROUND)
                                        }
                                    }

                                    1 -> {
                                        if (currentMode != DrawViewAction.RECOVER_AREA) {
                                            currentMode = DrawViewAction.RECOVER_AREA
                                        }

                                    }

                                    2 -> {
                                        // Transform mode
                                        // switch overlay into transform mode
                                        graphicOverlay?.setAction(DrawViewAction.TRANSFORM)
                                        currentMode = DrawViewAction.TRANSFORM
                                    }

                                    /*4 -> {
                                        graphicOverlay?.hideBrush()
                                        currentMode = DrawViewAction.NONE

                                        graphicOverlay?.getCurrentBitmap()?.let { bitmap ->
                                            val transparentBitmap = Bitmap.createBitmap(
                                                bitmap.width,
                                                bitmap.height,
                                                Bitmap.Config.ARGB_8888
                                            ).apply { eraseColor(AndroidColor.TRANSPARENT) }
                                            Canvas(transparentBitmap).drawBitmap(bitmap, 0f, 0f, null)
                                            finalBitmap = transparentBitmap.asImageBitmap()
                                        }
                                        setSelected(1) // Reset to eraser mode
                                    }*/

                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            CustomTab(
                                selectedItemIndex = selected,
                                items = tabItems,
                                modifier = Modifier,
                                tabWidth = 130.dp,
                                onClick = setSelected
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                        }

                        var adViewLoadState by remember { mutableStateOf(true) }
                        var callback by remember { mutableStateOf(false) }

                        if (!isPremium) {

                            AnimatedVisibility(adViewLoadState) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateContentSize()
                                        .heightIn(min = 54.dp) // match banner height
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
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
                                            adUnit = AdIdsFactory.getBannerAdId(),
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

                finalBitmap?.let { bitmap ->
                    Dialog(onDismissRequest = { }) {
                        Column(
                            modifier = Modifier
                                .background(
                                    color = colors.background,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "${stringResource(R.string.preview_of_final_image)} \uD83D\uDC47",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.onBackground,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                            )
                            Spacer(Modifier.size(16.dp))
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Preview of ticket"
                            )
                            Spacer(Modifier.size(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { finalBitmap = null },
                                    modifier = Modifier
                                        .weight(1f) // Takes 50% of the width
                                        .padding(end = 3.dp) // Optional: small padding to separate buttons
                                ) {
                                    Text(
                                        stringResource(R.string.close),
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )
                                }

                                Spacer(Modifier.size(6.dp))

                                Button(
                                    onClick = {
                                        onSaveImage(bitmap)
                                        finalBitmap = null
                                    },
                                    modifier = Modifier
                                        .weight(1f) // Takes 50% of the width
                                        .padding(start = 3.dp) // Optional: small padding to separate buttons
                                ) {
                                    Text(
                                        stringResource(R.string.save),
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                removeBackgroundBitmap?.let { bitmap ->
                    Dialog(onDismissRequest = { }) {

                        var removeBg by remember { mutableStateOf(false) }
                        val boxModifier = if (removeBg) {
                            Modifier
                                .background(
                                    color = colors.background,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .animatedBorder(
                                    borderColors = listOf(Color.Red, Color.Green, Color.Blue),
                                    backgroundColor = colors.background,
                                    shape = RoundedCornerShape(16.dp),
                                    borderWidth = 3.dp,
                                    animationDurationInMillis = 2500
                                )
                                .animateContentSize()
                        } else {
                            Modifier
                                .background(
                                    color = colors.background,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .animateContentSize()
                        }
                        Box(
                            modifier = boxModifier,
                            contentAlignment = Alignment.Center
                        ) {


                            if (removeBg) {
                                LottieAnimation(
                                    composition = composition,
                                    modifier = Modifier
                                        .width(160.dp)
                                        .aspectRatio(1f)
                                        .align(Alignment.Center),
                                    iterations = LottieConstants.IterateForever,
                                )
                                LaunchedEffect(processingStage) {
                                    if (processingStage == ProcessingStage.COMPLETED ||
                                        processingStage == ProcessingStage.ERROR ||
                                        processingStage == ProcessingStage.NO_NETWORK_AVAILABLE
                                    ) {

                                        removeBackgroundBitmap = null
                                    }
                                }

                            } else {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .background(
                                            color = colors.background,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "${stringResource(R.string.do_you_want_to_remove_background)} \uD83D\uDC47",
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center,
                                        color = colors.onBackground,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally),
                                    )
                                    Spacer(Modifier.size(16.dp))
                                    Image(
                                        bitmap = bitmap,
                                        contentDescription = "Preview of Removed Background image"
                                    )
                                    Spacer(Modifier.size(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = { removeBackgroundBitmap = null },
                                            modifier = Modifier
                                                .weight(1f) // Takes 50% of the width
                                                .padding(end = 3.dp)
                                        ) {
                                            Text(
                                                stringResource(R.string.no),
                                                modifier = Modifier.padding(horizontal = 10.dp)
                                            )
                                        }

                                        Spacer(Modifier.size(6.dp))

                                        Button(
                                            onClick = {
                                                removeBg = true
                                                onRemoveBackgroundAi(bitmap)

                                            },
                                            modifier = Modifier
                                                .weight(1f) // Takes 50% of the width
                                                .padding(end = 3.dp)
                                        ) {
                                            Text(
                                                stringResource(R.string.yes),
                                                modifier = Modifier.padding(horizontal = 10.dp)
                                            )
                                        }
                                    }
                                }
                            }

                        }

                    }
                }

            }
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun ImageProcessingScreenPreview() {

    PreviewContainer {
        CutOutImageScreen(
            uiState = CutOutImageScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            isPremium = false,
            onBackClick = {},
            onGetProClick = {}
        )
    }
}