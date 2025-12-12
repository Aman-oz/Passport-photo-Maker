package com.ots.aipassportphotomaker.presentation.ui.editimage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Picture
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.AdaptiveBannerAd
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.common.ext.animatedBorder
import com.ots.aipassportphotomaker.common.ext.animatedGradient
import com.ots.aipassportphotomaker.common.ext.bounceClick
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.common.utils.ImageUtils.saveBitmapToGallery
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.ProcessingStage
import com.ots.aipassportphotomaker.domain.model.SuitsEntity
import com.ots.aipassportphotomaker.domain.repository.ColorFactory
import com.ots.aipassportphotomaker.domain.util.determinePixels
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.ColorItem
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.TextSwitch
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.AppColors
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.viewmodel.SharedViewModel
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import io.mhssn.colorpicker.ColorPickerDialog
import io.mhssn.colorpicker.ColorPickerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun EditImagePage(
    mainRouter: MainRouter,
    viewModel: EditImageScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel
) {
    val TAG = "EditImagePage"

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val suitsPaging = viewModel.suits.collectAsLazyPagingItems()

    val imageUrl = viewModel.imageUrl
    val selectedColor = viewModel.selectedColor
    val colorFactory = viewModel.colorFactory

    val localSelectedColor by viewModel.localSelectedColor.collectAsState(initial = Color.Transparent)


    Logger.d(TAG, "EditImagePage: Suits Paging: ${suitsPaging.itemCount} items loaded")

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "EditImagePage: Navigation State: $navigationState, ImageUrl: $imageUrl")
        when (navigationState) {
            is EditImageScreenNavigationState.CutOutScreen -> mainRouter.navigateToCutOutScreen(
                documentId = navigationState.documentId,
                imageUrl = uiState.imageUrl,
                selectedBackgroundColor = localSelectedColor,
                sourceScreen = "EditImageScreen"
            )

            is EditImageScreenNavigationState.SavedImageScreen -> {
                mainRouter.navigateToSavedImageScreen(
                    documentId = navigationState.documentId,
                    imagePath = uiState.imagePath,
                    selectedDpi = viewModel.selectedDpi,
                    sourceScreen = "EditImageScreen"
                )
            }
        }
    }


    val processingStage by viewModel.processingStage.collectAsState()
    val shouldRemoveBackground by viewModel.shouldRemoveBackground.collectAsState()

    // Get the activity-scoped SharedViewModel
    val activity = context as ComponentActivity
    val commonSharedViewModel: SharedViewModel = hiltViewModel(activity)
    val editedImageResult by commonSharedViewModel.editedImageResult.collectAsState()
    val removedBgResult by commonSharedViewModel.removedBgResult.collectAsState()

    LaunchedEffect(editedImageResult) {
        Logger.i(TAG, "LaunchedEffect triggered with: $editedImageResult")
        editedImageResult?.let { editedImageUrl ->
            Logger.i(TAG, "Received edited image URL: $editedImageUrl")
            viewModel.updateImageUrl(editedImageUrl)
            commonSharedViewModel.clearResult()
        }
    }

    LaunchedEffect(removedBgResult) {
        Logger.i(TAG, "LaunchedEffect triggered with: $editedImageResult")
        removedBgResult.let { isRemoved ->
            Logger.i(TAG, "Received isRemoved BG: $isRemoved")
            viewModel.updateIsBgRemoved(isRemoved)
            commonSharedViewModel.clearRemovedBgResult()
        }
    }

    LaunchedEffect(key1 = suitsPaging.loadState) {
        viewModel.onLoadStateUpdate(suitsPaging.loadState)
    }

    EditImageScreen(
        suits = suitsPaging,
        uiState = uiState,
        shouldRemoveBg = shouldRemoveBackground,
        processingStage = processingStage,
        selectedColor = localSelectedColor,
        colorFactory = colorFactory,
        isPremium = viewModel.isPremiumUser(),
        onImageSaved = { imagePath ->
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "btnSaveImage_EditImageScreen")
            viewModel.onImageSaved(imagePath)
            viewModel.showInterstitialAd(activity) { }
            //Rewarded ad
            /*viewModel.loadAndShowRewardedAd(
                activity,
                onAdClosed = {
                    viewModel.onImageSaved(imagePath)
                },
                onRewardedEarned = { isEarned ->
                    if (!isEarned) {
                        viewModel.onImageSaved(imagePath)
                        viewModel.showInterstitialAd(activity) { }
                    }

                })*/
        },
        onColorChange = { color, colorType ->
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "color_${colorType}_EditImageScreen")
            if (uiState.sourceScreen == "HomeScreen" && !uiState.isBgRemoved && shouldRemoveBackground) {
                uiState.imageUrl?.let {
                    viewModel.removeBackground(File(it))
                }
                viewModel.selectColor(color, colorType)
            } else {
                viewModel.selectColor(color, colorType)
            }
        },
        onBackClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "btnBack_EditImageScreen")
            mainRouter.goBack()

            viewModel.showInterstitialAd(activity) { }
        },
        onEraseClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "btnEraser_EditImageScreen")
            viewModel.onCutoutClicked()
        },
        onGetProClick = {
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "btnGetPro_EditImageScreen")
            mainRouter.navigateToPremiumScreen()
        },
        onEditPositionChange = { position ->
            viewModel.sendEvent(
                AnalyticsConstants.CLICKED,
                "editPosition_${if (position == 0) "backdrop" else "add_suit"}_EditImageScreen"
            )
            viewModel.updateEditPosition(position)
        },
        onSuitSelected = { suitUrl ->
            viewModel.sendEvent(AnalyticsConstants.CLICKED, "suit_EditImageScreen")
            viewModel.onSuitSelected(suitUrl)
        }
    )

    BackHandler {
        viewModel.sendEvent(AnalyticsConstants.CLICKED, "backPress_EditImageScreen")
        mainRouter.goBack()
        viewModel.showInterstitialAd(activity) { }
    }

}

