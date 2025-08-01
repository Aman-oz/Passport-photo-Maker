package com.ots.aipassportphotomaker.presentation.ui.bottom_nav

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.domain.bottom_nav.route
import com.ots.aipassportphotomaker.presentation.ui.components.TopBar
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.AppColors
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.funnelFamily

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Composable
fun NavigationBarScreen(
    sharedViewModel: NavigationBarSharedViewModel,
    mainRouter: MainRouter,
    darkMode: Boolean,
    onThemeUpdated: () -> Unit,
    nestedNavController: NavHostController,
    content: @Composable () -> Unit
) {
    val uiState = NavigationBarUiState()
    Scaffold(
        topBar = {
            TopBar(
                stringResource(id = R.string.photo_id),
                darkMode,
                fontFamily = funnelFamily,
                onThemeUpdated = onThemeUpdated,
                onGetProClick = {
                    mainRouter.navigateToSearch()
                }
            )

        },
        bottomBar = {
            BottomNavigationBar(
                items = uiState.bottomItems,
                navController = nestedNavController,
                onItemClick = { bottomItem ->
                    val currentPageRoute = nestedNavController.currentDestination?.route
                    val clickedPageRoute = bottomItem.page
                    val notSamePage = currentPageRoute != clickedPageRoute.route()
                    if (notSamePage) {
                        nestedNavController.navigate(clickedPageRoute) {
                            launchSingleTop = true
                            popUpTo(nestedNavController.graph.findStartDestination().id)
                        }
                    }
                    sharedViewModel.onBottomItemClicked(bottomItem)
                }
            )
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize(1f)
                .padding(paddingValues)
        ) {
            content()
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NavigationBarScreenPreview() = PreviewContainer {
    val navController = rememberNavController()
    val mainRouter = MainRouter(navController)
    val darkTheme = isSystemInDarkTheme()

    NavigationBarScreen(
        sharedViewModel = NavigationBarSharedViewModel(),
        mainRouter = mainRouter,
        darkMode = darkTheme,
        onThemeUpdated = { },
        nestedNavController = navController,
        content = {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color = colors.background)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 20.sp,
                    text = "Page Content"
                )
            }
        }
    )
}