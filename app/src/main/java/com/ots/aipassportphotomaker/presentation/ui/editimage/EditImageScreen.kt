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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.ImageUtils.loadAndTransformBitmap
import com.ots.aipassportphotomaker.common.utils.ImageUtils.saveBitmapToGallery
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.SuitsEntity
import com.ots.aipassportphotomaker.domain.repository.ColorFactory
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.ColorItem
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.TextSwitch
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.AppColors
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
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
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "EditImagePage"

    val uiState by viewModel.uiState.collectAsState()


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
                imageUrl = imageUrl,
                selectedBackgroundColor = localSelectedColor
            )

            is EditImageScreenNavigationState.SavedImageScreen -> {
                    mainRouter.navigateToSavedImageScreen(
                        documentId = navigationState.documentId,
                        imagePath = uiState.imagePath,
                    )
            }
        }
    }

    LaunchedEffect(key1 = suitsPaging.loadState) {
        viewModel.onLoadStateUpdate(suitsPaging.loadState)
    }

    EditImageScreen(
        suits = suitsPaging,
        uiState = uiState,
        selectedColor = localSelectedColor,
        colorFactory = colorFactory,
        onImageSaved = { imagePath ->
            viewModel.onImageSaved(imagePath)
        },
        onColorChange = { color, colorType ->
            viewModel.selectColor(color, colorType)
        },
        onBackClick = { mainRouter.goBack() },
        onEraseClick = {
            viewModel.onCutoutClicked()
        },
        onGetProClick = { }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UseKtx")
@Composable
private fun EditImageScreen(
    suits: LazyPagingItems<SuitsEntity>,
    uiState: EditImageScreenUiState,
    selectedColor: Color? = null,
    colorFactory: ColorFactory,
    onImageSaved: (String) -> Unit = {},
    onColorChange: (Color, ColorFactory.ColorType) -> Unit,
    onEraseClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
) {


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

    Surface {

        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage
        val backgroundColor = uiState.selectedColor
        val imageUrl = uiState.imageUrl
        var showNoSuitsFound = uiState.showNoSuitsFound
        
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
                title = "Edit image",
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
                    var offsetX by remember { mutableStateOf(0f) }
                    var offsetY by remember { mutableStateOf(0f) }
                    var scale by remember { mutableStateOf(1f) }
                    val maxScale = 3f
                    val minScale = 0.5f

                    // Get the size of the parent Box
                    val boxWidth = remember { mutableStateOf(0f) }
                    val boxHeight = remember { mutableStateOf(0f) }
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

                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Edited Image",
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxHeight()
                                .aspectRatio(uiState.ratio)
                                .background(if (backgroundColor != Color.Unspecified) backgroundColor else colors.primary)
                                .align(Alignment.Center)
                                .clipToBounds()
                                .onGloballyPositioned { coordinates ->
                                    boxWidth.value = coordinates.size.width.toFloat()
                                    boxHeight.value = coordinates.size.height.toFloat()

                                    Logger.i("EditImageScreen", "Box width: ${boxWidth.value}, height: ${boxHeight.value}, Selected Document size: ${uiState.documentSize}")
                                    if (!isLayoutReady) isLayoutReady = true
                                }
                                .graphicsLayer(scaleX = if (flip) -1f else 1f)
                                /*.pointerInput(Unit) {
                                    detectTransformGestures(
                                    ) { centroid, pan, zoom, rotation ->
                                        // Update scale first (but clamp to min/max values)
                                        val newScale = (scale * zoom).coerceIn(minScale, maxScale)

                                        // Calculate the bounds based on new scale - avoid using coerceIn with potentially invalid bounds
                                        val scaledWidth = boxWidth.value * newScale
                                        val scaledHeight = boxHeight.value * newScale

                                        // Calculate max offset allowed (avoid negative bounds when box is smaller than container)
                                        val maxOffsetX = maxOf(0f, (scaledWidth - boxWidth.value) / 2)
                                        val maxOffsetY = maxOf(0f, (scaledHeight - boxHeight.value) / 2)

                                        // Apply bounds safely
                                        val newOffsetX = offsetX + pan.x / scale
                                        offsetX = if (maxOffsetX > 0f) newOffsetX.coerceIn(-maxOffsetX, maxOffsetX) else 0f

                                        val newOffsetY = offsetY + pan.y / scale
                                        offsetY = if (maxOffsetY > 0f) newOffsetY.coerceIn(-maxOffsetY, maxOffsetY) else 0f

                                        // Finally update the scale
                                        scale = newScale
                                    }
                                }
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = {  *//*Optional: Handle drag start*//*  },
                                        onDragEnd = {  *//*Optional: Handle drag end*//*  }
                                    ) { change, dragAmount ->

                                        change.consume()
                                        val scaledWidth = boxWidth.value * scale
                                        val scaledHeight = boxHeight.value * scale

                                        // Calculate max offset allowed (avoid negative bounds)
                                        val maxOffsetX = maxOf(0f, (scaledWidth - boxWidth.value) / 2)
                                        val maxOffsetY = maxOf(0f, (scaledHeight - boxHeight.value) / 2)

                                        // Apply bounds safely
                                        val newOffsetX = offsetX + dragAmount.x / scale
                                        offsetX = if (maxOffsetX > 0f) newOffsetX.coerceIn(-maxOffsetX, maxOffsetX) else 0f

                                        val newOffsetY = offsetY + dragAmount.y / scale
                                        offsetY = if (maxOffsetY > 0f) newOffsetY.coerceIn(-maxOffsetY, maxOffsetY) else 0f
                                    }
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onDoubleTap = {
                                            scale = 1f
                                            offsetX = 0f
                                            offsetY = 0f
                                        }
                                    )
                                }*/,
                            contentScale = ContentScale.Fit,
                            onState = { state ->
                                isImageLoading = state is AsyncImagePainter.State.Loading
                            }
                        )

                        if (isImageLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(48.dp),
                                color = colors.primary
                            )
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
                                elevation = CardDefaults.cardElevation(10.dp),
                                onClick = {
                                    flip = !flip
                                }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.flip_icon),
                                    contentDescription = "Flip",
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Card(
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = colors.primary),
                                elevation = CardDefaults.cardElevation(10.dp),
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
                    }
