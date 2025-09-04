package com.ots.aipassportphotomaker.data.remote.api

import com.ots.aipassportphotomaker.data.model.ApiResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
interface CropImageApi {

    @Multipart
    @POST("detect_face_and_crop_image")
    suspend fun cropImage(
        @Part("width") width: Float,
        @Part("height") height: Float,
        @Part("unit") unit: String,
        @Part("dpi") dpi: Int,
        @Part("requestId") requestId: String,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse> // Use Response to handle error details
}