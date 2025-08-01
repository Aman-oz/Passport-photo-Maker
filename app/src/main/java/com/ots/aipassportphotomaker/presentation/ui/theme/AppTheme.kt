package com.ots.aipassportphotomaker.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()

    colors = if (darkTheme) {
        darkColors
    } else {
        lightColors
    }

    systemUiController.setStatusBarColor(color = colors.primary)

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}