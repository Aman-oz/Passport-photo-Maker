package com.ots.aipassportphotomaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.ots.aipassportphotomaker.adsmanager.admob.MyAdsManager
import com.ots.aipassportphotomaker.common.iab.AppBillingClient
import com.ots.aipassportphotomaker.common.managers.AdsConsentManager
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.managers.AppLocaleManager
import com.ots.aipassportphotomaker.common.managers.PreferencesHelper
import com.ots.aipassportphotomaker.common.utils.SharedPrefUtils
import com.ots.aipassportphotomaker.data.remote.api.CropImageApi
import com.ots.aipassportphotomaker.data.remote.api.RemoveBackgroundApi
import com.ots.aipassportphotomaker.data.repository.CropImageRepositoryImpl
import com.ots.aipassportphotomaker.data.repository.RemoveBackgroundRepositoryImpl
import com.ots.aipassportphotomaker.data.repository.SuitsDataSource
import com.ots.aipassportphotomaker.data.repository.SuitsRepositoryImpl
import com.ots.aipassportphotomaker.data.repository.shared.SharedRepositoryImpl
import com.ots.aipassportphotomaker.data.util.DiskExecutor
import com.ots.aipassportphotomaker.data.util.NetworkMonitorImpl
import com.ots.aipassportphotomaker.domain.permission.PermissionsHelper
import com.ots.aipassportphotomaker.domain.repository.CropImageRepository
import com.ots.aipassportphotomaker.domain.repository.RemoveBackgroundRepository
import com.ots.aipassportphotomaker.domain.repository.SharedRepository
import com.ots.aipassportphotomaker.domain.repository.SuitsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Created by amanullah on 24/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideDiskExecutor(): DiskExecutor {
        return DiskExecutor()
    }
    @Provides
    @AppSettingsSharedPreference
    fun provideAppSettingsSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPrefUtils.PREF_KEY, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePreferencesHelper(@ApplicationContext context: Context): PreferencesHelper {
        return PreferencesHelper(context)
    }

    @Provides
    @Singleton
    fun provideAnalyticsManager(@ApplicationContext context: Context): AnalyticsManager {
        return AnalyticsManager(context)
    }

    @Provides
    @Singleton
    fun provideAdsManager(@ApplicationContext context: Context, analyticsManager: AnalyticsManager, preferencesHelper: PreferencesHelper): MyAdsManager {
        return MyAdsManager(context, analyticsManager, preferencesHelper)
    }

    @Provides
    @Singleton
    fun provideConsentManager(@ApplicationContext context: Context): AdsConsentManager {
        return AdsConsentManager(context)
    }

    @Provides
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitorImpl = NetworkMonitorImpl(context)

    @Provides
    @Singleton
    fun provideSharedRepository(
        @ApplicationContext context: Context
    ): SharedRepository = SharedRepositoryImpl(context)

    @Provides
    @Singleton
    fun providePermissionsHelper(): PermissionsHelper = PermissionsHelper()

    @Provides
    fun provideCropImageRepository(cropImageApi: CropImageApi): CropImageRepository {
        return CropImageRepositoryImpl(cropImageApi)
    }

    @Provides
    fun provideRemoveBackgroundRepository(api: RemoveBackgroundApi): RemoveBackgroundRepository {
        return RemoveBackgroundRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideSuitsRepository(suitsDataSource: SuitsDataSource): SuitsRepository {
        return SuitsRepositoryImpl(suitsDataSource)
    }

    @Provides
    @Singleton
    fun provideBillingClient() : AppBillingClient {
        return AppBillingClient()
    }

    @Singleton
    @Provides
    fun provideAppLocaleManager(@ApplicationContext context: Context): AppLocaleManager {
        return AppLocaleManager(context)
    }
}