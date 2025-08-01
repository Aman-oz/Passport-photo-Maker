package com.ots.aipassportphotomaker.presentation.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ots.aipassportphotomaker.common.ext.composableHorizontalSlide
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
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
            /*val viewModel = hiltViewModel<FeedViewModel>()
            FeedPage(
                mainRouter = MainRouter(mainNavController),
                viewModel = viewModel,
                sharedViewModel = backStack.sharedViewModel(navController = mainNavController)
            )*/
        }
        composableHorizontalSlide<Page.CreateID> {
            /*val viewModel = hiltViewModel<FavoritesViewModel>()
            FavoritesPage(
                mainRouter = MainRouter(mainNavController),
                viewModel = viewModel,
            )*/
        }

        composableHorizontalSlide<Page.History> {
            /*val viewModel = hiltViewModel<ProfileViewModel>()
            ProfilePage(
                mainRouter = MainRouter(mainNavController),
                viewModel = viewModel,
            )*/
        }
    }
}