package com.ots.aipassportphotomaker.common.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

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