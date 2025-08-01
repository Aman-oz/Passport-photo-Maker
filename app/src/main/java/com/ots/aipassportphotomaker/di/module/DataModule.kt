package com.ots.aipassportphotomaker.di.module

import android.content.Context
import com.ots.aipassportphotomaker.data.util.NetworkMonitorImpl
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor = NetworkMonitorImpl(context)
}