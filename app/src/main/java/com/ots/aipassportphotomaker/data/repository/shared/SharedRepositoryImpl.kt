package com.ots.aipassportphotomaker.data.repository.shared

import android.content.Context
import com.ots.aipassportphotomaker.domain.repository.SharedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

// Created by amanullah on 22/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class SharedRepositoryImpl @Inject constructor(
    context: Context
): SharedRepository {
    private val _consentEvent = MutableSharedFlow<Unit>()

    override fun getConsentEvent(): Flow<Unit> = _consentEvent.asSharedFlow()

    override suspend fun triggerConsentEvent() {
        _consentEvent.emit(Unit)
    }
}