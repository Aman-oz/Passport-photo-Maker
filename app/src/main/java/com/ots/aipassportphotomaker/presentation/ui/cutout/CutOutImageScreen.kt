package com.ots.aipassportphotomaker.presentation.ui.cutout

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.request.ImageRequest
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.DrawViewAction
import com.ots.aipassportphotomaker.common.utils.GraphicOverlay
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.CustomTab
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun CutOutImagePage(
    mainRouter: MainRouter,
    viewModel: CutOutImageScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "EditImagePage"

    val uiState by viewModel.uiState.collectAsState()

    val imageUrl = viewModel.imageUrl
    val selectedColor = viewModel.selectedColor

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "EditImagePage: Navigation State: $navigationState, ImageUrl: $imageUrl")
        /* when (navigationState) {
             is EditImageScreenNavigationState.CutOutScreen -> mainRouter.navigateToCutOutScreen(
                 documentId = navigationState.documentId,
                 imageUrl = imageUrl,
                 selectedBackgroundColor = selectedColor
             )
         }*/
    }

    CutOutImageScreen(
        uiState = uiState,
        selectedColor = selectedColor,
        onBackClick = { mainRouter.goBack() },
        onGetProClick = { }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UseKtx")
@Composable
private fun CutOutImageScreen(
    uiState: CutOutImageScreenUiState,
    selectedColor: String? = null,
    onSaveImage: (ImageBitmap) -> Unit = {},
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
) {

    Surface {

        val context = LocalContext.current
        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage
        val backgroundColor = uiState.selectedColor
        val imageUrl = uiState.imageUrl

        // Add the GraphicOverlay
        var graphicOverlay by remember { mutableStateOf<GraphicOverlay?>(null) }
        var isErasing by remember { mutableStateOf(false) }

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
                            // Load the image as a bitmap
                            val imageLoader = ImageLoader.Builder(context).build()
                            var bitmap by remember { mutableStateOf<Bitmap?>(null) }

                            LaunchedEffect(imageUrl) {
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
                            }

                            AndroidView(
                                factory = { ctx ->
                                    GraphicOverlay(ctx, null).apply {
                                        graphicOverlay = this
                                        setAction(DrawViewAction.MANUAL_CLEAR)
                                        setButtons(null, null)
                                        bitmap?.let { setBitmap(it) }
                                    }
                                },
                                update = { view ->
                                    if (isErasing) {
                                        view.showBrush()
                                    } else {
                                        view.hideBrush()
                                    }
                                    bitmap?.let {
                                        if (view.getCurrentBitmap() != it) {
                                            view.setBitmap(it)
                                        }
                                    }
                                    view.invalidate()
                                },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .matchParentSize()
                            )

                            // Add control buttons
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = { isErasing = !isErasing }
                                    ) {
                                        Text(if (isErasing) "Stop Erasing" else "Erase")
                                    }

                                }

                                Button(
                                    onClick = {
                                        graphicOverlay?.getCurrentBitmap()?.let { bitmap ->
                                            onSaveImage(bitmap.asImageBitmap())
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Save")
                                }
                            }
                        }


                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {

                        var size by remember { mutableFloatStateOf(0f) }
                        var offset by remember { mutableFloatStateOf(0f) }

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
                                value = size,
                                onValueChange = { size = it }
                            )
                            Text(text = size.toString())
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column {
                            Slider(
                                value = offset,
                                onValueChange = { offset = it }
                            )
                            Text(text = offset.toString())
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        //tabs
                        val tabItems = listOf(
                            R.drawable.ai_tab_item_icon,
                            R.drawable.eraser_icon,
                            R.drawable.brush_tab_item_icon,
                            R.drawable.tick_icon
                        )
                        val (selected, setSelected) = remember {
                            mutableStateOf(0)
                        }
                        when(selected) {
                            0 -> {
                                graphicOverlay?.setAction(DrawViewAction.AUTO_CLEAR)
                            }
                            1 -> {
                                graphicOverlay?.setAction(DrawViewAction.MANUAL_CLEAR)
                            }
                            2 -> {
                                graphicOverlay?.setAction(DrawViewAction.ZOOM)
                            }
                            3 -> {
                                graphicOverlay?.setAction(DrawViewAction.NONE)
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