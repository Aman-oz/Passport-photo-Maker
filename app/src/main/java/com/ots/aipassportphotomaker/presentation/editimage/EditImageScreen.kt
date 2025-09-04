package com.ots.aipassportphotomaker.presentation.editimage

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.domain.model.ProcessingStage
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.ImageEditorView
import com.ots.aipassportphotomaker.presentation.ui.components.ImageWithLottieScan
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.processimage.ImageProcessingScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.processimage.ImageProcessingScreenUiState
import com.ots.aipassportphotomaker.presentation.ui.processimage.ImageProcessingScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import kotlinx.coroutines.delay

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun EditImagePage(
    mainRouter: MainRouter,
    viewModel: EditImageScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "ImageProcessingPage"

    val uiState by viewModel.uiState.collectAsState()

    val imageUrl = viewModel.imageUrl
    val selectedColor = viewModel.selectedColor

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "PhotoIDDetailPage: Navigation State: $navigationState")
       /* when (navigationState) {
            is EditImageScreenNavigationState.CutOutScreen -> mainRouter.navigateToCutOutScreen(
                documentId = navigationState.documentId,
                imageUrl = imageUrl,
                selectedBackgroundColor = selectedColor
            )
        }*/
    }

    ImageProcessingScreen(
        uiState = uiState,
        selectedColor = selectedColor,
        onBackClick = { mainRouter.goBack() },
        onGetProClick = { }
    )
}

@Composable
private fun ImageProcessingScreen(
    uiState: EditImageScreenUiState,
    selectedColor: String? = null,
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
) {

    Surface {

        val context = LocalContext.current
        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage
        val backgroundColor = uiState.selectedColor
        val imageUrl = uiState.imageUrl

        if (errorMessage != null) Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

        Column(
            modifier = Modifier
                .background(colors.background)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {

                        })
                }
        ) {
            CommonTopBar(
                title = "Edit image",
                onBackClick = {
                    onBackClick.invoke()

                },
                onGetProClick = {
                    onGetProClick.invoke()

                }
            )

            if (isLoading) {
                LoaderFullScreen()
            } else {
                Column(
                    modifier = Modifier
                        .background(colors.background)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {

                                })
                        }
                ) {

                    ImageEditorView(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        imagePath = imageUrl,
                        onTap = {}
                    )

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            text = "Final Image",
                            color = colors.onBackground,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            text = "Edit",
                            color = colors.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                }
            }
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun ImageProcessingScreenPreview() {

    PreviewContainer {
        ImageProcessingScreen(
            uiState = EditImageScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            onBackClick = {},
            onGetProClick = {}
        )
    }
}