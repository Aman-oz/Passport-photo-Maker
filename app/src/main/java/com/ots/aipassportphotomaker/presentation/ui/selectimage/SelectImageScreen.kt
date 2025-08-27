package com.ots.aipassportphotomaker.presentation.ui.selectimage

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
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
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.EmptyStateIcon
import com.ots.aipassportphotomaker.presentation.ui.components.EmptyStateView
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.SearchView
import com.ots.aipassportphotomaker.presentation.ui.createid.DocumentDetailList
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDDetailScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDDetailScreenUiState
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDDetailScreenViewModel
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import kotlinx.coroutines.flow.flowOf
import kotlin.text.ifEmpty

// Created by amanullah on 27/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun SelectImagePage(
    mainRouter: MainRouter,
    viewModel: PhotoIDDetailScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "PhotoIDDetailPage"

    val documentsPaging = viewModel.documents.collectAsLazyPagingItems()
    val documentsSearchedPaging = viewModel.searchedDocuments.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()

    val lazyGridState = rememberLazyGridState()

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "PhotoIDDetailPage: Navigation State: $navigationState")
        when (navigationState) {
            is PhotoIDDetailScreenNavigationState.DocumentInfoScreen -> mainRouter.navigateToDocumentInfoScreen(navigationState.documentId)
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

    // Choose which list to show
    val showSearchResults = !uiState.showDefaultState
    val documentsToShow = if (showSearchResults) documentsSearchedPaging else documentsPaging

    SelectImageScreen(
        documents = documentsToShow,
        uiState = uiState,
        lazyGridState = lazyGridState,
        onQueryChange = viewModel::onSearch,
        onDocumentClick = viewModel::onDocumentClicked,
        onBackClick = { mainRouter.goBack() },
    )
}

@Composable
private fun SelectImageScreen(
    documents: LazyPagingItems<DocumentListItem>,
    uiState: PhotoIDDetailScreenUiState,
    lazyGridState: LazyGridState,
    onDocumentClick: (documentId: Int) -> Unit,
    onQueryChange: (query: String) -> Unit,
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {},
) {

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface {

        val context = LocalContext.current
        val isLoading = uiState.showLoading
        var showNoDocumentsFound = uiState.showNoDocumentsFound
        val errorMessage = uiState.errorMessage
        var query: String by remember { mutableStateOf("") }

        showNoDocumentsFound = !uiState.showDefaultState && documents.itemCount == 0
        if (errorMessage != null) Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

        Column(
            modifier = Modifier
                .background(colors.background)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    })
                }
        ) {
            CommonTopBar(
                title = uiState.type.ifEmpty { uiState.type },
                onBackClick = {
                    onBackClick.invoke()

                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
                onGetProClick = {
                    onGetProClick.invoke()

                    focusManager.clearFocus()
                    keyboardController?.hide()
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
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            })
                        }
                ) {
                    SearchView (
                        onQueryChange = {
                            query = it
                            onQueryChange(it)
                        },
                        onCloseClick = {
                            query = ""
                            onQueryChange("")
                        }
                    )
                    if (showNoDocumentsFound) {
                        EmptyStateView(
                            title = stringResource(id = R.string.no_search_results_title),
                            icon = EmptyStateIcon(
                                iconRes = R.drawable.history_empty,
                                size = 100.dp,
                                spacing = 12.dp
                            ),
                            subtitle = stringResource(
                                id = R.string.no_search_results_subtitle,
                                query
                            ),
                            titleTextSize = 20.sp,
                            subtitleTextSize = 16.sp,
                            verticalArrangement = Arrangement.Top,
                            modifier = Modifier.padding(top = 80.dp, start = 24.dp, end = 24.dp)
                        )
                    } else {
                        DocumentDetailList(
                            documents,
                            onDocumentClick,
                            lazyGridState = lazyGridState,
                            showSeparator = false,
                            onScrollStarted = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        )
                    }
                }
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
        SelectImageScreen(
            documents = documents,
            uiState = PhotoIDDetailScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            lazyGridState = rememberLazyGridState(),
            onDocumentClick = {},
            onQueryChange = {}
        )
    }
}