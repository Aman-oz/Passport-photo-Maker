package com.ots.aipassportphotomaker.common.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

// Created by amanullah on 09/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
object ImageUtils {
    fun captureComposableBitmap(
        context: Context,
        scale: Float,
        rotation: Float,
        onCaptured: (Bitmap) -> Unit
    ) {
        val composeView = ComposeView(context).apply {
            setContent {
                Box(
                    modifier = Modifier
                        .size(300.dp) // or any fixed size
                        .background(Color.Gray)
                ) {
                    Image(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                rotationZ = rotation
                            ),
                        painter = painterResource(R.drawable.sample_image_portrait),
                        contentDescription = null
                    )
                }
            }
        }

        composeView.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY)
        )
        composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

        val bitmap = Bitmap.createBitmap(
            composeView.width,
            composeView.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        composeView.draw(canvas)

        onCaptured(bitmap)
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String = "zoomed_image.png") {
        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        Toast.makeText(context, "Image saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }


    suspend fun loadAndTransformBitmap1(
        context: Context,
        imageUrl: String?,
        backgroundColor: Color,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        boxSize: Float
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (imageUrl.isNullOrEmpty()) {
                println("Image URL is null or empty")
                Logger.e("ImageUtils", "Image URL is null or empty")
                return@withContext null
            }
            if (boxSize <= 0) {
                println("Box size is invalid: $boxSize")
                Logger.e("ImageUtils", "Box size is invalid: $boxSize")
                return@withContext null
            }
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .build()
            val result = ImageLoader(context).execute(request)
            if (result.drawable == null) {
                println("Failed to load drawable from $imageUrl")
                Logger.e("ImageUtils", "Failed to load drawable from $imageUrl")
                return@withContext null
            }
            val originalBitmap = result.drawable?.toBitmap(
                (boxSize * context.resources.displayMetrics.density).toInt(),
                (boxSize * context.resources.displayMetrics.density).toInt()
            ) ?: run {
                println("Failed to convert drawable to Bitmap")
                Logger.e("ImageUtils", "Failed to convert drawable to Bitmap")
                return@withContext null
            }

            // Convert hardware bitmap to ARGB_8888 if necessary
            val softwareBitmap = if (originalBitmap.config == Bitmap.Config.HARDWARE) {
                val bitmap = Bitmap.createBitmap(
                    originalBitmap.width,
                    originalBitmap.height,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                canvas.drawBitmap(originalBitmap, 0f, 0f, null)
                bitmap
            } else {
                originalBitmap
            }

            // Create a new Bitmap with the transformed size
            val scaledWidth = (boxSize * scale).toInt().coerceAtLeast(1) // Ensure positive
            val scaledHeight = (boxSize * scale).toInt().coerceAtLeast(1) // Ensure positive
            val transformedBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(transformedBitmap)

            // Apply background color
            val backgroundColor = if (backgroundColor != Color.Unspecified) backgroundColor else colors.primary
            canvas.drawColor(backgroundColor.toArgb())

            // Apply transformations and draw the software bitmap
            canvas.save()
            canvas.scale(scale, scale, scaledWidth / 2f, scaledHeight / 2f)
            canvas.translate(offsetX, offsetY)
            val x = -softwareBitmap.width / 2f
            val y = -softwareBitmap.height / 2f
            canvas.drawBitmap(softwareBitmap, x, y, null)
            canvas.restore()

            // Recycle the intermediate bitmap if it was converted
            if (softwareBitmap != originalBitmap) {
                originalBitmap.recycle()
            }

            transformedBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            println("Exception in loadAndTransformBitmap: ${e.message}")
            Logger.e("ImageUtils", "Error loading or transforming bitmap: ${e.message}", e)
            null
        }
    }

    suspend fun loadAndTransformBitmap(context: Context, imageUrl: String?): Bitmap? {
        if (imageUrl.isNullOrEmpty()) return null

        return withContext(Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .allowHardware(false) // Important to allow bitmap manipulation
                    .build()

                val loader = coil.ImageLoader(context)
                val result = loader.execute(request)

                return@withContext (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
            } catch (e: Exception) {
                Logger.e("ImageUtils", "Error loading image: ${e.message}", e)
                null
            }
        }
    }

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val filename = "passportphoto_${System.currentTimeMillis()}.jpg"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                    }
                    Logger.i("ImageUtils", "Image saved to gallery: $it")
                    it
                }
            } else {
                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                if (!directory.exists()) {
                    directory.mkdirs()
                }

                val file = File(directory, filename)
                FileOutputStream(file).use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                }

                // Scan the file to make it immediately visible in gallery
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.toString()),
                    arrayOf("image/jpeg"),
                    null
                )
                Logger.i("ImageUtils", "Image saved to gallery: ${file.absolutePath}, uri: ${Uri.fromFile(file)}")
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            Logger.e("ImageUtils", "Error saving bitmap: ${e.message}", e)
            null
        }
    }

    /**********************************/

    fun createBitmapFromPicture(picture: Picture): Bitmap {
        val bitmap = Bitmap.createBitmap(
            picture.width,
            picture.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawPicture(picture)
        return bitmap
    }

    suspend fun Bitmap.saveToDisk(context: Context): Uri {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "screenshot-${System.currentTimeMillis()}.png"
        )

        file.writeBitmap(this, Bitmap.CompressFormat.PNG, 100)

        return scanFilePath(context, file.path) ?: throw Exception("File could not be saved")
    }

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }
}