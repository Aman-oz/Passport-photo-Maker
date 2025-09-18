package com.ots.aipassportphotomaker.domain.model.dbmodels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "created_images") // New table to avoid conflicts
data class CreatedImageDbData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "document_image")
    val documentImage: String,
    @ColumnInfo(name = "created_image")
    val createdImage: String, // Path to the final saved image
    @ColumnInfo(name = "document_size")
    val documentSize: String,
    @ColumnInfo(name = "unit")
    val unit: String,
    @ColumnInfo(name = "pixel")
    val pixel: String,
    @ColumnInfo(name = "resolution")
    val resolution: String
) {
    // Optional: Converter to domain model
    fun toDomain(): CreatedImageEntity = CreatedImageEntity(
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
}

