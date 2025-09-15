package com.ots.aipassportphotomaker.domain.bottom_nav

import com.ots.aipassportphotomaker.domain.model.CustomDocumentData
import kotlinx.serialization.Serializable

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
sealed class Page {
    @Serializable
    data object NavigationBar : Page()

    @Serializable
    data object Home : Page()

    @Serializable
    data object PhotoID : Page()

    @Serializable
    data object PhotoID2 : Page()

    @Serializable
    data object History : Page()

    @Serializable
    data object Premium : Page()

    @Serializable
    data class ItemDetailScreen(val name: String) : Page()

    @Serializable
    data class PhotoIDDetailScreen(val type: String) : Page()

    @Serializable
    data class DocumentInfoScreen(val documentId: Int) : Page()

    @Serializable
    data class DocumentInfoScreenFromCustom(@Serializable val customData: CustomDocumentData) : Page()

    @Serializable
    data class ImageProcessingScreen(
        val documentId: Int,
        val imagePath: String?,
        val filePath: String?,
        val selectedDpi: String,
        val selectedBackgroundColor: String?,
        val sourceScreen: String
    ) : Page()

    @Serializable
    data class EditImageScreen(
        val documentId: Int,
        val imageUrl: String?,
        val selectedBackgroundColor: String?,
        val editPosition: Int = 0,
        val selectedDpi: String,
        val sourceScreen: String
    ) : Page()

    @Serializable
    data class CutOutImageScreen(
        val documentId: Int,
        val imageUrl: String?,
        val selectedBackgroundColor: String?,
        val sourceScreen: String
    ) : Page()

    @Serializable
    data class SavedImageScreen(
        val documentId: Int,
        val imagePath: String?,
        val selectedDpi: String,
        val sourceScreen: String
    ) : Page()

    @Serializable
    data class SelectPhotoScreen(val id: Int) : Page()

    @Serializable
    data class HistoryDetailScreen(val name: String) : Page()
}

sealed class Graph {
    @Serializable
    data object Main : Graph()
}

fun Page.route(): String? {
    return this.javaClass.canonicalName
}