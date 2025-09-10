package com.ots.aipassportphotomaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.ots.aipassportphotomaker.data.remote.api.CropImageApi
import com.ots.aipassportphotomaker.data.remote.api.RemoveBackgroundApi
import com.ots.aipassportphotomaker.data.repository.CropImageRepositoryImpl
import com.ots.aipassportphotomaker.data.repository.RemoveBackgroundRepositoryImpl
import com.ots.aipassportphotomaker.data.repository.SuitsDataSource
import com.ots.aipassportphotomaker.data.repository.SuitsRepositoryImpl
import com.ots.aipassportphotomaker.data.util.DiskExecutor
import com.ots.aipassportphotomaker.data.util.NetworkMonitorImpl
import com.ots.aipassportphotomaker.domain.permission.PermissionsHelper
import com.ots.aipassportphotomaker.domain.repository.CropImageRepository
import com.ots.aipassportphotomaker.domain.repository.RemoveBackgroundRepository
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
        return context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
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
}