package com.ots.aipassportphotomaker.presentation.ui.cutout

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import android.graphics.Color as AndroidColor
import coil.request.ImageRequest
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.DrawViewAction
import com.ots.aipassportphotomaker.common.utils.FileUtils.saveBitmapToInternalStorage
import com.ots.aipassportphotomaker.common.utils.GraphicOverlay
import com.ots.aipassportphotomaker.common.utils.ImageUtils.saveBitmapToGallery
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.ProcessingStage
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.CustomTab
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.viewmodel.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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

    val activity = context as ComponentActivity
    val commonSharedViewModel: SharedViewModel = hiltViewModel(activity)

    CutOutImageScreen(
        uiState = uiState,
        processingStage = processingStage,
        selectedColor = selectedColor,
        onBackClick = { mainRouter.goBack() },
        onGetProClick = { },
        onSaveImage = { bitmap ->
            uiScope.launch {
                when (viewModel.sourceScreen) {
                    "EditImageScreen" -> {
                        Logger.i(TAG, "onSaveImage: Navigating back to EditImageScreen")
                        val internalPath = saveBitmapToInternalStorage(context, bitmap.asAndroidBitmap())
                        if (internalPath != null) {
                            Logger.i(TAG, "Image saved successfully: $internalPath")
                            commonSharedViewModel.setEditedImageResult(internalPath)

                            delay(100)
                            mainRouter.goBack()

                        } else {
                            Logger.e(TAG, "Failed to save image")
                        }
                    }

                    "HomeScreen" -> {
                        Logger.i(TAG, "onSaveImage: Navigating to final screen")
                        val savedUri = saveBitmapToGallery(context, bitmap.asAndroidBitmap())
                        if (savedUri != null) {
                            Logger.i(TAG, "Image saved successfully: $savedUri")
                            viewModel.onImageSaved(savedUri.toString())

                        } else {
                            Logger.e(TAG, "Failed to save image")
                        }
                    }

                    else -> {
                        Logger.i(TAG, "onSaveImage: Unknown sourceScreen, defaulting to EditImageScreen: ${uiState.sourceScreen}")
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
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UseKtx")
@Composable
private fun CutOutImageScreen(
    uiState: CutOutImageScreenUiState,
    processingStage: ProcessingStage = ProcessingStage.NONE,
    selectedColor: String? = null,
    onSaveImage: (ImageBitmap) -> Unit = {},
    onRemoveBackgroundAi: (ImageBitmap) -> Unit = {},
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
) {

    Surface {

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
        val (selected, setSelected) = remember { mutableStateOf(1) }

        // Get appropriate message based on processing stage
        val currentMessage = when (processingStage) {
            ProcessingStage.UPLOADING -> "🔄 Uploading Photo..."
            ProcessingStage.PROCESSING -> {
                // Alternate between these messages during processing
                val processingMessages = listOf(
                    "🔮 AI Magic in progress… just a moment!",
                    "🎨 Removing the best fit for your requirements!",
                    "☺ Face detection in progress… almost there!",
                )
                val messageIndex by produceState(initialValue = 0) {
                    while (processingStage == ProcessingStage.PROCESSING) {
                        delay(3000)
                        value = (value + 1) % processingMessages.size
                    }
                }
                processingMessages[messageIndex]
            }
            ProcessingStage.CROPPING_IMAGE -> "💫 Cropping image..."
            ProcessingStage.COMPLETED -> {
                "✅ Process completed successfully"

            }
            ProcessingStage.NONE -> ""
            ProcessingStage.NO_NETWORK_AVAILABLE -> "❌ No network connection"
            ProcessingStage.ERROR -> "❌ Something went wrong"
            ProcessingStage.DOWNLOADING -> "⬇️ Applying Changes..."
            ProcessingStage.SAVING_IMAGE -> "💾 Saving image..."
            ProcessingStage.BACKGROUND_REMOVAL -> "🖼️ Removing background..."
        }

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
                title = "Cut Out",
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

                            /*LaunchedEffect(imageUrl) {
                                val request = ImageRequest.Builder(context)
                                    .data(imageUrl)
                                    .target { drawable ->
                                        bitmap = when (drawable) {
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
                                    }
                                    .build()
                                imageLoader.enqueue(request)
                            }*/
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
                                                        drawable.setBounds(0, 0, canvas.width, canvas.height)
                                                        drawable.draw(canvas)
                                                        bmp
                                                    }
                                                }

                                                // Update both bitmap state and GraphicOverlay
                                                bitmap = newBitmap
                                                graphicOverlay?.setBitmap(newBitmap)

                                                Logger.i("CutOutImageScreen", "Image updated successfully: ${imageUrl}")
                                            }
                                            .build()
                                        imageLoader.enqueue(request)
                                    } catch (e: Exception) {
                                        Logger.e("CutOutImageScreen", "Failed to load image: ${e.message}", e)
                                    }
                                }
                            }

                            AndroidView(
                                factory = { ctx ->
                                    /*GraphicOverlay(ctx, null).apply {
                                        graphicOverlay = this
                                        setAction(DrawViewAction.ERASE_BACKGROUND)
                                        setBrushSize(brushSize)
                                        bitmap?.let { setBitmap(it) }
                                    }*/

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
                                    /*view.setAction(currentMode)
                                    view.setBrushSize(brushSize)
                                    bitmap?.let {
                                        if (view.getCurrentBitmap() != it) {
                                            view.setBitmap(it)
                                        }
                                    }
                                    view.invalidate()*/
                                },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .matchParentSize()
                            )
                        }


                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {


                        Row(
                            modifier = Modifier
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

                        Column {
                            Slider(
                                value = brushSize,
                                onValueChange = {
                                    brushSize = it
                                    graphicOverlay?.setBrushSize(it)
                                },
                                valueRange = 10f..100f
                            )
                            Text(text = brushSize.toString())
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column {
                            Slider(
                                value = brushOffset,
                                onValueChange = {
                                    brushOffset = it
                                    graphicOverlay?.setBrushOffset(it)
                                                },
                                valueRange = 0f..150f

                            )
                            Text(text = brushOffset.toString())
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        //tabs
                        val tabItems = listOf(
                            R.drawable.ai_tab_item_icon,
                            R.drawable.eraser_icon,
                            R.drawable.brush_tab_item_icon,
                            R.drawable.tick_icon_thick
                        )

                        /*LaunchedEffect(selected) {
                            when (selected) {
                                0 -> {
                                    graphicOverlay?.setAction(DrawViewAction.NONE)
                                    currentMode = DrawViewAction.NONE
                                }

                                1 -> {
                                    graphicOverlay?.setAction(DrawViewAction.ERASE_BACKGROUND)
                                    currentMode = DrawViewAction.ERASE_BACKGROUND
                                }

                                2 -> {
                                    graphicOverlay?.setAction(DrawViewAction.RECOVER_AREA)
                                    currentMode = DrawViewAction.RECOVER_AREA
                                }

                                3 -> {

                                    graphicOverlay?.setAction(DrawViewAction.NONE)
                                    currentMode = DrawViewAction.NONE

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
                                            finalBitmap = transparentBitmap.asImageBitmap()
                                        }

                                    setSelected(1)
                                }
                            }
                        }*/

                        LaunchedEffect(selected) {
                            when (selected) {
                                0 -> {
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
                                }
                                1 -> {
                                    if (currentMode != DrawViewAction.ERASE_BACKGROUND) {
                                        currentMode = DrawViewAction.ERASE_BACKGROUND
                                    }
                                }
                                2 -> {
                                    if (currentMode != DrawViewAction.RECOVER_AREA) {
                                        currentMode = DrawViewAction.RECOVER_AREA
                                    }
                                }
                                3 -> {
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
                                }
                            }
                        }

                        CustomTab(
                            selectedItemIndex = selected,
                            items = tabItems,
                            modifier = Modifier,
                            tabWidth = 100.dp,
                            onClick = setSelected
                        )

                    }

                }

                finalBitmap?.let { bitmap ->
                    Dialog(onDismissRequest = { }) {
                        Column(
                            modifier = Modifier
                                .background(colors.onBackground)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Preview of Ticket image \uD83D\uDC47",
                                color = colors.background,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                            )
                            Spacer(Modifier.size(16.dp))
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Preview of ticket"
                            )
                            Spacer(Modifier.size(4.dp))
                            Row {
                                Button(onClick = { finalBitmap = null }) {
                                    Text("Close")
                                }


                                Spacer(Modifier.size(6.dp))

                                Button(onClick = {
                                    onSaveImage(bitmap)
                                    finalBitmap = null

                                }) {
                                    Text("Save")
                                }
                            }
                        }
                    }
                }

                removeBackgroundBitmap?.let { bitmap ->
                    Dialog(onDismissRequest = { }) {

                        Box(
                            modifier = Modifier
                                .background(color = colors.background, shape = RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            var removeBg by remember { mutableStateOf(false) }
                            if (removeBg) {
                                Column(
                                    modifier = Modifier
                                        .size(300.dp)
                                        .padding(16.dp)
                                        .align(Alignment.Center)
                                ) {

                                    CircularProgressIndicator(
                                        color = colors.primary,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = currentMessage,
                                        color = colors.onBackground,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                    )
                                    if (currentMessage == "✅ Process completed successfully") {
                                        // Delay for a moment to let user see the completed message
                                        LaunchedEffect(Unit) {
                                            delay(500)
                                            removeBackgroundBitmap = null
                                        }
                                    }
                                }
                            } else {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Do you want to remove background through Ai? \uD83D\uDC47",
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
                                Row {
                                    Button(onClick = { removeBackgroundBitmap = null }) {
                                        Text("No")
                                    }

                                    Spacer(Modifier.size(6.dp))

                                    Button(onClick = {
                                        removeBg = true
                                        onRemoveBackgroundAi(bitmap)

                                    }) {
                                        Text("Yes")
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
            onBackClick = {},
            onGetProClick = {}
        )
    }
}