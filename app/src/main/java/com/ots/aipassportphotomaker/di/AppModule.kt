package com.ots.aipassportphotomaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.ots.aipassportphotomaker.data.util.NetworkMonitorImpl
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
}