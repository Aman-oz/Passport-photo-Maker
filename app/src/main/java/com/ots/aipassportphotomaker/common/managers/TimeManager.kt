package com.ots.aipassportphotomaker.common.managers

// Created by amanullah on 21/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class TimeManager private constructor() {
    companion object {
        private const val TAG = "MediationModule"
        private var instance: TimeManager? = null

        @Synchronized
        fun getInstance(): TimeManager {
            return instance ?: TimeManager().also { instance = it }
        }
    }

    private var isRunning: Boolean = false
    private var startTime: Long = 0

    fun setStartTime(value: Long) {
        startTime = value
    }

    fun start() {
        if (!isRunning) {
            android.util.Log.d(TAG, "TimeManager started")
            isRunning = true
            startTime = System.currentTimeMillis()
        } else {
            android.util.Log.d(TAG, "TimeManager is already running")
        }
    }

    fun stop() {
        if (isRunning) {
            android.util.Log.d(TAG, "TimeManager stopped")
            isRunning = false
            // You can perform any cleanup or stop tasks here
        } else {
            android.util.Log.d(TAG, "TimeManager is not running")
        }
    }

    fun getCurrentTime(): String {
        val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    fun getElapsedTime(): Long {
        return if (isRunning) System.currentTimeMillis() - startTime else 0
    }

    fun getElapsedTimeInSecs(): Long {
        return if (isRunning) (System.currentTimeMillis() - startTime) / 1000 else 0
    }
}