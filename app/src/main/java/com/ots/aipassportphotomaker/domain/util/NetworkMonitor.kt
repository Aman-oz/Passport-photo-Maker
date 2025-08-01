package com.ots.aipassportphotomaker.domain.util

import kotlinx.coroutines.flow.Flow

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

interface NetworkMonitor {
    val networkState: Flow<NetworkState>
}

data class NetworkState(
    val isOnline: Boolean,
    val shouldRefresh: Boolean
)