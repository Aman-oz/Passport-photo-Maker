package com.ots.aipassportphotomaker.domain.util

import android.content.Context
import com.google.gson.Gson
import com.ots.aipassportphotomaker.data.model.DocumentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class JsonDataReader(private val context: Context) {

    suspend fun readDocumentData(): DocumentData? = withContext(Dispatchers.IO) {
        try {
            context.assets.open("document_sizes.json").use { inputStream ->
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                val jsonString = String(buffer, Charsets.UTF_8)
                Gson().fromJson(jsonString, DocumentData::class.java)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}