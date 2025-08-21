package com.ots.aipassportphotomaker.data.model

import com.ots.aipassportphotomaker.domain.model.DocumentEntity

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
data class DocumentJsonResponse(
    val documents: List<DocumentJson>
)

data class DocumentJson(
    val id: Int = 0, // Default value, overridden during deserialization
    val Name: String,
    val Size: String,
    val Unit: String,
    val Pixels: String,
    val Resolution: String,
    val Image: String? = null,
    val Type: String,
    val completed: String? = null
)

fun DocumentJson.toDomain() = DocumentEntity(
    id = id,
    name = Name,
    size = Size,
    unit = Unit,
    pixels = Pixels,
    resolution = Resolution,
    image = Image,
    type = Type,
    completed = completed
)