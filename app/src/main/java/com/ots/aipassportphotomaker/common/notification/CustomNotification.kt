package com.ots.aipassportphotomaker.common.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.util.Pair
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.presentation.ui.main.MainActivity

// Created by amanullah on 29/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class CustomNotification(val context: Context) {
    //notification manager
    private var notificationManager: NotificationManager? = null
        get() {
            if (field != null) {
                return field
            }
            field = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return field
        }
    val NOTIFICATION_ID = 1337
    val RECORDING_NOTIFICATION_ID = 101
    private val NOTIFICATION_CHANNEL_ID = "com.iamhco.gms.app"
    private val NOTIFICATION_CHANNEL_NAME = "com.iamhco.gms"
    fun showMainNotification(
        context: Context,
        title: String?,
        body: String?
    ): androidx.core.util.Pair<Int, Notification> {
        createNotificationChannel()
        Log.d("ADS==>", "Message Notification Body: ")
        val notification = createMainCustomNotification(context, title, body)
        notificationManager!!.notify(RECORDING_NOTIFICATION_ID, notification)
        return Pair(RECORDING_NOTIFICATION_ID, notification)
    }

    //notification channel
    @SuppressLint("WrongConstant")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)

            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    //custom notification builder
    private fun createMainCustomNotification(
        context: Context,
        title: String?,
        body: String?,
    ): Notification {
        // notification builder

        return customNotificationBuilder(context, title, body)
    }

    //notification builder
    private fun customNotificationBuilder(
        context: Context,
        title: String?,
        body: String?
    ): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setShowWhen(true)
            .setAutoCancel(true)
            .setContentTitle(title ?: "")
            .setContentText(body ?: "")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(getPendingIntent(context))
            .build()
    }

    //pending intent
    private fun getPendingIntent(context: Context): PendingIntent? {

        try {
//            val c = Class.forName(StartScreen)
            val intent = Intent(context, MainActivity::class.java)
            return PendingIntent.getActivity(context, 0, intent, getPendingIntent())
        } catch (ignored: ClassNotFoundException) {
            ignored.printStackTrace()
        }
        return null
    }

    //method to remove notification by id
    fun removeNotification(notificationId: Int) {
        notificationManager!!.cancel(notificationId)
    }

    private fun getPendingIntent(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }

}