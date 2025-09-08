package com.ots.aipassportphotomaker.presentation.ui.editimage

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ots.aipassportphotomaker.R
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
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.AppColors
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

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
    val colorFactory = viewModel.colorFactory

    val localSelectedColor by viewModel.localSelectedColor.collectAsState(initial = Color.Transparent)

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "EditImagePage: Navigation State: $navigationState, ImageUrl: $imageUrl")
         when (navigationState) {
             is EditImageScreenNavigationState.CutOutScreen -> mainRouter.navigateToCutOutScreen(
                 documentId = navigationState.documentId,
                 imageUrl = imageUrl,
                 selectedBackgroundColor = localSelectedColor
             )
         }
    }

    EditImageScreen(
        uiState = uiState,
        selectedColor = localSelectedColor,
        colorFactory = colorFactory,
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

@SuppressLint("UseKtx")
@Composable
private fun EditImageScreen(
    uiState: EditImageScreenUiState,
    selectedColor: Color? = null,
    colorFactory: ColorFactory,
    onSaveImage: (ImageBitmap) -> Unit = {},
    onColorChange: (Color, ColorFactory.ColorType) -> Unit,
    onEraseClick: () -> Unit = {},
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

                        var flip by remember { mutableStateOf(false) }

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

                            // Get intrinsic size of the image



                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Edited Image",
                                modifier = Modifier
                                    .padding(4.dp)
                                    .background(if (backgroundColor != Color.Unspecified) backgroundColor else colors.primary)
                                    .fillMaxHeight()
                                    .align(Alignment.Center) // Center the image
                                    .clipToBounds() // Clip to the image's content bounds
                                    .graphicsLayer(scaleX = if (flip) -1f else 1f),
                                contentScale = ContentScale.Fit, // Use natural size without scaling
                                placeholder = painterResource(id = R.drawable.scan_image_male)
                            )

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
                            .width(270.dp)
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
                        val isCustomSelected = colorFactory.selectedColorType == ColorFactory.ColorType.CUSTOM &&
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


                        val isTransparentSelected = colorFactory.selectedColorType == ColorFactory.ColorType.TRANSPARENT &&
                                colorFactory.getColorByType(ColorFactory.ColorType.TRANSPARENT) == selectedColor
                        ColorItem(
                            modifier = Modifier
                                .width(42.dp),
                            color = Color.Transparent,
                            ratio = 1.2f,
                            showTransparentBg = true,
                            isSelected = isTransparentSelected,
                            onClick = {
                                onColorChange.invoke(Color.Transparent, ColorFactory.ColorType.TRANSPARENT)
                            }

                        )

                        val isWhiteSelected = colorFactory.selectedColorType == ColorFactory.ColorType.WHITE &&
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

                        val isGreenSelected = colorFactory.selectedColorType == ColorFactory.ColorType.GREEN &&
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

                        val isBlueSelected = colorFactory.selectedColorType == ColorFactory.ColorType.BLUE &&
                                colorFactory.getColorByType(ColorFactory.ColorType.BLUE) == selectedColor
                        ColorItem(
                            modifier = Modifier
                                .width(42.dp),
                            color = AppColors.LightPrimary,
                            ratio = 1.2f,
                            isSelected = isBlueSelected,
                            onClick = {
                                onColorChange.invoke(AppColors.LightPrimary, ColorFactory.ColorType.BLUE)
                            }

                        )

                        val isRedSelected = colorFactory.selectedColorType == ColorFactory.ColorType.RED &&
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
            colorFactory = ColorFactory(),
            onColorChange = { _, _ -> },
            onEraseClick = {},
            onBackClick = {},
            onGetProClick = {}
        )
    }
}