package com.ots.aipassportphotomaker.presentation.ui.main

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ots.aipassportphotomaker.common.ext.composableHorizontalSlide
import com.ots.aipassportphotomaker.common.ext.sharedViewModel
import com.ots.aipassportphotomaker.domain.bottom_nav.Graph
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarScreen
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.home.HomePage
import com.ots.aipassportphotomaker.presentation.ui.home.HomeScreenViewModel

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun MainGraph(
    mainNavController: NavHostController,
    darkMode: Boolean,
    onThemeUpdated: () -> Unit
) {
    NavHost(
        navController = mainNavController,
        startDestination = Page.NavigationBar,
        route = Graph.Main::class
    ) {
        composableHorizontalSlide<Page.NavigationBar> { backStack ->
            val nestedNavController = rememberNavController()
            NavigationBarScreen(
                sharedViewModel = backStack.sharedViewModel(navController = mainNavController),
                mainRouter = MainRouter(mainNavController),
                darkMode = darkMode,
                onThemeUpdated = onThemeUpdated,
                nestedNavController = nestedNavController
            ) {
                NavigationBarNestedGraph(
                    navController = nestedNavController,
                    mainNavController = mainNavController,
                    parentRoute = Graph.Main::class
                )
            }
        }

        composableHorizontalSlide<Page.Home> {
            val viewModel = hiltViewModel<HomeScreenViewModel>()
            val sharedViewModel = hiltViewModel<NavigationBarSharedViewModel>()
            val mainRouter = MainRouter(mainNavController)
            HomePage(
                mainRouter = mainRouter,
                viewModel = viewModel,
                sharedViewModel = sharedViewModel,
            )
        }

        /*composableHorizontalSlide<Page.MovieDetails> {
            val viewModel = hiltViewModel<MovieDetailsViewModel>()
            MovieDetailsPage(
                mainNavController = mainNavController,
                viewModel = viewModel,
            )
        }*/
    }
}