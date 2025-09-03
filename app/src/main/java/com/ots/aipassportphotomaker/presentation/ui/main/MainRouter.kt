package com.ots.aipassportphotomaker.presentation.ui.main

import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import kotlin.toString

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class MainRouter(
    private val mainNavController: NavHostController
) {

    fun goBack() {
        mainNavController.popBackStack()
    }

    fun navigateToItemDetailScreen(name: String) {
        mainNavController.navigate(Page.ItemDetailScreen(name))
    }

    fun navigateToPhotoIDScreen2() {
        mainNavController.navigate(Page.PhotoID2)
    }

    fun navigateToDocumentInfoScreen(documentId: Int) {
        mainNavController.navigate(Page.DocumentInfoScreen(documentId = documentId))
    }

    fun navigateToPhotoIDDetailScreen(type: String) {
        mainNavController.navigate(Page.PhotoIDDetailScreen(type = type))
    }

    fun navigateToImageProcessingScreen(
        documentId: Int,
        imagePath: String?,
        selectedDpi: String,
        selectedBackgroundColor: Color?
    ) {
        mainNavController.navigate(Page.ImageProcessingScreen(
            documentId = documentId,
            imagePath = imagePath,
            selectedDpi = selectedDpi,
            selectedBackgroundColor = selectedBackgroundColor?.toString()
        ))
    }

    fun navigateToSelectPhotoScreen(documentId: Int) {
        mainNavController.navigate(Page.SelectPhotoScreen(documentId))
    }

    fun navigateToHistoryDetailScreen(name: String) {
        mainNavController.navigate(Page.HistoryDetailScreen(name))
    }

    fun navigateToPremiumScreen() {
        mainNavController.navigate(Page.Premium)
    }
}