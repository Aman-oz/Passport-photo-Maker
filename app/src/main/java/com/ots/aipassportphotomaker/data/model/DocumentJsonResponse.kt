package com.ots.aipassportphotomaker.data.model

import com.ots.aipassportphotomaker.domain.model.DocumentEntity

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
data class DocumentJsonResponse(
    val documents: List<DocumentJson>
)

data class DocumentJson(
    val id: Int,
    val name: String,
    val size: String,
    val unit: String,
    val pixels: String,
    val resolution: String,
    val image: String? = null,
    val type: String,
    val completed: String? = null
)

fun DocumentJson.toDomain() = DocumentEntity(
    id = id,
    name = name,
    size = size,
    unit = unit,
    pixels = pixels,
    resolution = resolution,
    image = image,
    type = type,
    completed = completed
)