val OffsetStateSaver = listSaver(
    save = { listOf(it.value.x, it.value.y) },
    restore = { mutableStateOf(Offset(it[0] as Float, it[1] as Float)) }
)
@OptIn(
    ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class,
    ExperimentalComposeApi::class
)
@SuppressLint("UseKtx")
@Composable
private fun EditImageScreen(
    suits: LazyPagingItems<SuitsEntity>,
    uiState: EditImageScreenUiState,
    shouldRemoveBg: Boolean = false,
    isPremium: Boolean = false,
    processingStage: ProcessingStage = ProcessingStage.NONE,
    selectedColor: Color? = null,
    colorFactory: ColorFactory,
    onImageSaved: (String) -> Unit = {},
    onColorChange: (Color, ColorFactory.ColorType) -> Unit,
    onEraseClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
    onEditPositionChange: (Int) -> Unit = {},
    onSuitSelected: (String?) -> Unit = {}
) {
    val TAG = "EditImageScreen"
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val picture = remember { Picture() }
    var selectedSuitId by remember { mutableStateOf<String>("none") }

    val writeStorageAccessState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            emptyList()
        } else {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    )

    val softwareImageLoader = remember {
        ImageLoader.Builder(context)
            .allowHardware(false)  // Force software bitmaps for Capturable compatibility
            .build()
    }

    Surface(
        modifier = Modifier
            .padding(bottom = systemBarsPadding.calculateBottomPadding())
    ) {

        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage
        val backgroundColor = uiState.selectedColor
        val imageUrl = uiState.imageUrl
        var showNoSuitsFound = uiState.showNoSuitsFound

        val captureController = rememberCaptureController()
        val uiScope = rememberCoroutineScope()
        var ticketBitmap: ImageBitmap? by remember { mutableStateOf(null) }
        var suitUrl: String? by remember { mutableStateOf(uiState.selectedSuitUrl) }
        var isSuitLoading by remember { mutableStateOf(false) } // Start with loading state

        var showSaveLoading by remember { mutableStateOf(false) }
        var isCreatingBitmap by remember { mutableStateOf(false) }

        val animatedColor by animateColorAsState(
            if (backgroundColor != Color.Unspecified) backgroundColor else Color.Transparent,
            label = "color"
        )

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai_erase))
//        val shouldRemoveBg by remember { mutableStateOf(uiState.sourceScreen == "HomeScreen" && shouldRemoveBg) }
        var showRemoveBackgroundDialog by remember { mutableStateOf(false) }

        showNoSuitsFound = suits.itemCount == 0

        Logger.i(
            "EditImagePage",
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
                title = stringResource(R.string.edit_image),
                showGetProButton = !isPremium,
                onBackClick = {
                    onBackClick.invoke()

                },
                onGetProClick = {
                    onGetProClick.invoke()

                }
            )

            if (isLoading) {
                LoaderFullScreen()
            } else {

                val pixelSize = determinePixels(uiState.documentPixels)

                //custom color picker dialog
                var showDialog by remember {
                    mutableStateOf(false)
                }

                ColorPickerDialog(
                    show = showDialog,
                    type = ColorPickerType.Classic(),
                    properties = DialogProperties(),
                    onDismissRequest = {
                        showDialog = false
                    },
                    onPickedColor = {
                        showDialog = false

                        if (shouldRemoveBg) {
                            showRemoveBackgroundDialog = true
                        }

                        onColorChange.invoke(
                            it,
                            ColorFactory.ColorType.CUSTOM
                        )
                        Logger.i("ChecklistItem", "Selected color: $it")
                    },
                )

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
//                    if (!imageUrl.isNullOrEmpty()) {

                        var flip by remember { mutableStateOf(false) }

                        var showLayers by rememberSaveable { mutableStateOf(false) }
                        var selectedLayer by remember { mutableIntStateOf(1) }

                        // Get the size of the parent Box
                        val boxWidth = remember { mutableStateOf(0f) }
                        val boxHeight = remember { mutableStateOf(0f) }
                        // Get the size of the parent Box
                        val boxWidthForSuit = remember { mutableStateOf(0f) }
                        val boxHeightForSuit = remember { mutableStateOf(0f) }

                        var isLayoutReady by remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .background(Color.LightGray)

                        ) {
                            // background layer
                            Image(
                                painter = painterResource(id = R.drawable.transparent_bg),
                                contentDescription = "Transparent Background",
                                modifier = Modifier
                                    .matchParentSize(),
                                contentScale = ContentScale.Crop
                            )

                            var isImageLoading by remember { mutableStateOf(true) } // Start with loading state

                            /*var imageScale by remember { mutableStateOf(1f) }
                            var imageRotation by remember { mutableStateOf(0f) }
                            var imageOffset by remember { mutableStateOf(Offset.Zero) }
                            var suitScale by remember { mutableStateOf(1f) }
                            var suitRotation by remember { mutableStateOf(0f) }
                            var suitOffset by remember { mutableStateOf(Offset.Zero) }*/

                            // primitives: keep as MutableState via rememberSaveable returning MutableState
                            var imageScale by rememberSaveable { mutableStateOf(1f) }
                            var imageRotation by rememberSaveable { mutableStateOf(0f) }

                            var suitScale by rememberSaveable { mutableStateOf(1f) }
                            var suitRotation by rememberSaveable { mutableStateOf(0f) }

// Offsets: rememberSaveable with saver must return MutableState if you want `by` to expose Offset
                            var imageOffset by rememberSaveable(
                                saver = OffsetStateSaver,
                                init = { mutableStateOf(Offset.Zero) }
                            )

                            var suitOffset by rememberSaveable(
                                saver = OffsetStateSaver,
                                init = { mutableStateOf(Offset.Zero) }
                            )


                            Box(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxHeight()
                                    .aspectRatio(uiState.ratio)
                                    .align(Alignment.Center)
                                    .capturable(captureController)
                                    .graphicsLayer(scaleX = if (!showLayers) if (flip) -1f else 1f else 1f)
                            ) {

                                // Main image modifier with conditional pointer input
                                val mainImageBaseModifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(uiState.ratio)
                                    .background(animatedColor)
                                    .align(Alignment.Center)
                                    .clipToBounds()
                                    .onGloballyPositioned { coordinates ->
                                        boxWidth.value = coordinates.size.width.toFloat()
                                        boxHeight.value = coordinates.size.height.toFloat()

                                        Logger.i(
                                            "EditImageScreen",
                                            "Box width: ${boxWidth.value}, height: ${boxHeight.value}, Selected Document size: ${uiState.documentSize}"
                                        )
                                        if (!isLayoutReady) isLayoutReady = true
                                    }
                                    .graphicsLayer(
                                        // Apply flip only to main image
                                        scaleX = imageScale * (
                                                if (selectedLayer == 1 && showLayers) {
                                                    if (flip) -1f else 1f
                                                } else {
                                                    1f
                                                }),
                                        scaleY = imageScale,
                                        rotationZ = imageRotation,
                                        translationX = imageOffset.x,
                                        translationY = imageOffset.y
                                    )

                                val mainImagePointerModifier = if (selectedLayer == 1) {
                                    Modifier.pointerInput(Unit) {
                                        detectTransformGestures { _, pan, zoom, rotate ->
                                            imageScale *= zoom
                                            imageRotation += rotate
                                            imageOffset += pan
                                        }
                                    }
                                } else {
                                    Modifier
                                }

                                val mainImageModifier =
                                    mainImageBaseModifier.then(mainImagePointerModifier)

                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    imageLoader = softwareImageLoader,
                                    contentDescription = "Edited Image",
                                    modifier = mainImageModifier,
                                    contentScale = ContentScale.Fit,
                                    onState = { state ->
                                        isImageLoading = state is AsyncImagePainter.State.Loading
                                    }
                                )

                                suitUrl?.let { url ->

                                    // Suit modifier with conditional pointer input
                                    val suitBaseModifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(uiState.ratio)
                                        .align(Alignment.BottomCenter)
                                        .clipToBounds()
                                        .onGloballyPositioned { coordinates ->
                                            boxWidthForSuit.value =
                                                coordinates.size.width.toFloat()
                                            boxHeightForSuit.value =
                                                coordinates.size.height.toFloat()

                                            Logger.i(
                                                "EditImageScreen",
                                                "Box width: ${boxWidthForSuit.value}, height: ${boxHeightForSuit.value}, Selected Document size: ${uiState.documentSize}"
                                            )
                                            if (!isLayoutReady) isLayoutReady = true
                                        }
                                        .graphicsLayer(
                                            scaleX = suitScale * (
                                                    if (selectedLayer == 0 && showLayers) {
                                                        if (flip) -1f else 1f
                                                    } else {
                                                        1f
                                                    }
                                                    ),
                                            scaleY = suitScale,
                                            rotationZ = suitRotation,
                                            translationX = suitOffset.x,
                                            translationY = suitOffset.y
                                        )

                                    val suitPointerModifier = if (selectedLayer == 0) {
                                        Modifier.pointerInput(Unit) {
                                            detectTransformGestures { _, pan, zoom, rotate ->
                                                suitScale *= zoom
                                                suitRotation += rotate
                                                suitOffset += pan
                                            }
                                        }
                                    } else {
                                        Modifier
                                    }

                                    val suitModifier = suitBaseModifier.then(suitPointerModifier)

                                    AsyncImage(
                                        model =
                                            ImageRequest.Builder(context)
                                                .data(url)
                                                .crossfade(true)
                                                .build(),
                                        imageLoader = softwareImageLoader,
                                        contentDescription = "Suit Image",
                                        contentScale = ContentScale.Fit,
                                        onState = { state ->
                                            isSuitLoading = state is AsyncImagePainter.State.Loading
                                        },
                                        modifier = suitModifier
                                    )
                                }
                            }

                            if (isImageLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(48.dp),
                                    color = colors.primary
                                )
                            }

                            if (isSuitLoading) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = colors.background,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .size(60.dp)
                                        .align(Alignment.Center),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(28.dp),
                                        color = colors.primary,
                                        strokeWidth = 2.dp
                                    )
                                }

                            }

                            // Add control buttons
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .background(Color.Transparent)
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {

                                Card(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally),
                                    shape = CircleShape,
                                    colors = CardDefaults.cardColors(containerColor = colors.primary),
                                    elevation = CardDefaults.cardElevation(20.dp),
                                    border = BorderStroke(
                                        width = 2.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                colors.primary,
                                                colors.background
                                            )
                                        )
                                    ),
                                    onClick = {
                                        flip = !flip
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = if (flip) R.drawable.flip_icon_left else R.drawable.flip_icon),
                                        contentDescription = "Flip",
                                        modifier = Modifier.padding(12.dp),
                                        tint = colors.onPrimary
                                    )
                                }

                                Card(
                                    shape = CircleShape,
                                    colors = CardDefaults.cardColors(containerColor = colors.primary),
                                    elevation = CardDefaults.cardElevation(20.dp),
                                    border = BorderStroke(
                                        width = 2.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                colors.primary,
                                                colors.background
                                            )
                                        )
                                    ),
                                    onClick = {
                                        onEraseClick.invoke()
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.eraser_icon),
                                        contentDescription = "Eraser",
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }


                            Column(
                                modifier = Modifier
                                    .background(Color.Transparent)
                                    .align(Alignment.TopStart)
                                    .padding(16.dp)
                                    .animateContentSize(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Card(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .animateContentSize(),
                                    shape = if (showLayers) RoundedCornerShape(4.dp) else CircleShape,
                                    colors = CardDefaults.cardColors(containerColor = colors.primary),
                                    elevation = CardDefaults.cardElevation(20.dp),
                                    border = BorderStroke(
                                        width = 2.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                colors.primary,
                                                colors.background
                                            )
                                        )
                                    ),
                                    onClick = {
                                        showLayers = !showLayers
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_layers),
                                        contentDescription = "Flip",
                                        modifier = Modifier.padding(12.dp),
                                        tint = colors.onPrimary
                                    )
                                }

                                if (showLayers) {

                                    suitUrl?.let { url ->
                                        Card(
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .size(50.dp),
                                            shape = RoundedCornerShape(4.dp),
                                            border = if (selectedLayer == 0) {
                                                BorderStroke(

                                                    width = 3.dp,
                                                    brush = Brush.linearGradient(
                                                        colors = listOf(
                                                            colors.primary,
                                                            colors.background
                                                        )
                                                    )
                                                )
                                            } else {
                                                BorderStroke(

                                                    width = 2.dp,
                                                    brush = Brush.linearGradient(
                                                        colors = listOf(
                                                            colors.background,
                                                            colors.background
                                                        )
                                                    )
                                                )
                                            },
                                            colors = CardDefaults.cardColors(containerColor = colors.background),
                                            elevation = CardDefaults.cardElevation(20.dp),
                                            onClick = {
                                                selectedLayer = 0
                                            }
                                        ) {
                                            AsyncImage(
                                                model =
                                                    ImageRequest.Builder(context)
                                                        .data(url)
                                                        .crossfade(true)
                                                        .build(),
                                                imageLoader = softwareImageLoader,
                                                contentDescription = "Suit Layer",
                                                modifier = Modifier.fillMaxSize()
                                            )

                                        }
                                    }
                                    Card(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .size(50.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        colors = CardDefaults.cardColors(containerColor = colors.background),
                                        border = if (selectedLayer == 1) {
                                            BorderStroke(

                                                width = 3.dp,
                                                brush = Brush.linearGradient(
                                                    colors = listOf(
                                                        colors.primary,
                                                        colors.background
                                                    )
                                                )
                                            )
                                        } else {
                                            BorderStroke(

                                                width = 2.dp,
                                                brush = Brush.linearGradient(
                                                    colors = listOf(
                                                        colors.background,
                                                        colors.background
                                                    )
                                                )
                                            )
                                        },
                                        elevation = CardDefaults.cardElevation(20.dp),
                                        onClick = {
                                            selectedLayer = 1
                                        }
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(imageUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentScale = ContentScale.FillBounds,
                                            imageLoader = softwareImageLoader,
                                            contentDescription = "Edited Image Layer",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
//                    }

                        Spacer(modifier = Modifier.height(16.dp))

                        val item1 = stringResource(R.string.backdrop)
                        val item2 = stringResource(R.string.add_suit)
                        val items = remember {
                            listOf(
                                item1,
                                item2
                            )
                        }
                        var selectedIndex by remember {
                            mutableStateOf(uiState.editPosition)
                        }

                        TextSwitch(
                            modifier = Modifier
                                .width(270.dp)
                                .align(Alignment.CenterHorizontally),
                            selectedIndex = selectedIndex,
                            items = items,
                            onSelectionChange = {
                                selectedIndex = it
                                onEditPositionChange.invoke(it)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (selectedIndex == 0) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {

                                Spacer(modifier = Modifier.width(40.dp))

                                // 1st: Custom Color (Color Picker)
                                val isCustomSelected =
                                    colorFactory.selectedColorType == ColorFactory.ColorType.CUSTOM &&
                                            colorFactory.customColor == selectedColor

                                ColorItem(
                                    modifier = Modifier.width(42.dp),
                                    color = Color.White,
                                    ratio = 1.2f,
                                    showEyeDropper = true,
                                    isSelected = isCustomSelected,
                                    onClick = {
                                        showDialog = true
                                    }
                                )


                                val isTransparentSelected =
                                    colorFactory.selectedColorType == ColorFactory.ColorType.TRANSPARENT &&
                                            colorFactory.getColorByType(ColorFactory.ColorType.TRANSPARENT) == selectedColor
                                ColorItem(
                                    modifier = Modifier
                                        .width(42.dp),
                                    color = Color.Transparent,
                                    ratio = 1.2f,
                                    showTransparentBg = true,
                                    isSelected = isTransparentSelected,
                                    onClick = {
                                        if (shouldRemoveBg) {
                                            showRemoveBackgroundDialog = true
                                        }

                                        onColorChange.invoke(
                                            Color.Transparent,
                                            ColorFactory.ColorType.TRANSPARENT
                                        )
                                    }

                                )

                                val isWhiteSelected =
                                    colorFactory.selectedColorType == ColorFactory.ColorType.WHITE &&
                                            colorFactory.getColorByType(ColorFactory.ColorType.WHITE) == selectedColor
                                ColorItem(
                                    modifier = Modifier
                                        .width(42.dp),
                                    color = Color.White,
                                    ratio = 1.2f,
                                    isSelected = isWhiteSelected,
                                    onClick = {
                                        if (shouldRemoveBg) {
                                            showRemoveBackgroundDialog = true
                                        }
                                        onColorChange.invoke(
                                            Color.White,
                                            ColorFactory.ColorType.WHITE
                                        )
                                    }

                                )

                                val isGreenSelected =
                                    colorFactory.selectedColorType == ColorFactory.ColorType.GREEN &&
                                            colorFactory.getColorByType(ColorFactory.ColorType.GREEN) == selectedColor
                                ColorItem(
                                    modifier = Modifier
                                        .width(42.dp),
                                    color = Color.Green,
                                    ratio = 1.2f,
                                    isSelected = isGreenSelected,
                                    onClick = {
                                        if (shouldRemoveBg) {
                                            showRemoveBackgroundDialog = true
                                        }
                                        onColorChange.invoke(
                                            Color.Green,
                                            ColorFactory.ColorType.GREEN
                                        )
                                    }

                                )

                                val isBlueSelected =
                                    colorFactory.selectedColorType == ColorFactory.ColorType.BLUE &&
                                            colorFactory.getColorByType(ColorFactory.ColorType.BLUE) == selectedColor
                                ColorItem(
                                    modifier = Modifier
                                        .width(42.dp),
                                    color = AppColors.LightPrimary,
                                    ratio = 1.2f,
                                    isSelected = isBlueSelected,
                                    onClick = {
                                        if (shouldRemoveBg) {
                                            showRemoveBackgroundDialog = true
                                        }
                                        onColorChange.invoke(
                                            AppColors.LightPrimary,
                                            ColorFactory.ColorType.BLUE
                                        )
                                    }

                                )

                                val isRedSelected =
                                    colorFactory.selectedColorType == ColorFactory.ColorType.RED &&
                                            colorFactory.getColorByType(ColorFactory.ColorType.RED) == selectedColor
                                ColorItem(
                                    modifier = Modifier
                                        .width(42.dp),
                                    color = Color.Red,
                                    ratio = 1.2f,
                                    isSelected = isRedSelected,
                                    onClick = {
                                        if (shouldRemoveBg) {
                                            showRemoveBackgroundDialog = true
                                        }
                                        onColorChange.invoke(Color.Red, ColorFactory.ColorType.RED)
                                    }

                                )
                            }
                        } else {
                            SuitsList(
                                suits = suits,
                                onItemClick = { selectedSuit ->
                                    selectedSuitId = selectedSuit._id
                                    suitUrl = if (selectedSuitId == "none") {
                                        if (showLayers) showLayers = false
                                        if (selectedLayer != 1) selectedLayer = 1
                                        null
                                    } else {
                                        if (!showLayers) showLayers = true
                                        if (selectedLayer != 0) selectedLayer = 0

                                        isSuitLoading = true
                                        selectedSuit.image
                                    }

                                    onSuitSelected.invoke(suitUrl)
                                },
                                selectedSuitId = selectedSuitId,
                                onSelectionChange = { suit, isSelected ->
                                    {
                                        selectedSuitId = if (isSelected) suit._id else "none"
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ticketBitmap?.let { bitmap ->
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
                                            modifier = Modifier
                                                .weight(1f) // Takes 50% of the width
                                                .padding(end = 3.dp),
                                            onClick = { ticketBitmap = null }) {
                                            Text(stringResource(R.string.close))
                                        }

                                        Spacer(Modifier.size(6.dp))

                                        Button(
                                            modifier = Modifier
                                                .weight(1f) // Takes 50% of the width
                                                .padding(start = 3.dp),
                                            onClick = {
                                                val permissionsGranted =
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                                        true
                                                    } else {
                                                        writeStorageAccessState.allPermissionsGranted
                                                    }

                                                showSaveLoading = true

                                                uiScope.launch {
                                                    if (permissionsGranted) {
                                                        withContext(Dispatchers.IO) {
                                                            val savedUri = saveBitmapToGallery(
                                                                context,
                                                                ticketBitmap?.asAndroidBitmap()
                                                            )
                                                            showSaveLoading = false
                                                            withContext(Dispatchers.Main) {
                                                                if (savedUri != null) {
                                                                    // Now you have the saved image URI
                                                                    ticketBitmap = null
                                                                    val imagePath =
                                                                        savedUri.toString()
                                                                    onImageSaved(imagePath)
                                                                    Logger.d(
                                                                        "EditImageScreen",
                                                                        "Image saved to: $imagePath"
                                                                    )
                                                                    /*Toast.makeText(
                                                                        context,
                                                                        "Image saved to gallery",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()*/
                                                                } else {
                                                                    Toast.makeText(
                                                                        context,
                                                                        context.getString(R.string.failed_to_save_image),
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                            }
                                                        }
                                                    } else if (writeStorageAccessState.shouldShowRationale) {
                                                        coroutineScope.launch {
                                                            val result =
                                                                snackbarHostState.showSnackbar(
                                                                    message = context.getString(R.string.the_storage_permission_is_needed_to_save_the_image),
                                                                    actionLabel = context.getString(
                                                                        R.string.grant_access
                                                                    )
                                                                )
                                                            if (result == SnackbarResult.ActionPerformed) {
                                                                writeStorageAccessState.launchMultiplePermissionRequest()
                                                            }
                                                        }
                                                    } else {
                                                        writeStorageAccessState.launchMultiplePermissionRequest()
                                                    }
                                                }

                                            }) {
                                            Text(stringResource(R.string.save))
                                        }
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(showRemoveBackgroundDialog) {
                            Dialog(onDismissRequest = { }) {

                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = colors.background,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .animatedBorder(
                                            borderColors = listOf(
                                                Color.Red,
                                                Color.Green,
                                                Color.Blue
                                            ),
                                            backgroundColor = colors.background,
                                            shape = RoundedCornerShape(16.dp),
                                            borderWidth = 3.dp,
                                            animationDurationInMillis = 2500
                                        )
                                        .animateContentSize(),
                                    contentAlignment = Alignment.Center
                                ) {

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
                                            showRemoveBackgroundDialog = false
                                        }
                                    }
                                }

                            }
                        }

                        Spacer(Modifier.size(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bounceClick()
                                .padding(horizontal = 16.dp)
                                .height(48.dp)
                                .animatedGradient(colors.primary, colors.primaryContainer)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                        },
                                        onTap = {
                                            if (boxWidth.value <= 0 || boxHeight.value <= 0 || !isLayoutReady) {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.layout_not_ready_please_wait),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@detectTapGestures
                                            }

                                            val fallbackWidth = boxWidth.value.toInt().coerceAtLeast(1).coerceAtMost(4096)  // Cap for sanity
                                            val fallbackHeight = boxHeight.value.toInt().coerceAtLeast(1).coerceAtMost(4096)
                                            if (fallbackWidth <= 0 || fallbackHeight <= 0 || !isLayoutReady) {
                                                Logger.w(TAG, "Layout not ready: box=${boxWidth.value}x${boxHeight.value}, ready=$isLayoutReady")
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.layout_not_ready_please_wait),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@detectTapGestures
                                            }

                                            isCreatingBitmap = true

                                            uiScope.launch {
                                                // Capture the screen content
                                                try {
                                                    val capturedBitmap =
                                                        captureController.captureAsync().await()
                                                    val capturedAndroid =
                                                        capturedBitmap.asAndroidBitmap()

                                                    val targetWidth =
                                                        if (pixelSize.width > 0) pixelSize.width else capturedAndroid.width
                                                    val targetHeight =
                                                        if (pixelSize.height > 0) pixelSize.height else capturedAndroid.height

                                                    if (targetWidth == 0 || targetHeight == 0) {
                                                        // Double-check: If still invalid, abort and show error.
                                                        Logger.e(
                                                            TAG,
                                                            "Invalid target dimensions: ${pixelSize.width}x${pixelSize.height}"
                                                        )
                                                        withContext(Dispatchers.Main) {
                                                            Toast.makeText(
                                                                context,
                                                                "Something went wrong. Please try again.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                        isCreatingBitmap = false
                                                        return@launch
                                                    }

                                                    // Create a new bitmap with source dimensions but containing captured content
                                                    val resultBitmap = Bitmap.createScaledBitmap(
                                                        capturedAndroid,
                                                        targetWidth,
                                                        targetHeight,
                                                        false
                                                    )

                                                    // Assign the result to ticketBitmap
                                                    withContext(Dispatchers.Main) {
                                                        ticketBitmap = resultBitmap.asImageBitmap()
                                                    }

                                                    isCreatingBitmap = false
                                                } catch (e: IllegalStateException) {
                                                    if (e.message?.contains("Software rendering") == true) {
                                                        Logger.e(
                                                            TAG,
                                                            "Capture failed: Hardware bitmap issue",
                                                            e
                                                        )
                                                    } else throw e
                                                }

                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCreatingBitmap) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp),
                                    color = colors.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                        .align(Alignment.Center)
                                ) {
                                    Row(
                                        modifier = Modifier.align(Alignment.Center)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.download_icon),
                                            contentDescription = "Save Icon",
                                            tint = colors.onPrimary,
                                            modifier = Modifier
                                                .size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(R.string.save_to_gallery),
                                            color = colors.onPrimary,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )


                                    }
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_back),
                                        contentDescription = null,
                                        tint = colors.onPrimary,
                                        modifier = Modifier
                                            .padding(end = 10.dp)
                                            .rotate(180f)
                                            .align(Alignment.CenterEnd)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

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
                                                    .animateContentSize()
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

                                        /*AdMobCollapsableBanner(
                                            adUnit = AdIdsFactory.getBannerAdId(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .animateContentSize()
                                                .align(Alignment.Center),
                                            adSize = AdSize.FULL_BANNER, // or adaptive size if needed
                                            collapseDirection = CollapseDirection.BOTTOM,
                                            onAdLoaded = { isLoaded ->
                                                adLoadState = isLoaded
                                                Logger.d(TAG, "AdMobBanner: onAdLoaded: $isLoaded")
                                            }
                                        )*/
                                    }
                                }
                            }

                            // Add SnackbarHost to display permission rationale
                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }

                    }

                    if (showSaveLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(48.dp),
                                    color = colors.primary
                                )
                                Text(
                                    text = stringResource(R.string.saving_image_please_wait),
                                    color = Color.White
                                )
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
    val suits = flowOf(
        PagingData.from(
            listOf<SuitsEntity>(
                SuitsEntity(
                    _id = "0001",
                    name = "id_suits_1",
                    image = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/original/id_suits_1.png",
                    thumbnail = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/thumbnail/id_suits_1.png",
                    isPremium = false
                ),
                SuitsEntity(
                    _id = "0002",
                    name = "id_suits_2",
                    image = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/original/id_suits_2.png",
                    thumbnail = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/thumbnail/id_suits_2.png",
                    isPremium = false
                ),
                SuitsEntity(
                    _id = "0003",
                    name = "id_suits_3",
                    image = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/original/id_suits_3.png",
                    thumbnail = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/thumbnail/id_suits_3.png",
                    isPremium = false
                ),
                SuitsEntity(
                    _id = "0004",
                    name = "id_suits_4",
                    image = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/original/id_suits_4.png",
                    thumbnail = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/thumbnail/id_suits_4.png",
                    isPremium = false
                ),
                SuitsEntity(
                    _id = "0005",
                    name = "id_suits_5",
                    image = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/original/id_suits_5.png",
                    thumbnail = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/thumbnail/id_suits_5.png",
                    isPremium = false
                ),
                SuitsEntity(
                    _id = "0006",
                    name = "id_suits_6",
                    image = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/original/id_suits_6.png",
                    thumbnail = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/thumbnail/id_suits_6.png",
                    isPremium = false
                ),
                SuitsEntity(
                    _id = "0007",
                    name = "id_suits_7",
                    image = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/original/id_suits_7.png",
                    thumbnail = "https://d7seqll9ts29z.cloudfront.net/Photo_ID_Maker_v1.0/thumbnail/id_suits_7.png",
                    isPremium = false
                )
            )
        )
    ).collectAsLazyPagingItems()
    PreviewContainer {
        EditImageScreen(
            suits = suits,
            uiState = EditImageScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            colorFactory = ColorFactory(),
            onColorChange = { _, _ -> },
            onEraseClick = {},
            onBackClick = {},
            onGetProClick = {}
        )
    }
}

// Helper functions (place these in a utils file or the same file)
private fun createBitmapFromPicture(picture: Picture): Bitmap {
    val bitmap = Bitmap.createBitmap(
        picture.width,
        picture.height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE) // Optional: Set background color
    canvas.drawPicture(picture)
    return bitmap
}

private suspend fun Bitmap.saveToGallery(context: Context): Uri {
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "edited_image-${System.currentTimeMillis()}.png"
    )

    file.writeBitmap(this, Bitmap.CompressFormat.PNG, 100)

    return scanFilePath(context, file.path) ?: throw Exception("File could not be saved")
}

private suspend fun scanFilePath(context: Context, filePath: String): Uri? {
    return suspendCancellableCoroutine { continuation ->
        MediaScannerConnection.scanFile(
            context,
            arrayOf(filePath),
            arrayOf("image/png")
        ) { _, scannedUri ->
            if (scannedUri == null) {
                continuation.cancel(Exception("File $filePath could not be scanned"))
            } else {
                continuation.resume(scannedUri) {
                    Logger.e("EditImageScreen", "Resuming scanFilePath: $it")
                }
            }
        }
    }
}

private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}