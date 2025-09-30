package com.ots.aipassportphotomaker.common.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ots.aipassportphotomaker.common.notification.CustomNotification

// Created by amanullah on 29/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class MyFirebaseMessagingService: FirebaseMessagingService() {
    private val TAG = "MyFirebaseMessigingService"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            CustomNotification(this).showMainNotification(this, it.title,it.body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}