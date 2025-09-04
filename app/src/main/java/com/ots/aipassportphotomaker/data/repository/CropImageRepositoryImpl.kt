package com.ots.aipassportphotomaker.data.repository

import android.util.Log
import com.ots.aipassportphotomaker.data.model.ApiResponse
import com.ots.aipassportphotomaker.data.remote.api.CropImageApi
import com.ots.aipassportphotomaker.domain.repository.CropImageRepository
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

// Created by amanullah on 03/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class CropImageRepositoryImpl @Inject constructor(
    private val cropImageApi: CropImageApi
) : CropImageRepository {
    private val TAG = CropImageRepositoryImpl::class.java.simpleName

    override suspend fun cropImage(
        file: File,
        width: Float,
        height: Float,
        unit: String,
        dpi: Int
    ): Result<ApiResponse> = runCatching {
        Log.d(TAG, "cropImage: file=${file.name}, width=$width, height=$height, unit=$unit, dpi=$dpi")

        // Validate file
        if (!file.exists() || file.length() == 0L) {
            throw IllegalStateException("File is empty or does not exist: ${file.absolutePath}")
        }

        // Prepare multipart file with specific MIME type
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull() ?: "image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        // Log request details
        Log.d(TAG, "Request: width=$width, height=$height, unit=$unit, dpi=$dpi, requestId=${java.util.UUID.randomUUID()}, file=${file.name}")

        // Call Retrofit API
        val response = cropImageApi.cropImage(
            width = width,
            height = height,
            unit = unit,
            dpi = dpi,
            requestId = java.util.UUID.randomUUID().toString(),
            file = filePart
        )

        if (response.isSuccessful) {
            response.body()?.also {
                Log.d("API_RESPONSE", "Deserialized: $it")
                return@runCatching it
            } ?: throw IllegalStateException("Response body is null")
        } else {
            val errorBody = response.errorBody()?.string() ?: "No error details"
            Log.e(TAG, "Server error: ${response.code()} - $errorBody")
            throw retrofit2.HttpException(response)
        }
    }.onFailure { error ->
        Log.e(TAG, "Failed to crop image: ${error.message}", error)
    }
}