package com.ots.aipassportphotomaker.presentation.ui.processimage

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.domain.model.ProcessingStage
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.ImageWithLottieScan
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import kotlinx.coroutines.delay

// Created by amanullah on 27/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@SuppressLint("ContextCastToActivity")
@Composable
fun ImageProcessingPage(
    mainRouter: MainRouter,
    viewModel: ImageProcessingScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "ImageProcessingPage"

    val uiState by viewModel.uiState.collectAsState()

    // Collect the processing stage
    val processingStage by viewModel.processingStage.collectAsState()
    val isPortrait = viewModel.isPortrait
    val imagePath = viewModel.imagePath
    val selectedColor = viewModel.selectedColor

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "PhotoIDDetailPage: Navigation State: $navigationState")
        when (navigationState) {
            is ImageProcessingScreenNavigationState.EditImageScreen -> mainRouter.navigateToEditImageScreen(
                documentId = navigationState.documentId,
                imageUrl = uiState.finalImageUrl,
                selectedBackgroundColor = uiState.selectedColor
            )
        }
    }

    ImageProcessingScreen(
        uiState = uiState,
        processingStage = processingStage,
        isPortrait = isPortrait,
        selectedColor = selectedColor,
        imagePath = imagePath,
        onBackClick = { mainRouter.goBack() },
        onGetProClick = { }
    )
}

@Composable
private fun ImageProcessingScreen(
    uiState: ImageProcessingScreenUiState,
    processingStage: ProcessingStage = ProcessingStage.NONE,
    isPortrait: Boolean = true,
    selectedColor: String? = null,
    imagePath: String? = null,
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
) {

    Surface {

        val context = LocalContext.current
        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage
        val documentImage = uiState.documentImage
        val backgroundColor = uiState.selectedColor

        if (errorMessage != null) Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

        // Get appropriate message based on processing stage
        val currentMessage = when (processingStage) {
            ProcessingStage.UPLOADING -> "🔄 Uploading Photo..."
            ProcessingStage.PROCESSING -> {
                // Alternate between these messages during processing
                val processingMessages = listOf(
                    "🔮 AI Magic in progress… just a moment!",
                    "🎨 Crafting the best fit for your document!",
                    "☺ Head centered"
                )
                val messageIndex by produceState(initialValue = 0) {
                    while (processingStage == ProcessingStage.PROCESSING) {
                        delay(3000)
                        value = (value + 1) % processingMessages.size
                    }
                }
                processingMessages[messageIndex]
            }
            ProcessingStage.CROPPING_IMAGE -> "💫 Cropping image..."
            ProcessingStage.COMPLETED -> {
                "✅ Process completed successfully"

            }
            ProcessingStage.NONE -> ""
            ProcessingStage.NO_NETWORK_AVAILABLE -> "❌ No network connection"
            ProcessingStage.ERROR -> "❌ Something went wrong"
            ProcessingStage.DOWNLOADING -> "⬇️ Applying Changes..."
            ProcessingStage.SAVING_IMAGE -> "💾 Saving image..."
            ProcessingStage.BACKGROUND_REMOVAL -> "🖼️ Removing background..."
        }

        Column(
            modifier = Modifier
                .background(colors.background)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {

                    })
                }
        ) {
            CommonTopBar(
                title = "Processing",
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

                    ImageWithLottieScan(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        imagePath = imagePath,
//                        isPortrait = isPortrait,
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
                            text = "Processing Image",
                            color = colors.onBackground,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            text = currentMessage,
                            color = colors.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                }

                Box() {
                    Box(
                        modifier = Modifier
                            .offset()
                    )
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
            uiState = ImageProcessingScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            onBackClick = {},
            onGetProClick = {}
        )
    }
}