package com.ots.aipassportphotomaker.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.data.model.DocumentJson
import com.ots.aipassportphotomaker.data.model.DocumentJsonResponse
import com.ots.aipassportphotomaker.data.model.toDomain
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.util.JsonDataReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class DocumentAssetDataSource(private val context: Context) {

    /*suspend fun getDocuments(): List<DocumentEntity> = withContext(Dispatchers.IO) {
        val response = JsonDataReader.readFromAssets(
            context,
            "document_sizes.json",
            DocumentJsonResponse::class.java
        ) ?: return@withContext emptyList()

        response.documents.map { document ->
            DocumentEntity(
                id = document.id,
                name = document.Name,
                size = document.Size,
                unit = document.Unit,
                pixels = document.Pixels,
                resolution = document.Resolution,
                image = document.Image,
                type = document.Type,
                completed = document.completed
            )
        }
    }*/
    suspend fun getDocuments(): List<DocumentEntity> = withContext(Dispatchers.IO) {
        try {
            // Read raw JSON string
            val jsonString = context.assets.open("document_sizes.json")
                .bufferedReader()
                .use { it.readText() }

            // Parse JSON into JsonObject
            val jsonObject = Gson().fromJson(jsonString, JsonObject::class.java)
                ?: throw IllegalStateException("Failed to parse JSON from document_sizes.json")

            Logger.d("DocumentAssetDataSource", "getDocuments: Parsed JSON successfully data: ${jsonObject.entrySet().size} categories found")
            // Flatten all categories into a single list
            val documents = mutableListOf<DocumentJson>()
            var idCounter = 1

            jsonObject.entrySet().forEach { (category, jsonElement) ->
                val jsonArray = jsonElement.asJsonArray
                jsonArray.forEach { element ->
                    val obj = element.asJsonObject
                    documents.add(
                        DocumentJson(
                            id = idCounter++,
                            Name = obj.get("Name").asString,
                            Size = obj.get("Size").asString,
                            Unit = obj.get("Unit").asString,
                            Pixels = obj.get("Pixels").asString,
                            Resolution = obj.get("Resolution").asString,
                            Image = obj.get("Image")?.asString,
                            Type = obj.get("Type").asString,
                            completed = obj.get("Completed")?.asString
                        )
                    )
                }
            }

            // Convert to DocumentEntity
            documents.map { it.toDomain() }
        } catch (e: Exception) {
            // Log error for debugging
            e.printStackTrace()
            emptyList() // Return empty list instead of crashing
        }
    }
}