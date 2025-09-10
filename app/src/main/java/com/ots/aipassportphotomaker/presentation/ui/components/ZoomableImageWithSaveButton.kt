package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateZoomBy
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import coil.compose.AsyncImage
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.ImageUtils.captureComposableBitmap
import com.ots.aipassportphotomaker.common.utils.ImageUtils.saveBitmapToFile
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// Created by amanullah on 09/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.


@Composable
fun ZoomableImageWithSaveButton(
    model: Any, contentDescription: String? = null
) {

    Box(modifier = Modifier.fillMaxSize()) {
        val angle by remember { mutableStateOf(0f) }
        var zoom by remember { mutableStateOf(1f) }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp.value
        val screenHeight = configuration.screenHeightDp.dp.value

        AsyncImage(
            model,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .graphicsLayer(
                    scaleX = zoom,
                    scaleY = zoom,
                    rotationZ = angle
                )
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { _, pan, gestureZoom, _ ->
                            zoom = (zoom * gestureZoom).coerceIn(1F..4F)
                            if (zoom > 1) {
                                val x = (pan.x * zoom)
                                val y = (pan.y * zoom)
                                val angleRad = angle * PI / 180.0

                                offsetX =
                                    (offsetX + (x * cos(angleRad) - y * sin(angleRad)).toFloat()).coerceIn(
                                        -(screenWidth * zoom)..(screenWidth * zoom)
                                    )
                                offsetY =
                                    (offsetY + (x * sin(angleRad) + y * cos(angleRad)).toFloat()).coerceIn(
                                        -(screenHeight * zoom)..(screenHeight * zoom)
                                    )
                            } else {
                                offsetX = 0F
                                offsetY = 0F
                            }
                        }
                    )
                }
                .fillMaxSize()
        )
    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ZoomableImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onLongPress: ((Offset) -> Unit)? = null,
    onTap: ((Offset) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()

    var layout: LayoutCoordinates? = null

    var scale by remember { mutableStateOf(1f) }
    var translation by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale *= zoomChange
        translation += panChange.times(scale)
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .transformable(state = transformableState)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = onLongPress,
                    onDoubleTap = {
                        val maxScale = 2f
                        val midScale = 1.5f
                        val minScale = 1f
                        val targetScale = when {
                            scale >= minScale -> midScale
                            scale >= midScale -> maxScale
                            scale >= maxScale -> minScale
                            else -> minScale
                        }
                        scope.launch {
                            transformableState.animateZoomBy(targetScale / scale)
                        }
                    },
                    onTap = onTap
                )
            }
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        drag(down.id) {
                            if (layout == null) return@drag
                            val maxX = layout!!.size.width * (scale - 1) / 2f
                            val maxY = layout!!.size.height * (scale - 1) / 2f
                            val targetTranslation = (it.positionChange() + translation)
                            if (targetTranslation.x > -maxX && targetTranslation.x < maxX &&
                                targetTranslation.y > -maxY && targetTranslation.y < maxY
                            ) {
                                translation = targetTranslation
                                it.consumePositionChange()
                            }
                        }
                    }
                }
            }
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier
                .matchParentSize()
                .onPlaced { layout = it }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = translation.x,
                    translationY = translation.y
                ),
            contentScale = ContentScale.Fit
        )

        LaunchedEffect(transformableState.isTransformInProgress) {
            if (!transformableState.isTransformInProgress) {
                if (scale < 1f) {
                    val originScale = scale
                    val originTranslation = translation
                    AnimationState(initialValue = 0f).animateTo(
                        1f,
                        SpringSpec(stiffness = Spring.StiffnessLow)
                    ) {
                        scale = originScale + (1 - originScale) * this.value
                        translation = originTranslation * (1 - this.value)
                    }
                } else {
                    if (layout == null) return@LaunchedEffect
                    val maxX = layout.size.width * (scale - 1) / 2f
                    val maxY = layout.size.height * (scale - 1) / 2f
                    val target = Offset(
                        translation.x.coerceIn(-maxX, maxX),
                        translation.y.coerceIn(-maxY, maxY)
                    )
                    AnimationState(
                        typeConverter = Offset.VectorConverter,
                        initialValue = translation
                    ).animateTo(target, SpringSpec(stiffness = Spring.StiffnessLow)) {
                        translation = this.value
                    }
                }
            }
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ZoomableImagePreview() {

    PreviewContainer {
//        ZoomableImageWithSaveButton(model = R.drawable.sample_image_portrait)
        ZoomableImage(
            painter = painterResource(id = R.drawable.sample_image_portrait),
            contentDescription = "Sample Image",
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .clip(RectangleShape)
                .background(Color.White)
        )
    }

}