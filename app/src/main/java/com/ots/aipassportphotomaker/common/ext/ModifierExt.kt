package com.ots.aipassportphotomaker.common.ext

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Created by amanullah on 10/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

fun Modifier.segmentedShadow(
    color: Color,
    layers: Int = 20,
    shadowWidth: Dp = 2.dp
): Modifier {
    return this.drawBehind {
        val shadowSize = shadowWidth.toPx()
        val size = shadowSize / layers

        repeat(layers) {
            drawRoundRect(
                style = Stroke(width = size),
                color = color.copy(alpha = 1f - (1 / layers.toFloat()) * it),
                topLeft = Offset(
                    shadowSize - it * size,
                    shadowSize - it * size
                ),
                cornerRadius = CornerRadius(shadowSize + it * size),
                size = Size(
                    this.size.width - shadowSize * 2 + size * it * 2,
                    this.size.height - shadowSize * 2 + size * it * 2
                )
            )
        }
    }
}