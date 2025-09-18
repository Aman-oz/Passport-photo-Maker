package com.ots.aipassportphotomaker.common.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Created by amanullah on 18/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
object ViewsUtils {

    val premiumHorizontalGradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4D50FB),
            Color(0xFFEF15AA),
            Color(0xFFFA7451)
        )
    )

    val premiumHorizontalOppositeGradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFA7451),
            Color(0xFFEF15AA),
            Color(0xFF4D50FB),
        )
    )
}