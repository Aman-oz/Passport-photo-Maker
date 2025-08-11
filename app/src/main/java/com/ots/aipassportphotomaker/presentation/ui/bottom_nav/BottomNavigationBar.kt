package com.ots.aipassportphotomaker.presentation.ui.bottom_nav

import android.content.res.Configuration
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.impl.utils.forAll
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.domain.bottom_nav.route
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun BottomNavigationBar(
    items: List<BottomNavigationBarItem>,
    navController: NavController,
    onItemClick: (BottomNavigationBarItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar (
        containerColor = colors.background,
        contentColor = colors.onBackground
    ) {
        items.forEach { item ->
            val selected = item.page.route() == backStackEntry.value?.destination?.route
            val tintColor = if (selected) colors.primary else colors.onBackground
            NavigationBarItem(
                alwaysShowLabel = false,
                selected = selected,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(painter = item.icon(), contentDescription = null, tint = tintColor)
                },
                /*label = {
                    Text(text = item.tabName)
                },*/
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    indicatorColor = if (selected) colors.custom400 else colors.background
                )
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BottomNavigationBarViewPreview() {
    PreviewContainer {
        BottomNavigationBar(listOf(
            BottomNavigationBarItem.Home,
            BottomNavigationBarItem.CreateID,
            BottomNavigationBarItem.History
        ), rememberNavController()) {}
    }
}