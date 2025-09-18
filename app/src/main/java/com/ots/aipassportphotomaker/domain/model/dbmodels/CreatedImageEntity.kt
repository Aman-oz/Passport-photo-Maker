package com.ots.aipassportphotomaker.domain.model.dbmodels

import com.ots.aipassportphotomaker.data.model.mapper.CreatedImageData

data class CreatedImageEntity(
    val id: Int = 0,
    val name: String,
    val type: String,
    val documentImage: String,
    val createdImage: String, // Path to the final saved image
    val documentSize: String,
    val unit: String,
    val pixel: String,
    val resolution: String
)

fun CreatedImageEntity.toData(): CreatedImageData = CreatedImageData(
    id = id, name = name, type = type, documentImage = documentImage,
    createdImage = createdImage, documentSize = documentSize, unit = unit,
    pixel = pixel, resolution = resolution
)
