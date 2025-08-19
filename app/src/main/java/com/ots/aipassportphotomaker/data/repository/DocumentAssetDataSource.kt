package com.ots.aipassportphotomaker.data.repository

import android.content.Context
import com.ots.aipassportphotomaker.data.model.DocumentJsonResponse
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.util.JsonDataReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class DocumentAssetDataSource(private val context: Context) {

    suspend fun getDocuments(): List<DocumentEntity> = withContext(Dispatchers.IO) {
        val response = JsonDataReader.readFromAssets(
            context,
            "document_sizes.json",
            DocumentJsonResponse::class.java
        ) ?: return@withContext emptyList()

        response.documents.map { document ->
            DocumentEntity(
                id = document.id,
                name = document.name,
                size = document.size,
                unit = document.unit,
                pixels = document.pixels,
                resolution = document.resolution,
                image = document.image,
                type = document.type,
                completed = document.completed
            )
        }
    }
}