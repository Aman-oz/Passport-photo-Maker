package com.ots.aipassportphotomaker.domain.model

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi. All rights reserved.
sealed class DocumentListItem {
    data class Document(
        val id: Int,
        val name: String,
        val size: String,
        val unit: String,
        val pixels: String,
        val resolution: String,
        val image: String? = null,
        val type: String,
        val completed: String? = null
    ): DocumentListItem()

    data class Separator(val category: String) : DocumentListItem()
}
