package com.ots.aipassportphotomaker.domain.model

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
data class DocumentEntity(
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
