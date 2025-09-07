package com.ots.aipassportphotomaker.presentation.editimage

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.request.ImageRequest
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.DrawViewAction
import com.ots.aipassportphotomaker.common.utils.GraphicOverlay
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.repository.ColorFactory
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.ColorItem
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.TextSwitch
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.BackgroundOption
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.AppColors
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.customError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    EditImageScreen(
        uiState = uiState,
        selectedColor = selectedColor,
        onBackClick = { mainRouter.goBack() },
        onGetProClick = { }
    )
}

@SuppressLint("UseKtx")
@Composable
private fun EditImageScreen(
    uiState: EditImageScreenUiState,
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
                    if (!imageUrl.isNullOrEmpty()) {

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
                                                val canvas = android.graphics.Canvas(bmp)
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

                            // Add the GraphicOverlay
                            var graphicOverlay by remember { mutableStateOf<GraphicOverlay?>(null) }
                            var isErasing by remember { mutableStateOf(false) }

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
                                        onClick = { graphicOverlay?.undo() }
                                    ) {
                                        Text("Undo")
                                    }

                                    Button(
                                        onClick = { isErasing = !isErasing }
                                    ) {
                                        Text(if (isErasing) "Stop Erasing" else "Erase")
                                    }

                                    Button(
                                        onClick = { graphicOverlay?.redo() }
                                    ) {
                                        Text("Redo")
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
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val items = remember {
                        listOf("Backdrop", "Add suit")
                    }
                    var selectedIndex by remember {
                        mutableStateOf(0)
                    }

                    TextSwitch(
                        modifier = Modifier
                            .width(300.dp)
                            .align(Alignment.CenterHorizontally),
                        selectedIndex = selectedIndex,
                        items = items,
                        onSelectionChange = {
                            selectedIndex = it
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {

                        Spacer(modifier = Modifier.width(40.dp))

                        // 1st: Custom Color (Color Picker)
                        ColorItem(
                            modifier = Modifier.width(42.dp),
                            color = Color.White,
                            ratio = 1.2f,
                            showEyeDropper = true,
                            isSelected = false,
                            onClick = {
//
                            }
                        )


                        ColorItem(
                            modifier = Modifier
                                .width(42.dp),
                            color = Color.White,
                            ratio = 1.2f,
                            showTransparentBg = true,
                            isSelected = true,
                            onClick = {

                            }

                        )

                        ColorItem(
                            modifier = Modifier
                                .width(42.dp),
                            color = Color.White,
                            ratio = 1.2f,
                            isSelected = false,
                            onClick = {

                            }

                        )

                        ColorItem(
                            modifier = Modifier
                                .width(42.dp),
                            color = Color.Green,
                            ratio = 1.2f,
                            isSelected = false,
                            onClick = {

                            }

                        )

                        ColorItem(
                            modifier = Modifier
                                .width(42.dp),
                            color = AppColors.LightPrimary,
                            ratio = 1.2f,
                            isSelected = false,
                            onClick = {

                            }

                        )

                        ColorItem(
                            modifier = Modifier
                                .width(42.dp),
                            color = Color.Red,
                            ratio = 1.2f,
                            isSelected = false,
                            onClick = {

                            }

                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {

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
                }

            }
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun ImageProcessingScreenPreview() {

    PreviewContainer {
        EditImageScreen(
            uiState = EditImageScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            onBackClick = {},
            onGetProClick = {}
        )
    }
}