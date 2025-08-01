package com.ots.aipassportphotomaker.data.remote.api

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
interface CropImageApi {
    suspend fun cropImage(
        file: java.io.File,
        width: Float,
        height: Float,
        unit: String,
        dpi: Int
    )
}