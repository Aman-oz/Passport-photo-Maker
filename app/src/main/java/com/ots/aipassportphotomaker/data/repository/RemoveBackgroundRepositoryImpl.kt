package com.ots.aipassportphotomaker.data.repository

import android.util.Log
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.data.model.RemoverApiResponse
import com.ots.aipassportphotomaker.data.remote.api.CropImageApi
import com.ots.aipassportphotomaker.data.remote.api.RemoveBackgroundApi
import com.ots.aipassportphotomaker.domain.repository.CropImageRepository
import com.ots.aipassportphotomaker.domain.repository.RemoveBackgroundRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

// Created by amanullah on 08/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class RemoveBackgroundRepositoryImpl @Inject constructor(
    private val removeBackgroundApi: RemoveBackgroundApi
) : RemoveBackgroundRepository {
    private val TAG = RemoveBackgroundRepositoryImpl::class.java.simpleName
    override suspend fun removeBackground(file: File): Result<RemoverApiResponse> = runCatching {
        Log.d(
            TAG,
            "cropImage: file=${file.name}"
        )

        // Validate file
        if (!file.exists() || file.length() == 0L) {
            throw IllegalStateException("File is empty or does not exist: ${file.absolutePath}")
        }

        // Prepare multipart file with specific MIME type
        val requestFile =
            file.asRequestBody("image/jpeg".toMediaTypeOrNull() ?: "image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)


        // Call Retrofit API
        val response = removeBackgroundApi.removeBackground(
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

    fun createFilePart(file: File, paramName: String): MultipartBody.Part {
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(paramName, file.name, requestBody)
    }
}