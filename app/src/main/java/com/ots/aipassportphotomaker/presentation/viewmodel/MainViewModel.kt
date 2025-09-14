package com.ots.aipassportphotomaker.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel

// Created by amanullah on 15/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class MainViewModel: BaseViewModel() {

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }
}