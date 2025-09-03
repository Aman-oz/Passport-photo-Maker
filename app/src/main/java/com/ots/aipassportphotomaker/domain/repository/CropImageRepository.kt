package com.ots.aipassportphotomaker.domain.repository

import com.ots.aipassportphotomaker.data.model.ApiResponse
import java.io.File

// Created by amanullah on 03/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
interface CropImageRepository {
    suspend fun cropImage(
        file: File,
        width: Float,
        height: Float,
        unit: String,
        dpi: Int
    ): Result<ApiResponse>
}