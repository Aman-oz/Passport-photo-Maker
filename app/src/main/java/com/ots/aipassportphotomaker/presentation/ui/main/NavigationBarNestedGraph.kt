package com.ots.aipassportphotomaker.presentation.ui.main

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ots.aipassportphotomaker.common.ext.composableHorizontalSlide
import com.ots.aipassportphotomaker.common.ext.sharedViewModel
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDPage
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.history.HistoryPage
import com.ots.aipassportphotomaker.presentation.ui.history.HistoryScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.home.HomePage
import com.ots.aipassportphotomaker.presentation.ui.home.HomeScreenViewModel
import kotlin.reflect.KClass

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Composable
fun NavigationBarNestedGraph(
    navController: NavHostController,
    mainNavController: NavHostController,
    parentRoute: KClass<*>?
) {
    NavHost(
        navController = navController,
        startDestination = Page.Home,
        route = parentRoute
    ) {
        composableHorizontalSlide<Page.Home> { backStack ->
            val viewModel = hiltViewModel<HomeScreenViewModel>()
            HomePage(
                mainRouter = MainRouter(mainNavController),
                viewModel = viewModel,
                sharedViewModel = backStack.sharedViewModel(navController = mainNavController)
            )
        }
        composableHorizontalSlide<Page.PhotoID> { backStack ->
            val viewModel = hiltViewModel<PhotoIDScreenViewModel>()
            PhotoIDPage(
                mainRouter = MainRouter(mainNavController),
                viewModel = viewModel,
                sharedViewModel = backStack.sharedViewModel(navController = mainNavController)
            )
        }

        composableHorizontalSlide<Page.History> { backStack ->
            val viewModel = hiltViewModel<HistoryScreenViewModel>()
            HistoryPage(
                mainRouter = MainRouter(mainNavController),
                viewModel = viewModel,
                sharedViewModel = backStack.sharedViewModel(navController = mainNavController)
            )
        }
    }
}