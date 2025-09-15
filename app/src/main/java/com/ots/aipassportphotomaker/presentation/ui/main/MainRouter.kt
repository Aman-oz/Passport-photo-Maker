package com.ots.aipassportphotomaker.presentation.ui.main

import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.model.CustomDocumentData

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


    //To be implemented later for custom size
    /*fun navigateFromCustomHomeToDocumentInfoScreen(customData: CustomDocumentData) {
        mainNavController.navigate(Page.DocumentInfoScreenFromCustom(customData = customData))
    }*/

    fun navigateToPhotoIDDetailScreen(type: String) {
        mainNavController.navigate(Page.PhotoIDDetailScreen(type = type))
    }

    fun navigateToImageProcessingScreen(
        documentId: Int,
        imagePath: String?,
        filePath: String?,
        selectedDpi: String,
        selectedBackgroundColor: Color?,
        sourceScreen: String
    ) {
        mainNavController.navigate(
            Page.ImageProcessingScreen(
                documentId = documentId,
                imagePath = imagePath,
                filePath = filePath,
                selectedDpi = selectedDpi,
                selectedBackgroundColor = selectedBackgroundColor?.toString(),
                sourceScreen = sourceScreen
            )
        )
    }

    fun navigateToEditImageScreen(
        documentId: Int,
        imageUrl: String?,
        selectedBackgroundColor: Color?,
        editPosition: Int = 0,
        selectedDpi: String,
        sourceScreen: String
    ) {
        mainNavController.navigate(
            Page.EditImageScreen(
                documentId = documentId,
                imageUrl = imageUrl,
                selectedBackgroundColor = selectedBackgroundColor?.toString(),
                editPosition = editPosition,
                selectedDpi = selectedDpi,
                sourceScreen = sourceScreen
            )
        ) {

            popUpTo(Page.ImageProcessingScreen::class) {
                inclusive = true
            }
        }
    }

    fun navigateFromHomeToEditImageScreen(
        documentId: Int,
        imageUrl: String?,
        selectedBackgroundColor: Color?,
        editPosition: Int = 0,
        selectedDpi: String,
        sourceScreen: String
    ) {
        mainNavController.navigate(
            Page.EditImageScreen(
                documentId = documentId,
                imageUrl = imageUrl,
                selectedBackgroundColor = selectedBackgroundColor?.toString(),
                editPosition = editPosition,
                selectedDpi = selectedDpi,
                sourceScreen = sourceScreen
            )
        )
    }

    fun navigateToCutOutScreen(
        documentId: Int,
        imageUrl: String?,
        selectedBackgroundColor: Color?,
        sourceScreen: String
    ) {
        mainNavController.navigate(
            Page.CutOutImageScreen(
                documentId = documentId,
                imageUrl = imageUrl,
                selectedBackgroundColor = selectedBackgroundColor?.toString(),
                sourceScreen
            )
        )
    }

    fun navigateFromHomeToCutOutScreen(
        imageUrl: String?,
        sourceScreen: String
    ) {
        mainNavController.navigate(
            Page.CutOutImageScreen(
                documentId = 0,
                imageUrl = imageUrl,
                selectedBackgroundColor = Color.White.toString(),
                sourceScreen
            )
        )
    }

    fun navigateToSavedImageScreen(
        documentId: Int,
        imagePath: String?,
        selectedDpi: String,
        sourceScreen: String
    ) {
        mainNavController.navigate(
            Page.SavedImageScreen(
                documentId = documentId,
                imagePath = imagePath,
                selectedDpi = selectedDpi,
                sourceScreen = sourceScreen
            )
        ) {
            // Clear entire processing flow
            popUpTo(Page.DocumentInfoScreen::class) {
                inclusive = false
            }
        }
    }

    fun navigateFromCutoutToSavedImageScreen(
        documentId: Int,
        imagePath: String?,
        sourceScreen: String
    ) {
        mainNavController.navigate(
            Page.SavedImageScreen(
                documentId = documentId,
                imagePath = imagePath,
                selectedDpi = "300",
                sourceScreen = sourceScreen
            )
        ) {
            // Clear entire processing flow
            popUpTo(Page.CutOutImageScreen::class) {
                inclusive = true
            }
        }
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