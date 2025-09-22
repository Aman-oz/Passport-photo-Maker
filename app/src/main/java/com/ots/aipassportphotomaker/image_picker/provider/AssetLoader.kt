package com.ots.aipassportphotomaker.image_picker.provider

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.image_picker.model.AssetInfo
import com.ots.aipassportphotomaker.image_picker.model.RequestType

internal object AssetLoader {

    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
    )

    fun insertImage(context: Context): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "camera-${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }
        return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    fun deleteByUri(context: Context, uri: Uri) {
        context.contentResolver.delete(uri, null, null)
    }

    fun findByUri(context: Context, uri: Uri): AssetInfo? {
        val cursor = context.contentResolver.query(uri, projection, null, null, null, null)
        cursor?.use {
            val indexId = it.getColumnIndex(projection[0])
            val indexFilename = it.getColumnIndex(projection[1])
            val indexDate = it.getColumnIndex(projection[2])
            val indexMimeType = it.getColumnIndex(projection[3])
            val indexSize = it.getColumnIndex(projection[4])
            val indexDirectory = it.getColumnIndex(projection[5])
            val indexFilepath = it.getColumnIndex(projection[6])

            if (it.moveToNext()) {

                val id = it.getLong(indexId)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)


                return AssetInfo(
                    id = id,
                    uriString = contentUri.toString(),
                    filepath = it.getString(indexFilepath) ?: "",
                    filename = it.getString(indexFilename) ?: "Unknown_${id}",
                    date = it.getLong(indexDate),
                    mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                    mimeType = it.getString(indexMimeType) ?: "image/jpeg",
                    size = it.getLong(indexSize),
                    duration = 0L, // Not applicable for images
                    directory = it.getString(indexDirectory) ?: "",
                )
            }
        }
        return null
    }

    fun load(context: Context, requestType: RequestType, limit: Int = 100, offset: Int = 0): List<AssetInfo> {
        if (requestType != RequestType.IMAGE) return emptyList()
        val assets = ArrayList<AssetInfo>()
//        val cursor = createCursor(context, requestType)
        // CHECK: Verify storage permission
        val readPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            Logger.e("AssetLoader", "Missing storage permission: READ_MEDIA_IMAGES or READ_EXTERNAL_STORAGE")
            return assets
        }
        val cursor = createCursor(context) ?: run {
            Logger.e("AssetLoader", "Cursor is null, query failed or no images available")
            return assets
        }

        cursor.use {
            val totalCount = it.count

            val indexId = it.getColumnIndex(projection[0])
            val indexFilename = it.getColumnIndex(projection[1])
            val indexDate = it.getColumnIndex(projection[2])
            val indexMimeType = it.getColumnIndex(projection[3])
            val indexSize = it.getColumnIndex(projection[4])
            val indexDirectory = it.getColumnIndex(projection[5])
            val indexFilepath = it.getColumnIndex(projection[6])

            if (offset >= totalCount) {
                Logger.w("AssetLoader", "Offset $offset exceeds total count $totalCount")
                return assets
            }

            if (!it.moveToPosition(offset)) {
                Logger.w("AssetLoader", "Failed to move to offset $offset")
                return assets
            }

            Logger.d("AssetLoader", "Total images available: $totalCount, offset: $offset, limit: $limit")

            var count = 0
            while (it.moveToNext() && count < limit && !it.isAfterLast) {

                val id = it.getLong(indexId)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                // Handle null values safely
                val filepath = it.getString(indexFilepath) ?: "" // Fallback to empty string or skip
                val filename = it.getString(indexFilename) ?: "Unknown_${id}" // Fallback to a default name
                val mimeType = it.getString(indexMimeType) ?: "image/jpeg" /*"application/octet-stream"*/ // Fallback MIME type
                val directory = it.getString(indexDirectory) ?: "" // Fallback to empty string

                assets.add(
                    AssetInfo(
                        id = id,
                        uriString = contentUri.toString(),
                        filepath = filepath,
                        filename = filename,
                        date = it.getLong(indexDate),
                        mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                        mimeType = mimeType,
                        size = it.getLong(indexSize),
                        duration = 0L, // Not applicable for images
                        directory = directory,
                    )
                )

                count++
            }
        } ?: run {
            Logger.w("AssetLoader", "Cursor is null, possibly no images available")
        }
        Logger.d("AssetLoader", "Loaded ${assets.size} images with limit $limit and offset $offset")
        return assets
    }

    fun getDirectoryCounts(context: Context): Map<String, Int> {
        val counts = mutableMapOf<String, Int>()
        val cursor = createCursor(context) ?: return counts
        cursor.use {
            val indexDirectory = it.getColumnIndexOrThrow(projection[5])
            while (it.moveToNext()) {
                val directory = it.getString(indexDirectory) ?: ""
                counts[directory] = counts.getOrDefault(directory, 0) + 1
            }
            Logger.d("AssetLoader", "Directory counts: $counts")
        }
        return counts
    }

    private fun createCursor(context: Context, limit: Int): Cursor? {
        // CHANGE: Added try-catch for error handling and removed OFFSET from sortOrder
        return try {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_TAKEN} DESC",
                null
            )
        } catch (e: Exception) {
            Logger.e("AssetLoader", "Error querying MediaStore: ${e.message}", e)
            null
        }
    }

    private fun createCursor(context: Context): Cursor? {
        return try {
            // Note: OFFSET is not supported in all Android versions; use a subquery or skip manually
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_TAKEN} DESC"
            )
        } catch (e: Exception) {
            Logger.e("AssetLoader", "Query error: ${e.message}", e)
            null
        }
    }

    private data class Selection(val selection: String, val arguments: List<String>)
}