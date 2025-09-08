package com.ots.aipassportphotomaker.domain.repository

import com.ots.aipassportphotomaker.data.model.RemoverApiResponse
import java.io.File

// Created by amanullah on 08/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
interface RemoveBackgroundRepository {
    suspend fun removeBackground(file: File): Result<RemoverApiResponse>
}