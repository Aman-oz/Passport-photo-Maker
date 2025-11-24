package com.ots.aipassportphotomaker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ots.aipassportphotomaker.common.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Created by amanullah on 12/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _editedImageResult = MutableStateFlow<String?>(null)
    private val _removedBgResult = MutableStateFlow<Boolean>(false)
    val editedImageResult = _editedImageResult.asStateFlow()
    val removedBgResult = _removedBgResult.asStateFlow()

    fun setEditedImageResult(imageUrl: String?) {
        Logger.i("SharedViewModel", "setEditedImageResult: $imageUrl")
        _editedImageResult.value = imageUrl
    }

    fun clearResult() {
        _editedImageResult.value = null
    }

    fun setRemovedBgResult(isBgRemoved: Boolean) {
        Logger.i("SharedViewModel", "setRemoveBgResult: $isBgRemoved")
        _removedBgResult.value = isBgRemoved
    }

    fun clearRemovedBgResult() {
        _removedBgResult.value = false
    }
}