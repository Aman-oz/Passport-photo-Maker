package com.ots.aipassportphotomaker.data.model.mapper

import com.ots.aipassportphotomaker.domain.model.dbmodels.CreatedImageDbData

data class CreatedImageData(
    val id: Int = 0,
    val name: String,
    val type: String,
    val documentImage: String,
    val createdImage: String,
    val documentSize: String,
    val unit: String,
    val pixel: String,
    val resolution: String
) {
    // Converter to Room entity
    fun toDbData(): CreatedImageDbData = CreatedImageDbData(
        id = id,
        name = name,
        type = type,
        documentImage = documentImage,
        createdImage = createdImage,
        documentSize = documentSize,
        unit = unit,
        pixel = pixel,
        resolution = resolution
    )

    // Converter from Room entity
    companion object {
        fun fromDbData(dbData: CreatedImageDbData): CreatedImageData = CreatedImageData(
            id = dbData.id,
            name = dbData.name,
            type = dbData.type,
            documentImage = dbData.documentImage,
            createdImage = dbData.createdImage,
            documentSize = dbData.documentSize,
            unit = dbData.unit,
            pixel = dbData.pixel,
            resolution = dbData.resolution
        )
    }
}
