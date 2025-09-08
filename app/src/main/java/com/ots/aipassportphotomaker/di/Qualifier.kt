package com.ots.aipassportphotomaker.di

import javax.inject.Qualifier

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AppSettingsSharedPreference

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OziBackgroundRemoverService
