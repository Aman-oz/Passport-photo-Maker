package com.ots.aipassportphotomaker.common.managers

import android.content.Context
import android.content.SharedPreferences
import com.ots.aipassportphotomaker.common.utils.SharedPrefUtils

// Created by amanullah on 20/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class PreferencesHelper(context: Context) {

    private val prefHelper: SharedPreferences =
        context.getSharedPreferences(SharedPrefUtils.PREF_KEY, Context.MODE_PRIVATE)

    fun setBoolean(key: String, value: Boolean) {
        prefHelper.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefHelper.getBoolean(key, defaultValue)
    }

    fun setString(key: String, value: String) {
        prefHelper.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String? {
        return prefHelper.getString(key, defaultValue)
    }
}