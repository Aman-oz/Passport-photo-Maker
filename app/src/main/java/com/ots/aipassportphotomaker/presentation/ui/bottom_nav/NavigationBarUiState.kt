package com.ots.aipassportphotomaker.presentation.ui.bottom_nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.domain.bottom_nav.Page

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
data class NavigationBarUiState(
    val bottomItems: List<BottomNavigationBarItem> = listOf(
        BottomNavigationBarItem.Home,
        BottomNavigationBarItem.CreateID,
        BottomNavigationBarItem.History
    )
)

sealed class BottomNavigationBarItem(
    val tabName: String,
    val icon: @Composable () -> Painter,
    val page: Page,
) {
    data object Home : BottomNavigationBarItem(
        tabName = "Home",
        icon = { painterResource(id = R.drawable.home_icon) },
        page = Page.Home
    )

    data object CreateID : BottomNavigationBarItem(
        tabName = "Create ID",
        icon = { painterResource(id = R.drawable.create_id_icon) },
        page = Page.PhotoID
    )

    data object History : BottomNavigationBarItem(
        tabName = "History",
        icon = { painterResource(id = R.drawable.history_icon_new) },
        page = Page.History
    )
}
