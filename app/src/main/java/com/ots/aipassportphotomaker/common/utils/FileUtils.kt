package com.ots.aipassportphotomaker.common.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

// Created by amanullah on 24/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
object FileUtils {

    private const val TAG = "FileUtils"

    fun getFileFromContentUri(context: Context, contentUri: Uri): File? {
        try {
            // First try to get the actual file path
            val filePath = getPathFromUri(context, contentUri)
            if (filePath != null) {
                val file = File(filePath)
                if (file.exists()) return file
                Logger.w(TAG, "File exists at path but won't be accessible: $filePath")
            }

            // If that fails, create a temporary file and copy the content
            val fileName = getFileNameFromUri(context, contentUri) ?: "temp_${System.currentTimeMillis()}.jpg"
            val tempFile = File(context.cacheDir, fileName)

            context.contentResolver.openInputStream(contentUri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    val buffer = ByteArray(4 * 1024) // 4k buffer
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                    outputStream.flush()
                }
                return tempFile
            }
            Logger.e(TAG, "Failed to open input stream for URI: $contentUri")
        } catch (e: Exception) {
            Logger.e(TAG, "Error getting file from URI: ${e.message}", e)
        }
        return null
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    return it.getString(displayNameIndex)
                }
            }
        }
        return null
    }

    private fun getPathFromUri(context: Context, uri: Uri): String? {
        // For MediaStore content URIs
        if (uri.authority == MediaStore.AUTHORITY) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    return it.getString(columnIndex)
                }
            }
        }
        return null
    }
}