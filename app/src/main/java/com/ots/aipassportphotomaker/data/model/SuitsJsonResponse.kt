package com.ots.aipassportphotomaker.data.model

import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.SuitsEntity

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
data class SuitsJsonResponse(
    val suits: List<SuitsJson>
)

data class SuitsJson(
    val _id: String,
    val name: String,
    val image: String,
    val thumbnail: String,
    val isPremium: Boolean
)

fun SuitsJson.toDomain() = SuitsEntity(
    _id = _id,
    name = name,
    image = image,
    thumbnail = thumbnail,
    isPremium = isPremium,
)