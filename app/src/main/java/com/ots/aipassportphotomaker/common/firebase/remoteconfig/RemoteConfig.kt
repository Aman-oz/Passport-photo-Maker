package com.ots.aipassportphotomaker.common.firebase.remoteconfig

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.ots.aipassportphotomaker.common.AppConstants.REMOTE_CONFIG_JSON_KEY
import com.ots.aipassportphotomaker.data.model.RemoteConfigModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// Created by amanullah on 30/10/2025.
// Copyright (c) 2025 Ozi Technology. All rights reserved.
object RemoteConfig {

    private val TAG = RemoteConfig::class.java.simpleName

    private var remoteConfigModel: RemoteConfigModel? = null

    var isRemoteConfigFetched = false

    private val remoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    fun initialize(isSuccessful: (Boolean) -> Unit) {

        val configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(60)
            .build()

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.let { config ->

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val result = withTimeoutOrNull(10000) { // Timeout of 10 seconds
                        fetchRemoteConfig()
                    }
                    if (result == true) {
                        Log.d(TAG, "Remote Config fetched successfully.")
                        isRemoteConfigFetched = true
                        updateConfigValues()
                        withContext(Dispatchers.Main) {
                            isSuccessful(true)
                        }
                    } else {
                        Log.e(TAG, "Remote Config fetch timed out or failed.")
                        withContext(Dispatchers.Main) {
                            isSuccessful(false)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching Remote Config: ${e.localizedMessage}")
                    withContext(Dispatchers.Main) {
                        isSuccessful(false)
                    }
                }
            }
        }
    }

    private suspend fun fetchRemoteConfig(): Boolean {
        return suspendCoroutine { continuation ->
            try {
                remoteConfig.fetchAndActivate()
                    .addOnCompleteListener() { updated ->
                        if (updated.isSuccessful) {
                            Log.d(TAG, "fetchRemoteConfig: Remote Config updated.")
                            continuation.resume(true)
                        } else {
                            Log.d(TAG, "fetchRemoteConfig: Remote Config not updated.")
                            continuation.resume(false)
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "fetchRemoteConfig: Exception occurred - ${e.localizedMessage}")
                continuation.resumeWithException(e)
            }
        }
    }

    private fun updateConfigValues() {
        val nativeAdsJsonValues = remoteConfig.getString(REMOTE_CONFIG_JSON_KEY)

        Log.d(TAG, "getRemoteConfig: $nativeAdsJsonValues")

        remoteConfigModel = try {
            if (nativeAdsJsonValues.isNotEmpty()) {
                Gson().fromJson(nativeAdsJsonValues, RemoteConfigModel::class.java)
            } else {
                RemoteConfigModel() // Default values if no data found
            }
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "Error parsing JSON", e)
            RemoteConfigModel() // Return default if parsing fails
        }
    }

    @JvmStatic
    fun getConfigModel(): RemoteConfigModel {
        return remoteConfigModel ?: RemoteConfigModel()
    }
}