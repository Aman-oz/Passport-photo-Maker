package com.ots.aipassportphotomaker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ots.aipassportphotomaker.domain.model.DocumentEntity

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Entity(tableName = "documents")
data class DocumentDbData(
    @PrimaryKey val id: Int,
    val name: String,
    val size: String,
    val unit: String,
    val pixels: String,
    val resolution: String,
    val image: String? = null,
    val type: String,
    val completed: String? = null
)

fun DocumentDbData.toDomain() = DocumentEntity(
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