//                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val items = remember {
                        listOf("Backdrop", "Add suit")
                    }
                    var selectedIndex by remember {
                        mutableStateOf(0)
                    }

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
                                    onColorChange.invoke(Color.White, ColorFactory.ColorType.CUSTOM)
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
                                    onColorChange.invoke(Color.White, ColorFactory.ColorType.WHITE)
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
                                    onColorChange.invoke(Color.Green, ColorFactory.ColorType.GREEN)
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
                                    onColorChange.invoke(Color.Red, ColorFactory.ColorType.RED)
                                }

                            )
                        }
                    } else {
                        SuitsList(
                            suits = suits,
                            onItemClick = { selectedSuit ->
                                selectedSuitId = selectedSuit._id
                            },
                            selectedSuitId = selectedSuitId,
                            onSelectionChange = { suit, isSelected -> {
                                selectedSuitId = if (isSelected) suit._id else "none"
                            }}
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {

                            if (boxWidth.value <= 0 || boxHeight.value <= 0 || !isLayoutReady) {
                                Toast.makeText(context, "Layout not ready, please wait", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val permissionsGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                true
                            } else {
                                writeStorageAccessState.allPermissionsGranted
                            }

                            if (permissionsGranted) {
                                /*coroutineScope.launch {
                                    try {


                                        // Get the source image from the URL
                                        val sourceBitmap = loadAndTransformBitmap(context, imageUrl) ?: run {
                                            Toast.makeText(context, "Could not load image", Toast.LENGTH_SHORT).show()
                                            return@launch
                                        }

                                        val imageWidth = sourceBitmap.width * scale
                                        val imageHeight = sourceBitmap.height * scale

                                        // Create a bitmap with background color
                                        val bitmap = Bitmap.createBitmap(
                                            imageWidth.toInt(),
                                            imageHeight.toInt(),
                                            Bitmap.Config.ARGB_8888
                                        )
                                        val canvas = android.graphics.Canvas(bitmap)

                                        // Fill with background color
                                        canvas.drawColor(backgroundColor.toArgb())

                                        // Apply transformations
                                        val matrix = android.graphics.Matrix()
                                        matrix.postScale(
                                            if (flip) -1f else 1f,
                                            1f
                                        )

                                        *//*matrix.postScale(
                                            if (flip) -scale else scale,
                                            scale,
                                            sourceBitmap.width / 2f,
                                            sourceBitmap.height / 2f
                                        )*//*
                                        // Position the image centered in our bitmap
                                        val drawX = if (flip) imageWidth else 0f
                                        matrix.postTranslate(drawX, 0f)

                                        *//*matrix.postTranslate(
                                            offsetX + (bitmap.width - sourceBitmap.width * scale) / 2,
                                            offsetY + (bitmap.height - sourceBitmap.height * scale) / 2
                                        )*//*

                                        // Draw the image with transformations
                                        canvas.drawBitmap(sourceBitmap, matrix, null)

                                        // Save to gallery
                                        withContext(Dispatchers.IO) {
                                            val saved = saveBitmapToGallery(context, bitmap)
                                            withContext(Dispatchers.Main) {
                                                if (saved) {
                                                    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            Log.e("EditImageScreen", "Error saving image", e)
                                        }
                                    }
                                }*/

                                coroutineScope.launch {
                                    try {
                                        // Get the source image from the URL
                                        val sourceBitmap = loadAndTransformBitmap(context, imageUrl) ?: run {
                                            Toast.makeText(context, "Could not load image", Toast.LENGTH_SHORT).show()
                                            return@launch
                                        }

                                        // Create a bitmap with exact dimensions of the source
                                        val resultBitmap = Bitmap.createBitmap(
                                            sourceBitmap.width,
                                            sourceBitmap.height,
                                            Bitmap.Config.ARGB_8888
                                        )
                                        val canvas = android.graphics.Canvas(resultBitmap)

                                        // Fill with background color
                                        canvas.drawColor(backgroundColor.toArgb())

                                        // Calculate the visible portion of the source image
                                        // Scale factors determine how much of the image is visible
                                        val scaleFactor = scale

                                        // Create a matrix that applies all current transformations
                                        val matrix = android.graphics.Matrix()

                                        // Apply flip if needed
                                        if (flip) {
                                            matrix.postScale(-1f, 1f, sourceBitmap.width / 2f, sourceBitmap.height / 2f)
                                        }

                                        // The offsetX and offsetY values represent the translation in the view
                                        // To apply them to the source bitmap, we need to adjust them relative to scale
                                        // The center point of the view is where the image is anchored
                                        val sourceWidthHalf = sourceBitmap.width / 2f
                                        val sourceHeightHalf = sourceBitmap.height / 2f

                                        // Adjust offsets to be relative to the source bitmap
                                        // offsetX and offsetY values are in screen pixels, we need to convert to bitmap coordinates
                                        val viewToBitmapScaleX = sourceBitmap.width.toFloat() / boxWidth.value
                                        val viewToBitmapScaleY = sourceBitmap.height.toFloat() / boxHeight.value

                                        // Calculate offset in bitmap coordinates
                                        val bitmapOffsetX = -offsetX * viewToBitmapScaleX / scale
                                        val bitmapOffsetY = -offsetY * viewToBitmapScaleY / scale

                                        // Draw the bitmap in the center of the canvas
                                        canvas.drawBitmap(sourceBitmap, matrix, null)

                                        // Save to gallery
                                        /*withContext(Dispatchers.IO) {
                                            val saved = saveBitmapToGallery(context, resultBitmap)
                                            withContext(Dispatchers.Main) {
                                                if (saved) {
                                                    onImageSaved(resultBitmap.asImageBitmap())
                                                    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }*/

                                        // Save to gallery
                                        withContext(Dispatchers.IO) {
                                            val savedUri = saveBitmapToGallery(context, resultBitmap)
                                            withContext(Dispatchers.Main) {
                                                if (savedUri != null) {
                                                    // Now you have the saved image URI
                                                    val imagePath = savedUri.toString()
                                                    onImageSaved(imagePath)
                                                    Logger.d("EditImageScreen", "Image saved to: $imagePath")
                                                    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }

                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            Log.e("EditImageScreen", "Error saving image", e)
                                        }
                                    }
                                }
                            } else if (writeStorageAccessState.shouldShowRationale) {
                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "The storage permission is needed to save the image",
                                        actionLabel = "Grant Access"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        writeStorageAccessState.launchMultiplePermissionRequest()
                                    }
                                }
                            } else {
                                writeStorageAccessState.launchMultiplePermissionRequest()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text(
                            text = "Save to Gallery",
                            color = colors.onPrimary,
                            fontSize = 16.sp
                        )
                    }

                    // Add SnackbarHost to display permission rationale
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
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