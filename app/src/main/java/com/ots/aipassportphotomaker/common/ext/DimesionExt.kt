package com.ots.aipassportphotomaker.common.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Created by amanullah on 12/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun Dp.toPX(): Int {
    val density = LocalDensity.current
    return with(density) { this@toPX.toPx().toInt() }
}

@Composable
fun Int.toDp(): Dp {
    val density = LocalDensity.current
    return with(density) {
        this@toDp.toDp()
    }
}

class ImageSize(
    val width: Dp,
    val height: Dp
) {
    companion object {
        @Composable
        fun getImageFixedSize(): ImageSize {
            val configuration = LocalConfiguration.current
            val aspectRatio = 19f / 6f
            val imageWidth = configuration.screenWidthDp.dp / 3
            val imageHeight = imageWidth * aspectRatio
            return ImageSize(imageWidth, imageHeight)
        }
    }

}