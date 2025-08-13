package com.ots.aipassportphotomaker.data.model

import com.google.gson.annotations.SerializedName
import com.ots.aipassportphotomaker.domain.model.DocumentEntity

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
data class DocumentData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("size") val size: String,
    @SerializedName("unit") val unit: String,
    @SerializedName("pixels") val pixels: String,
    @SerializedName("resolution") val resolution: String,
    @SerializedName("image") val image: String? = null,
    @SerializedName("type") val type: String,
    @SerializedName("completed") val completed: String? = null
)

fun DocumentData.toDomain() = DocumentEntity(
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

fun DocumentData.toDbData() = DocumentDbData(
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

/*data class DocumentData(
    val passports: List<Document>,
    val visas: List<Document>,
    val standard: List<Document>,
    val nationalID: List<Document>,
    val driversLicense: List<Document>,
    val residentCards: List<Document>,
    val profiles: List<Document>
)*/
