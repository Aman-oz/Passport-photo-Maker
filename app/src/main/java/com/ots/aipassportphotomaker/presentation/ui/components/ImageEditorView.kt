package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun ImageEditorView(
    modifier: Modifier = Modifier,
    imagePath: String? = null,
    imageBitmap: ImageBitmap,
    erasedPaths: List<Path>,
    onPathAdded: (Path) -> Unit,
    onTap: () -> Unit,
    backgroundDrawableId: Int = R.drawable.transparent_bg // Add background parameter
) {
    val path = remember { Path() }
    val strokeWidth = 40f

    Box(
        modifier = modifier
    ) {
        // Background Image
        Image(
            painter = painterResource(id = backgroundDrawableId),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        // Canvas for drawing and erasing
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            path.moveTo(offset.x, offset.y)
                        },
                        onDrag = { change, _ ->
                            path.lineTo(change.position.x, change.position.y)
                            onPathAdded(Path().apply { addPath(path) })
                        },
                        onDragEnd = {
                            onPathAdded(Path().apply { addPath(path) })
                            path.reset()
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onTap() })
                }
        ) {
            // Draw original image with proper scaling
            val imageWidth = imageBitmap.width.toFloat()
            val imageHeight = imageBitmap.height.toFloat()
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Calculate scale to fit width while maintaining aspect ratio
            val scale = canvasWidth / imageWidth
            val scaledHeight = imageHeight * scale

            // Draw image centered vertically
            drawImage(
                image = imageBitmap,
                dstOffset = IntOffset(0, ((canvasHeight - scaledHeight) / 2).toInt()),
                dstSize = IntSize(canvasWidth.toInt(), scaledHeight.toInt())
            )

            // Apply eraser strokes
            erasedPaths.forEach { erasePath ->
                drawPath(
                    path = erasePath,
                    color = Color.Transparent,
                    blendMode = BlendMode.Clear,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ImageEditorViewPreview() {

    PreviewContainer {

        ImageEditorView(
            modifier = Modifier
                .width(300.dp)
                .aspectRatio(3f / 4f)
                .clipToBounds(),
            imageBitmap = ImageBitmap(100, 100),
            erasedPaths = emptyList(),
            onPathAdded = {},
            onTap = {}
        )

    }
}