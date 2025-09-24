package com.ots.aipassportphotomaker.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Created by amanullah on 15/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class MainViewModel: BaseViewModel() {
    private val TAG = "MainViewModel"

    // State to control the exit dialog visibility
    private val _showExitDialog = MutableStateFlow(false)
    val showExitDialog = _showExitDialog.asStateFlow()

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun dismissDialog() {
        visiblePermissionDialogQueue.removeAt(0)
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    // Function to trigger the exit dialog
    fun showExitDialog() {
        Logger.d(TAG, "Showing exit dialog triggered")
        launch {
            _showExitDialog.value = true
        }
    }

    // Function to hide the exit dialog
    fun hideExitDialog() {
        Logger.d(TAG, "Hiding exit dialog")
        launch {
            _showExitDialog.value = false
        }
    }
}