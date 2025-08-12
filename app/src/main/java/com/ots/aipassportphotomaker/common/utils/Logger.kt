package com.ots.aipassportphotomaker.common.utils

import android.util.Log
import com.ots.aipassportphotomaker.BuildConfig

// Created by amanullah on 11/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

/**
 * Singleton utility class for logging that only prints logs in debug mode.
 */
object Logger {
    /**
     * Logs a debug message if the app is in debug mode.
     * @param tag The tag to identify the log message.
     * @param message The message to log.
     */
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    /**
     * Logs an error message if the app is in debug mode.
     * @param tag The tag to identify the log message.
     * @param message The message to log.
     */
    fun e(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message)
        }
    }

    /**
     * Logs an error message with an exception if the app is in debug mode.
     * @param tag The tag to identify the log message.
     * @param message The message to log.
     * @param throwable The exception to log.
     */
    fun e(tag: String, message: String, throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, throwable)
        }
    }

    /**
     * Logs an info message if the app is in debug mode.
     * @param tag The tag to identify the log message.
     * @param message The message to log.
     */
    fun i(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }

    /**
     * Logs a warning message if the app is in debug mode.
     * @param tag The tag to identify the log message.
     * @param message The message to log.
     */
    fun w(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message)
        }
    }
}