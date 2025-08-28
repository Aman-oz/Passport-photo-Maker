package com.ots.aipassportphotomaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.ots.aipassportphotomaker.data.util.DiskExecutor
import com.ots.aipassportphotomaker.data.util.NetworkMonitorImpl
import com.ots.aipassportphotomaker.domain.permission.PermissionsHelper
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
}