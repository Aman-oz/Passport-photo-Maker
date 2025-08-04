package com.ots.aipassportphotomaker.presentation.ui.main

import androidx.navigation.NavHostController
import com.ots.aipassportphotomaker.domain.bottom_nav.Page

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class MainRouter(
    private val mainNavController: NavHostController
) {

    fun navigateToSearch() {
//        mainNavController.navigate(Page.Search)
    }

    fun navigateToItemDetailScreen(name: String) {
        mainNavController.navigate(Page.ItemDetailScreen(name))
    }
}