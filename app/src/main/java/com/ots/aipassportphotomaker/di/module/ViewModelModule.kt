package com.ots.aipassportphotomaker.di.module

import com.ots.aipassportphotomaker.presentation.viewmodel.SharedViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped

// Created by amanullah on 12/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Module
@InstallIn(ActivityComponent::class) // Activity scope for sharing between fragments/screens
class ViewModelModule {
    /*@Provides
    @ActivityScoped
    fun provideSharedViewModel(): SharedViewModel = SharedViewModel()*/
}