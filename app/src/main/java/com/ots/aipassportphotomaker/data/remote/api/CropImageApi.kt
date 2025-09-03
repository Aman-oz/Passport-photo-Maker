package com.ots.aipassportphotomaker.data.remote.api

import com.ots.aipassportphotomaker.data.model.ApiResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
interface CropImageApi {
/*    suspend fun cropImage(
        file: java.io.File,
        width: Float,
        height: Float,
        unit: String,
        dpi: Int
    )*/

    @Multipart
    @POST("detect_face_and_crop_image")
    suspend fun cropImage(
        @Part("width") width: String,
        @Part("height") height: String,
        @Part("unit") unit: String,
        @Part("dpi") dpi: String,
        @Part("requestId") requestId: String,
        @Part file: MultipartBody.Part
    ): ApiResponse
}