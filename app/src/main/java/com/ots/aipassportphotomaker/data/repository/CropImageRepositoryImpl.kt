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

        // Prepare multipart file
//        val filePart = MultipartBody.Part.createFormData(
//            "file",
//            file.name,
//            okhttp3.RequestBody.create(MediaType.parse("image/*"), file)
//        )

        val filePart = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )

        // Call Retrofit API
        cropImageApi.cropImage(
            width = width.toString(),
            height = height.toString(),
            unit = unit,
            dpi = dpi.toString(),
            requestId = java.util.UUID.randomUUID().toString(),
            file = filePart
        ).also {
            Log.d("API_RESPONSE", "Deserialized: $it")
        }
    }.onFailure { error ->
        Log.e(TAG, "Failed to crop image: ${error.message}", error)
    }
}