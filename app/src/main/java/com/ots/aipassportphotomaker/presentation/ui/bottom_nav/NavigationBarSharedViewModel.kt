package com.ots.aipassportphotomaker.presentation.ui.bottom_nav

import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@HiltViewModel
class NavigationBarSharedViewModel @Inject constructor() : BaseViewModel() {

    private val _bottomItem = singleSharedFlow<BottomNavigationBarItem>()
    val bottomItem = _bottomItem.asSharedFlow()

    // Change to StateFlow instead of SharedFlow
    private val _editedImageResult = MutableStateFlow<String?>(null)
    val editedImageResult = _editedImageResult.asStateFlow()

    var isFirstLaunch = false

    fun onBottomItemClicked(bottomItem: BottomNavigationBarItem) = launch {
        _bottomItem.emit(bottomItem)
    }

    fun setEditedImageResult(imageUrl: String?) {
        Logger.i("SharedViewModelTag", "setEditedImageResult: $imageUrl")
        _editedImageResult.value = imageUrl
    }

    fun clearResult() {
        _editedImageResult.value = null
    }
}