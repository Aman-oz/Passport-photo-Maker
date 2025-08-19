package com.ots.aipassportphotomaker.domain.util

import android.content.Context
import com.google.gson.Gson
import com.ots.aipassportphotomaker.data.model.DocumentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
object JsonDataReader {

    fun <T> readFromAssets(context: Context, fileName: String, classType: Class<T>): T? {
        return try {
            val jsonString = context.assets.open(fileName)
                .bufferedReader()
                .use { it.readText() }
            Gson().fromJson(jsonString, classType)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}