package com.ots.aipassportphotomaker.data.remote.api

import com.ots.aipassportphotomaker.data.model.RemoverApiResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Created by amanullah on 08/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
interface RemoveBackgroundApi {
    @Multipart
    @POST("remove_bg")
    suspend fun removeBackground(
        @Part("requestId") requestId: String,
        @Part file: MultipartBody.Part
    ): Response<RemoverApiResponse>
}