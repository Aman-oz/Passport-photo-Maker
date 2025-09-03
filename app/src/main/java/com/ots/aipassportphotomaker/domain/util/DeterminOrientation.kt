package com.ots.aipassportphotomaker.domain.util

import androidx.compose.runtime.Composable
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.DocumentPixels
import com.ots.aipassportphotomaker.domain.model.DocumentSize

// Created by amanullah on 25/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Composable
fun determineOrientation(size: String?): DocumentSize {
    return if (size != null && size.contains(" x ")) {
        try {
            val (widthStr, heightStr) = size.split(" x ")
            val width = widthStr.toFloatOrNull() ?: 0f
            val height = heightStr.toFloatOrNull() ?: 0f
            val orientation = when {
                width > height -> "Landscape"
                height > width -> "Portrait"
                else -> "Square"
            }
            DocumentSize(width, height, orientation)
        } catch (e: Exception) {
            // Log the error for debugging
            Logger.e("determineOrientation", "Error parsing size: $size", e)
            DocumentSize(0f, 0f, "Unknown")
        }
    } else {
        Logger.w("determineOrientation", "Size is null or invalid: $size")
        DocumentSize(0f, 0f, "Unknown")
    }
}


fun getDocumentWidthAndHeight(size: String?): DocumentSize {
    return if (size != null && size.contains(" x ")) {
        try {
            val (widthStr, heightStr) = size.split(" x ")
            val width = widthStr.toFloatOrNull() ?: 0f
            val height = heightStr.toFloatOrNull() ?: 0f
            val orientation = when {
                width > height -> "Landscape"
                height > width -> "Portrait"
                else -> "Square"
            }
            DocumentSize(width, height, orientation)
        } catch (e: Exception) {
            // Log the error for debugging
            Logger.e("determineOrientation", "Error parsing size: $size", e)
            DocumentSize(0f, 0f, "Unknown")
        }
    } else {
        Logger.w("determineOrientation", "Size is null or invalid: $size")
        DocumentSize(0f, 0f, "Unknown")
    }
}

@Composable
fun determinePixels(pixels: String?): DocumentPixels {
    return if (pixels != null && pixels.contains("x")) {
        try {

            // Parse Pixels (e.g., "413x531 px")
            val (widthPxStr, heightPxStr) = pixels.split("x").map { it.trim().split(" ")[0] } // Remove " px" and split
            val widthPx = widthPxStr.toIntOrNull() ?: 0
            val heightPx = heightPxStr.toIntOrNull() ?: 0

            DocumentPixels(widthPx, heightPx)
        } catch (e: Exception) {
            android.util.Log.e("determinePixels", "Error parsing size: pixels: $pixels", e)
            DocumentPixels(0, 0)
        }
    } else {
        android.util.Log.w("determinePixels", "Size or pixels is null or invalid: pixels=$pixels")
        DocumentPixels(0, 0)
    }
}