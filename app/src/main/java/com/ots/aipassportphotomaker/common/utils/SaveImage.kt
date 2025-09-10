package com.ots.aipassportphotomaker.common.utils

import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.graphics.Bitmap
import android.graphics.Picture
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.core.content.ContextCompat.startActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.ots.aipassportphotomaker.common.utils.ImageUtils.createBitmapFromPicture
import com.ots.aipassportphotomaker.common.utils.ImageUtils.saveBitmapToGallery
import com.ots.aipassportphotomaker.common.utils.ImageUtils.saveToDisk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File

// Created by amanullah on 09/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

// This logic should live in your ViewModel - trigger a side effect to invoke URI sharing.
// checks permissions granted, and then saves the bitmap from a Picture that is already capturing content
// and shares it with the default share sheet.
@OptIn(ExperimentalPermissionsApi::class)
fun shareBitmapFromComposable(
    context: Context,
    picture: android.graphics.Picture,
    snackbarHostState: SnackbarHostState,
    writeStorageAccessState: MultiplePermissionsState,
    coroutineScope: CoroutineScope
) {
    if (writeStorageAccessState.allPermissionsGranted) {
        coroutineScope.launch(Dispatchers.IO) {
            val bitmap = createBitmapFromPicture(picture)
            val uri = bitmap.saveToDisk(context)
            shareBitmap(context, uri)
        }
    } else if (writeStorageAccessState.shouldShowRationale) {
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "The storage permission is needed to save the image",
                actionLabel = "Grant Access"
            )

            if (result == SnackbarResult.ActionPerformed) {
                writeStorageAccessState.launchMultiplePermissionRequest()
            }
        }
    } else {
        writeStorageAccessState.launchMultiplePermissionRequest()
    }
}


// Function to capture and save the bitmap
@OptIn(ExperimentalPermissionsApi::class)
fun saveBitmapFromComposable(
    context: Context,
    picture: android.graphics.Picture,
    snackbarHostState: SnackbarHostState,
    writeStorageAccessState: MultiplePermissionsState,
    coroutineScope: CoroutineScope
) {
    if (writeStorageAccessState.allPermissionsGranted) {
        coroutineScope.launch(Dispatchers.IO) {
            val bitmap =
                createBitmapFromPicture(
                    picture
                )
            saveBitmapToGallery(context, bitmap)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
            }
        }
    } else if (writeStorageAccessState.shouldShowRationale) {
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "The storage permission is needed to save the image",
                actionLabel = "Grant Access"
            )
            if (result == SnackbarResult.ActionPerformed) {
                writeStorageAccessState.launchMultiplePermissionRequest()
            }
        }
    } else {
        writeStorageAccessState.launchMultiplePermissionRequest()
    }
}

fun shareBitmap(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(context, createChooser(intent, "Share your image"), null)
}

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

suspend fun scanFilePath(context: Context, filePath: String): Uri? {
    return suspendCancellableCoroutine { continuation ->
        MediaScannerConnection.scanFile(
            context,
            arrayOf(filePath),
            arrayOf("image/png")
        ) { _, scannedUri ->
            if (scannedUri == null) {
                continuation.cancel(Exception("File $filePath could not be scanned"))
            } else {
                continuation.resume(scannedUri) { throwable ->
                    Log.e("EditImageScreen", "Resuming scanFilePath: $throwable")
                }
            }
        }
    }
}

fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}