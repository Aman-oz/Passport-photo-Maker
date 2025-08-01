package com.ots.aipassportphotomaker.common.ext

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.


fun <T> singleSharedFlow() = MutableSharedFlow<T>(
    replay = 0,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

@SuppressLint("ComposableNaming")
@Composable
fun <T> Flow<T>.collectAsEffect(effect: suspend (T) -> Unit) {
    LaunchedEffect(key1 = Unit) {
        onEach(effect).launchIn(this)
    }
}