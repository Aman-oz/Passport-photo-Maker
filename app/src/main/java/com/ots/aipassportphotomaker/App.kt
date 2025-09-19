package com.ots.aipassportphotomaker

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.aman.downloader.DownloaderConfig
import com.aman.downloader.OziDownloader
import com.aman.downloader.OziDownloader.Companion.create
import com.ots.aipassportphotomaker.common.iab.AppBillingClient
import dagger.hilt.android.HiltAndroidApp

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@HiltAndroidApp
class App: Application(), ImageLoaderFactory {

    var oziDownloader: OziDownloader? = null
    val appBillingClient: AppBillingClient by lazy { AppBillingClient() }

    override fun onCreate() {
        super.onCreate()

        oziDownloader = create(applicationContext, DownloaderConfig())
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader(this).newBuilder()
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.1)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.3)
                    .directory(cacheDir)
                    .build()
            }
            .logger(DebugLogger())
            .build()
    }
}