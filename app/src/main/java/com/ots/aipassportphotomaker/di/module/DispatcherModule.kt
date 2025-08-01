package com.ots.aipassportphotomaker.di.module

import com.ots.aipassportphotomaker.data.util.DispatchersProviderImpl
import com.ots.aipassportphotomaker.di.DefaultDispatcher
import com.ots.aipassportphotomaker.di.IoDispatcher
import com.ots.aipassportphotomaker.di.MainDispatcher
import com.ots.aipassportphotomaker.domain.util.DispatchersProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Module
@InstallIn(SingletonComponent::class)
class DispatcherModule {

    @Provides
    @IoDispatcher
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @MainDispatcher
    fun providesMainDispatcher(): MainCoroutineDispatcher = Dispatchers.Main

    @Provides
    fun providesDispatcherProvider(
        @IoDispatcher io: CoroutineDispatcher,
        @MainDispatcher main: MainCoroutineDispatcher,
        @DefaultDispatcher default: CoroutineDispatcher
    ): DispatchersProvider {
        return DispatchersProviderImpl(
            io = io,
            main = main,
            default = default
        )
    }
}