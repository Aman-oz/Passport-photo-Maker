package com.ots.aipassportphotomaker.domain.repository

import kotlinx.coroutines.flow.Flow

// Created by amanullah on 22/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
interface SharedRepository {

    fun getConsentEvent(): Flow<Unit>
    suspend fun triggerConsentEvent()
}