package com.ots.aipassportphotomaker.common.utils

import androidx.compose.ui.graphics.Color

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
object ColorUtils {
    // Utility function to parse the color string into a Color object
    fun parseColorFromString(colorString: String?): Color? {
        if (colorString.isNullOrEmpty()) return null
        return try {
            // Extract the numeric values using a regex pattern
            val pattern = """Color\(([\d\.]+),\s*([\d\.]+),\s*([\d\.]+),\s*([\d\.]+)""".toRegex()
            val matchResult = pattern.find(colorString)
            val (r, g, b, a) = matchResult?.destructured ?: return null

            // Convert the float values to Color
            Color(
                red = r.toFloatOrNull() ?: 0f,
                green = g.toFloatOrNull() ?: 0f,
                blue = b.toFloatOrNull() ?: 0f,
                alpha = a.toFloatOrNull() ?: 1f
            )
        } catch (e: Exception) {
            null // Return null if parsing fails, fallback to default color
        }
    }
}