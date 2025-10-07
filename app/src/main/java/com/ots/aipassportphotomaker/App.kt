package com.ots.aipassportphotomaker

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.aman.downloader.DownloaderConfig
import com.aman.downloader.OziDownloader
import com.aman.downloader.OziDownloader.Companion.create
import com.android.billingclient.BuildConfig
import com.las.collage.maker.iab.ProductItem
import com.ots.aipassportphotomaker.adsmanager.admob.MyAdsManager
import com.ots.aipassportphotomaker.common.iab.AppBillingClient
import com.ots.aipassportphotomaker.common.iab.interfaces.ConnectResponse
import com.ots.aipassportphotomaker.common.iab.interfaces.PurchaseResponse
import com.ots.aipassportphotomaker.common.iab.subscription.SubscriptionItem
import com.ots.aipassportphotomaker.common.managers.AdsConsentManager
import com.ots.aipassportphotomaker.common.managers.PreferencesHelper
import com.ots.aipassportphotomaker.common.utils.AdsConstants
import com.ots.aipassportphotomaker.common.utils.SharedPrefUtils
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@HiltAndroidApp
class App: Application(), ImageLoaderFactory {
    private val TAG: String = "AppTag"

    var oziDownloader: OziDownloader? = null
    val appBillingClient: AppBillingClient by lazy { AppBillingClient() }

    var skuDetail: SubscriptionItem? = null

    @Inject
    lateinit var adsManager: MyAdsManager

    @Inject
    lateinit var adsConsentManager: AdsConsentManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    companion object {
        private lateinit var instance: App
        @JvmStatic
        fun getInstance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        oziDownloader = create(applicationContext, DownloaderConfig())

        getSubscriptionDetails()
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

    private fun getSubscriptionDetails() {
        appBillingClient.connect(this, object : ConnectResponse {
            override fun disconnected() {
                Log.d(TAG, "InappBilling connection disconnected.")
            }

            override fun billingUnavailable() {
                Log.d(TAG, "InappBilling billing unavailable.")
            }

            override fun developerError() {
                Log.d(TAG, "InappBilling developer error.")
            }

            override fun error() {
                Log.d(TAG, "InappBilling simple error.")
            }

            override fun featureNotSupported() {
                Log.d(TAG, "InappBilling feature not available.")
            }

            override fun itemUnavailable() {
                Log.d(TAG, "InappBilling item not available.")
            }

            override fun ok(subscriptionItems: List<SubscriptionItem>) {
                Log.d(TAG, "InappBilling connection ok. Processing subscription.")
                for (it in subscriptionItems) {
                    if (it.subscribedItem != null) {
                        skuDetail = it
                    }
                }
                adsManager.setEnabledNoAds(skuDetail != null && skuDetail?.subscribedItem != null)
            }

            override fun serviceDisconnected() {
                Log.d(TAG, "InappBilling service disconnected.")
            }

            override fun serviceUnavailable() {
                Log.d(TAG, "InappBilling service unavailable.")
            }
        }, object : PurchaseResponse {
            override fun isAlreadyOwned() {}
            override fun userCancelled() {
                adsManager.setEnabledNoAds(false)
            }

            override fun ok(productItem: ProductItem) {}
            override fun error(error: String) {}
        })
    }
}