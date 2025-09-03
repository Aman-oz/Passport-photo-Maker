package com.ots.aipassportphotomaker.presentation.ui.processimage

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.EmptyStateIcon
import com.ots.aipassportphotomaker.presentation.ui.components.EmptyStateView
import com.ots.aipassportphotomaker.presentation.ui.components.ImageWithLottieScan
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.SearchView
import com.ots.aipassportphotomaker.presentation.ui.createid.DocumentDetailList
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDDetailScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDDetailScreenUiState
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDDetailScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlin.compareTo
import kotlin.rem
import kotlin.text.ifEmpty

// Created by amanullah on 27/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun ImageProcessingPage(
    mainRouter: MainRouter,
    viewModel: ImageProcessingScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "ImageProcessingPage"

    val uiState by viewModel.uiState.collectAsState()

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "PhotoIDDetailPage: Navigation State: $navigationState")
        when (navigationState) {
            is ImageProcessingScreenNavigationState.EditImageScreen -> mainRouter.navigateToDocumentInfoScreen(navigationState.documentId)
        }
    }

    ImageProcessingScreen(
        uiState = uiState,
        viewModel = viewModel,
        onBackClick = { mainRouter.goBack() },
        onGetProClick = { }
    )
}

@Composable
private fun ImageProcessingScreen(
    uiState: ImageProcessingScreenUiState,
    viewModel: ImageProcessingScreenViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
) {

    Surface {

        val context = LocalContext.current
        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage
        val documentImage = uiState.documentImage

        if (errorMessage != null) Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

        val processingMessages = listOf(
            "🔄 Uploading Photo...",
            "🔮 AI Magic in progress… just a moment!",
            "🎨 Crafting the best fit for your document!",
            "☺ Head centered",
            "💫 Removing background...",
            "✅ Process completed successfully"
        )

        var messageIndex by remember { mutableStateOf(-1) }

        LaunchedEffect(isLoading) {
            if (isLoading) {
                messageIndex = 0
                while (isLoading) {
                    delay(2500) // Change message every 2.5 seconds
                    messageIndex = (messageIndex + 1) % processingMessages.size
                }
            } else {
                messageIndex = -1 // Reset when not loading
            }
        }

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
                            text = if (messageIndex >= 0) processingMessages[messageIndex] else "",
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
            uiState = ImageProcessingScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            onBackClick = {},
            onGetProClick = {}
        )
    }
}