package com.ots.aipassportphotomaker.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.min

// Created by amanullah on 07/09/2025.
// Copyright (c) 2025 Ozi PUblishing. All rights reserved.
object BitmapUtils {

    fun isMemorySufficient(bitmap: Bitmap): Boolean {
        val maxMemory = Runtime.getRuntime().maxMemory()
        val usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val bitmapSize = bitmap.allocationByteCount
        return (usedMemory + bitmapSize) < maxMemory
    }

    fun getResizedBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        // Calculate the scale factor
        val scale = min(
            (width.toFloat() / bitmap.getWidth()).toDouble(),
            (height.toFloat() / bitmap.getHeight()).toDouble()
        ).toFloat()

        // Calculate the dimensions to maintain the aspect ratio
        val newWidth = Math.round(bitmap.getWidth() * scale)
        val newHeight = Math.round(bitmap.getHeight() * scale)

        // Create a new bitmap with the desired width and height
        val resizedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Set up the canvas and paint
        val canvas = Canvas(resizedBitmap)
        val xTranslation = (width - newWidth) / 2.0f
        val yTranslation = (height - newHeight) / 2.0f
        val transformation = Matrix()
        transformation.postTranslate(xTranslation, yTranslation)
        transformation.preScale(scale, scale)
        val paint = Paint()
        paint.setFilterBitmap(true)

        // Draw the resized bitmap onto the canvas
        canvas.drawBitmap(bitmap, transformation, paint)

        return resizedBitmap
    }
}