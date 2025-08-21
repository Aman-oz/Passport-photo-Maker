package com.ots.aipassportphotomaker.presentation.ui.createid

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import kotlinx.coroutines.flow.flowOf

// Created by amanullah on 21/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun PhotoIDDetailPage(
    mainRouter: MainRouter,
    viewModel: PhotoIDDetailScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "PhotoIDDetailPage"

    val documentsPaging = viewModel.documents.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    Logger.d(TAG, "PhotoIDDetailPage: UI State: $uiState")
    Logger.d(TAG, "PhotoIDDetailPage: Documents Paging: ${documentsPaging.itemCount} items loaded")
    // val pullToRefreshState = rememberPullRefreshState(uiState.showLoading, { viewModel.onRefresh() })
    val lazyGridState = rememberLazyGridState()


    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "PhotoIDDetailPage: Navigation State: $navigationState")
        when (navigationState) {
            is PhotoIDDetailScreenNavigationState.SelectPhotoScreen -> mainRouter.navigateToSelectPhotoScreen(navigationState.documentId)
        }
    }

    viewModel.refreshListState.collectAsEffect {
        documentsPaging.refresh()
    }

    sharedViewModel.bottomItem.collectAsEffect {
        Log.d(TAG, "HomePage: Clicked on item: ${it.page}")
        if (it.page == Page.Home) {
            lazyGridState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(key1 = documentsPaging.loadState) {
        viewModel.onLoadStateUpdate(documentsPaging.loadState)
    }

    PhotoIDDetailScreen(
        documents = documentsPaging,
        uiState = uiState,
        lazyGridState = lazyGridState,
        onDocumentClick = viewModel::onDocumentClicked
    )
}

@Composable
private fun PhotoIDDetailScreen(
    documents: LazyPagingItems<DocumentListItem>,
    uiState: PhotoIDDetailScreenUiState,
    lazyGridState: LazyGridState,
    onDocumentClick: (documentId: Int) -> Unit,
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
) {
    Surface {
        Column {
            CommonTopBar(
                title = uiState.title.ifEmpty { "Document Details" },
                onBackClick = onBackClick,
                onGetProClick = onGetProClick
            )

            if (uiState.showLoading) {
                LoaderFullScreen()
            } else {
                DocumentDetailList(
                    documents,
                    onDocumentClick,
                    lazyGridState = lazyGridState,
                )
            }
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun PhotoIDDetailScreenPreview() {
    val documents = flowOf(
        PagingData.from(
            listOf<DocumentListItem>(
                DocumentListItem.Document(
                    id = 2,
                    name = "Australian Passport",
                    size = "50.0 x 70.0",
                    unit = "mm",
                    pixels = "1181x1653 px",
                    resolution = "300 dpi",
                    image = "https://lh3.googleusercontent.com/d/16O1TVcECSP7E36BBpEkJDAGIQ4lxxMgV",
                    type = "Passport"
                ),
                DocumentListItem.Document(
                    id = 2,
                    name = "Canada Passport",
                    size = "50.0 x 70.0",
                    unit = "mm",
                    pixels = "1181x1653 px",
                    resolution = "300 dpi",
                    image = "https://i.stack.imgur.com/lDFzt.jpg",
                    type = "Passport"
                ),
                DocumentListItem.Document(
                    id = 2,
                    name = "Canada Passport",
                    size = "50.0 x 70.0",
                    unit = "mm",
                    pixels = "1181x1653 px",
                    resolution = "300 dpi",
                    image = "https://i.stack.imgur.com/lDFzt.jpg",
                    type = "Passport"
                ),
                DocumentListItem.Document(
                    id = 2,
                    name = "Canada Passport",
                    size = "50.0 x 70.0",
                    unit = "mm",
                    pixels = "1181x1653 px",
                    resolution = "300 dpi",
                    image = "https://i.stack.imgur.com/lDFzt.jpg",
                    type = "Passport"
                ),
                DocumentListItem.Document(
                    id = 2,
                    name = "Canada Passport",
                    size = "50.0 x 70.0",
                    unit = "mm",
                    pixels = "1181x1653 px",
                    resolution = "300 dpi",
                    image = "https://i.stack.imgur.com/lDFzt.jpg",
                    type = "Passport"
                ),
                DocumentListItem.Document(
                    id = 2,
                    name = "Canada Passport",
                    size = "50.0 x 70.0",
                    unit = "mm",
                    pixels = "1181x1653 px",
                    resolution = "300 dpi",
                    image = "https://i.stack.imgur.com/lDFzt.jpg",
                    type = "Passport"
                ),
                DocumentListItem.Document(
                    id = 2,
                    name = "Canada Passport",
                    size = "50.0 x 70.0",
                    unit = "mm",
                    pixels = "1181x1653 px",
                    resolution = "300 dpi",
                    image = "https://i.stack.imgur.com/lDFzt.jpg",
                    type = "Passport"
                ),
                DocumentListItem.Document(
                    id = 2,
                    name = "Canada Passport",
                    size = "50.0 x 70.0",
                    unit = "mm",
                    pixels = "1181x1653 px",
                    resolution = "300 dpi",
                    image = "https://i.stack.imgur.com/lDFzt.jpg",
                    type = "Passport"
                ),
            )
        )
    ).collectAsLazyPagingItems()
    PreviewContainer {
        PhotoIDDetailScreen(
            documents = documents,
            uiState = PhotoIDDetailScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            lazyGridState = rememberLazyGridState(),
            onDocumentClick = {}
        )
    }
}