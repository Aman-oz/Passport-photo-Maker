package com.ots.aipassportphotomaker.domain.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.ots.aipassportphotomaker.data.model.DocumentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
object JsonDataReader {

    fun <T> readFromAssets(context: Context, fileName: String, classType: Class<T>): T {
        try {
            val jsonString = context.assets.open(fileName)
                .bufferedReader()
                .use { it.readText() }
            return Gson().fromJson(jsonString, classType)
                ?: throw IllegalStateException("Failed to deserialize JSON from $fileName")
        } catch (e: IOException) {
            throw IOException("Failed to read asset file: $fileName", e)
        } catch (e: JsonSyntaxException) {
            throw IllegalStateException("Invalid JSON format in $fileName", e)
        }
    }
}