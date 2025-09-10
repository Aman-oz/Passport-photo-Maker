package com.ots.aipassportphotomaker.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.data.model.SuitsJson
import com.ots.aipassportphotomaker.data.model.toDomain
import com.ots.aipassportphotomaker.domain.model.SuitsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.component1
import kotlin.collections.component2

// Created by amanullah on 09/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class SuitsDataSource(private val context: Context) {

    suspend fun getSuits(): List<SuitsEntity> = withContext(Dispatchers.IO) {
        try {
            // Read raw JSON string
            val jsonString = context.assets.open("suits.json")
                .bufferedReader()
                .use { it.readText() }

            Logger.d("SuitsDataSource", "Raw JSON: $jsonString")

            // Parse JSON array directly
            val listType = object : TypeToken<List<SuitsJson>>() {}.type
            val suits: List<SuitsJson> = Gson().fromJson(jsonString, listType)
                ?: throw IllegalStateException("Failed to parse JSON from suits.json")

            Logger.d("SuitsDataSource", "getSuits: Total suits loaded: ${suits.size}")

            // Convert to domain entities and return
            suits.map { it.toDomain() }

        } catch (e: Exception) {
            Logger.e("SuitsDataSource", "Error loading suits from JSON: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getSuits1(): List<SuitsEntity> = withContext(Dispatchers.IO) {
        try {
            // Read raw JSON string
            val jsonString = context.assets.open("suits.json")
                .bufferedReader()
                .use { it.readText() }

            // Parse JSON into JsonObject
            val jsonObject = Gson().fromJson(jsonString, JsonObject::class.java)
                ?: throw IllegalStateException("Failed to parse JSON from suits.json")

            Logger.d("SuitsDataSource", "getSuits: Parsed JSON successfully data: ${jsonObject.entrySet().size} categories found")
            // Flatten all categories into a single list
            val suits = mutableListOf<SuitsJson>()
            jsonObject.entrySet().forEach { (category, jsonElement) ->
                val jsonArray = jsonElement.asJsonArray
                jsonArray.forEach { element ->
                    val obj = element.asJsonObject
                    suits.add(
                        SuitsJson(
                            _id = obj.get("_id").asString,
                            name = obj.get("name").asString,
                            image = obj.get("image").asString,
                            thumbnail = obj.get("thumbnail").asString,
                            isPremium = obj.get("isPremium").asBoolean,
                        )
                    )
                }
            }

            suits.map { it.toDomain() }
        } catch (e: Exception) {
            // Log error for debugging
            e.printStackTrace()
            emptyList() // Return empty list instead of crashing
        }
    }
}