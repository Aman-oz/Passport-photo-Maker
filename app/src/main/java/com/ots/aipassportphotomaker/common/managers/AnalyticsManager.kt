package com.ots.aipassportphotomaker.common.managers

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.ots.aipassportphotomaker.common.ext.replaceSpaceWithUnderscore
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.common.utils.SharedPrefUtils

// Created by amanullah on 20/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class AnalyticsManager(val context: Context) {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)

    companion object {
        val TAG = AnalyticsManager::class.java.simpleName
        @Volatile
        private var instance: AnalyticsManager? = null

        fun initialize(application: Application) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = AnalyticsManager(application)
                        Log.d(TAG, "AnalyticsManager initialized")
                    }
                }
            }
        }

        fun getInstance(): AnalyticsManager {
            return instance
                ?: throw IllegalStateException("AnalyticsManager must be initialized in Application class")
        }
    }

    /**
     * Enable or disable Firebase Analytics data collection
     * based on user consent (GDPR compliance).
     */
    fun setAnalyticsCollectionEnabled(isEnabled: Boolean) {
        firebaseAnalytics.setAnalyticsCollectionEnabled(isEnabled)
        cacheConsent(isEnabled)
        Log.d(TAG, "Firebase Analytics collection enabled = $isEnabled")
    }

    fun sendAnalytics(actionName: String?, actionDetail: String?) {

        if (!isAnalyticsAllowed()) {
            Log.d(TAG, "Analytics disabled — skipping event: $actionName and $actionDetail")
            return
        }

        if (actionDetail == null || actionName == null) {
            Log.w(TAG, "Skipping analytics: actionName or actionDetail is null")
            return
        }

        val truncatedActionDetail = if (actionDetail.length > 40) {
            Log.w(TAG, "Event detail truncated to 40 characters: $actionDetail")
            actionDetail.take(40)
        } else {
            actionDetail
        }

        val normalizedActionDetail = truncatedActionDetail.replaceSpaceWithUnderscore()
        val bundle = Bundle().apply {
            putString(AnalyticsConstants.ACTION_VIEW, normalizedActionDetail)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, actionName)
        }
        firebaseAnalytics.logEvent(normalizedActionDetail, bundle)
        Log.d(TAG, "actionName: $actionName actionDetail: $normalizedActionDetail")
    }

    fun sendEvent(eventName: String, params: Bundle) {
        if (!isAnalyticsAllowed()) {
            Log.d(TAG, "Analytics disabled — skipping event: $eventName and $params")
            return
        }

        val truncatedEventName = if (eventName.length > 40) {
            Log.w(TAG, "Event name truncated to 40 characters: $eventName")
            eventName.take(40)
        } else {
            eventName
        }
        val normalizedEventName = truncatedEventName.replaceSpaceWithUnderscore()
        firebaseAnalytics.logEvent(normalizedEventName, params)
        Log.d(TAG, "sendEvent: $normalizedEventName $params")
    }

    /**
     * Checks whether Firebase Analytics is currently enabled.
     */
    private fun isAnalyticsAllowed(): Boolean {
        return try {
            val prefs = context.getSharedPreferences(SharedPrefUtils.PREF_KEY, Context.MODE_PRIVATE)
            prefs.getBoolean(SharedPrefUtils.IS_CONSENT_GIVEN, false)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check analytics state: ${e.message}")
            false
        }
    }

    /**
     * Save consent locally for faster access on next app launch.
     */
    fun cacheConsent(isGiven: Boolean) {
        val prefs = context.getSharedPreferences(SharedPrefUtils.PREF_KEY, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(SharedPrefUtils.IS_CONSENT_GIVEN, isGiven).apply()
//        setAnalyticsCollectionEnabled(isGiven)
    }
}