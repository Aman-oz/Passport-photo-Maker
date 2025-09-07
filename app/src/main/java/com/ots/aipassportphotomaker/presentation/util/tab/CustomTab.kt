package com.ots.aipassportphotomaker.presentation.util.tab

import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.nativeCanvas

// Created by amanullah on 08/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

fun ContentDrawScope.drawWithLayer(block: ContentDrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}