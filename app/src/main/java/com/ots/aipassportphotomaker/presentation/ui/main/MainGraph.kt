package com.ots.aipassportphotomaker.presentation.ui.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ots.aipassportphotomaker.common.ext.composableHorizontalSlide
import com.ots.aipassportphotomaker.common.ext.sharedViewModel
import com.ots.aipassportphotomaker.domain.bottom_nav.Graph
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.bottom_nav.route
import com.ots.aipassportphotomaker.presentation.ui.editimage.EditImagePage
import com.ots.aipassportphotomaker.presentation.ui.editimage.EditImageScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarScreen
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDDetailPage
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDDetailScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDPage
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDPage2
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDScreen2ViewModel
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.cutout.CutOutImagePage
import com.ots.aipassportphotomaker.presentation.ui.cutout.CutOutImageScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentInfoPage
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentInfoScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.home.HomePage
import com.ots.aipassportphotomaker.presentation.ui.home.HomeScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.onboarding.OnboardingPage
import com.ots.aipassportphotomaker.presentation.ui.onboarding.OnboardingScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.permission.PermissionPage
import com.ots.aipassportphotomaker.presentation.ui.permission.PermissionScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.premium.PremiumPage
import com.ots.aipassportphotomaker.presentation.ui.premium.PremiumScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.processimage.ImageProcessingPage
import com.ots.aipassportphotomaker.presentation.ui.processimage.ImageProcessingScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.savedimage.SavedImagePage
import com.ots.aipassportphotomaker.presentation.ui.savedimage.SavedImageScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.splash.GetStartedPage
import com.ots.aipassportphotomaker.presentation.ui.splash.GetStartedScreenViewModel

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun MainGraph(
    mainNavController: NavHostController,
    darkMode: Boolean,
    isFirstLaunch: Boolean,
    onSettingClick: () -> Unit,
    onThemeUpdated: () -> Unit,
    onGetStartedCompleted: (Page) -> Unit,
) {
    NavHost(
        navController = mainNavController,
        startDestination = Page.GetStartedScreen, //Page.NavigationBar,
        route = Graph.Main::class
    ) {
        composableHorizontalSlide<Page.GetStartedScreen> { backStack ->
            val nestedNavController = rememberNavController()
            val getStartedViewModel: GetStartedScreenViewModel = hiltViewModel()
            val sharedViewModel = backStack.sharedViewModel<NavigationBarSharedViewModel>(navController = mainNavController)
            GetStartedPage(
                mainRouter = MainRouter(mainNavController),
                viewModel = getStartedViewModel,
                sharedViewModel = sharedViewModel,
                onGetStartedClick = {
                    val destination = if (isFirstLaunch) Page.OnboardingScreen else Page.NavigationBar
                    onGetStartedCompleted(destination)
                }
            )
        }

        composableHorizontalSlide<Page.OnboardingScreen> { backStack ->
            val nestedNavController = rememberNavController()
            val onboardingViewModel: OnboardingScreenViewModel = hiltViewModel()
            val sharedViewModel = backStack.sharedViewModel<NavigationBarSharedViewModel>(navController = mainNavController)
            OnboardingPage(
                mainRouter = MainRouter(mainNavController),
                viewModel = onboardingViewModel,
                sharedViewModel = sharedViewModel,
                onFinishClick = {
                    mainNavController.navigate(Page.PermissionScreen) {
                        popUpTo(Page.OnboardingScreen::class) { inclusive = true }
                    }
                }
            )
        }

        composableHorizontalSlide<Page.PermissionScreen> { backStack ->
            val nestedNavController = rememberNavController()
            val permissionViewModel: PermissionScreenViewModel = hiltViewModel()
            val sharedViewModel = backStack.sharedViewModel<NavigationBarSharedViewModel>(navController = mainNavController)
            PermissionPage(
                mainRouter = MainRouter(mainNavController),
                viewModel = permissionViewModel,
                sharedViewModel = sharedViewModel,
                onGetStartedClick = {
                    mainNavController.navigate(Page.NavigationBar) {
                        popUpTo(Page.PermissionScreen::class) { inclusive = true }
                    }
                }
            )
        }

        composableHorizontalSlide<Page.Premium> {
            val premiumScreenViewModel: PremiumScreenViewModel = hiltViewModel()
            val sharedViewModel = hiltViewModel<NavigationBarSharedViewModel>()
            PremiumPage(
                mainRouter = MainRouter(mainNavController),
                viewModel = premiumScreenViewModel,
                sharedViewModel = sharedViewModel
            )
        }

        composableHorizontalSlide<Page.NavigationBar> { backStack ->
            val nestedNavController = rememberNavController()
            NavigationBarScreen(
                sharedViewModel = backStack.sharedViewModel(navController = mainNavController),
                mainRouter = MainRouter(mainNavController),
                darkMode = darkMode,
                onSettingClick = {
                    onSettingClick()
                },
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

        composableHorizontalSlide<Page.PhotoID> {
            PhotoIDPage(
                mainRouter = MainRouter(mainNavController),
                viewModel = hiltViewModel<PhotoIDScreenViewModel>(),
                sharedViewModel = hiltViewModel<NavigationBarSharedViewModel>(),
            )
        }

        composableHorizontalSlide<Page.PhotoID2> {
            PhotoIDPage2(
                mainRouter = MainRouter(mainNavController),
                viewModel = hiltViewModel<PhotoIDScreen2ViewModel>(),
                sharedViewModel = hiltViewModel<NavigationBarSharedViewModel>(),
            )
        }

        composableHorizontalSlide<Page.PhotoIDDetailScreen> {
            PhotoIDDetailPage(
                mainRouter = MainRouter(mainNavController),
                viewModel = hiltViewModel<PhotoIDDetailScreenViewModel>(),
                sharedViewModel = hiltViewModel<NavigationBarSharedViewModel>(),
            )
        }

        composableHorizontalSlide<Page.DocumentInfoScreen> {
            DocumentInfoPage(
                mainRouter = MainRouter(mainNavController),
                viewModel = hiltViewModel<DocumentInfoScreenViewModel>(),
                sharedViewModel = hiltViewModel<NavigationBarSharedViewModel>(),
            )
        }

        composableHorizontalSlide<Page.ImageProcessingScreen> {
            ImageProcessingPage(
                mainRouter = MainRouter(mainNavController),
                viewModel = hiltViewModel<ImageProcessingScreenViewModel>(),
                sharedViewModel = hiltViewModel<NavigationBarSharedViewModel>(),
            )
        }

        composableHorizontalSlide<Page.EditImageScreen> {
            EditImagePage(
                mainRouter = MainRouter(mainNavController),
                viewModel = hiltViewModel<EditImageScreenViewModel>(),
                sharedViewModel = hiltViewModel<NavigationBarSharedViewModel>(),
            )
        }

        composableHorizontalSlide<Page.CutOutImageScreen> {
            CutOutImagePage(
                mainRouter = MainRouter(mainNavController),
                viewModel = hiltViewModel<CutOutImageScreenViewModel>(),
                sharedViewModel = hiltViewModel<NavigationBarSharedViewModel>(),
            )
        }

        composableHorizontalSlide<Page.SavedImageScreen> {
            SavedImagePage(
                mainRouter = MainRouter(mainNavController),
                viewModel = hiltViewModel<SavedImageScreenViewModel>(),
                sharedViewModel = hiltViewModel<NavigationBarSharedViewModel>(),
            )
        }
    }
}