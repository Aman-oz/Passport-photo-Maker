package com.ots.aipassportphotomaker.common.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.ots.aipassportphotomaker.BuildConfig
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.utils.Logger
import java.io.File

// Created by amanullah on 11/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

const val FacebookPackage = "com.facebook.katana"
const val InstagramPackage = "com.instagram.android"
const val WhatsappPackage = "com.whatsapp"
const val SharePackage = "share"

fun shareMedia(context: Context, imageUri: Uri, mPackageName: String) {
    kotlin.runCatching {
        Log.d("SaveImagePreview", "shareMedia: imageUri: $imageUri")

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name)) // Adjust resource as needed
            putExtra(
                Intent.EXTRA_TEXT,
                "Your professional Passport & Visa photo, made easy with Photo ID Maker. https://play.google.com/store/apps/details?id=${context.packageName}"
            )
            if (mPackageName == SharePackage) {
                shareToOthers(context, imageUri.toString())
                /*val chooserIntent = Intent.createChooser(this, "Share image")
                context.startActivity(chooserIntent)*/
            } else {
                setPackage(mPackageName)
                context.startActivity(this)
            }
        }
    }.getOrElse {
        Log.d("SavedImageScreen", "share: " + it.message)
        shareToOthers(context, imageUri.toString())
        Toast.makeText(context, context.getString(R.string.app_not_installed), Toast.LENGTH_SHORT).show()
    }
}

fun shareToOthers(context: Context, imagePath: String?) {
    imagePath?.let {
        ShareCompat.IntentBuilder(context)
            .setType("image/jpeg")
            .addStream(it.toUri())
            .setChooserTitle("Share image")
            .setSubject("Shared image")
            .startChooser()
    }
}