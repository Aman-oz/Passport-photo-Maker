package com.ots.aipassportphotomaker.presentation.ui.createid

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
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
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.EmptyStateView
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.SearchView
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDScreenNavigationState.PhotoIDDetails
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import kotlinx.coroutines.flow.flowOf
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.presentation.ui.components.EmptyStateIcon

@Composable
fun PhotoIDPage(
    mainRouter: MainRouter,
    viewModel: PhotoIDScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "PhotoIDPage"

    val documentsPaging = viewModel.documents.collectAsLazyPagingItems()
    val documentsSearchedPaging = viewModel.searchedDocuments.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()

    Logger.d(TAG, "PhotoIDPage: UI State: $uiState")
    Logger.d(TAG, "PhotoIDPage: Documents Paging: ${documentsPaging.itemCount} items loaded")
    Logger.d(TAG, "PhotoIDPage: Searched Documents Paging: ${documentsSearchedPaging.itemCount} items loaded")
    // val pullToRefreshState = rememberPullRefreshState(uiState.showLoading, { viewModel.onRefresh() })
    val lazyGridState = rememberLazyGridState()


    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "PhotoIDPage: Navigation State: $navigationState")
        when (navigationState) {
            is PhotoIDDetails -> mainRouter.navigateToPhotoIDDetailScreen(navigationState.type, "")
            is PhotoIDScreenNavigationState.DocumentInfoScreen -> {
                mainRouter.navigateToDocumentInfoScreen(navigationState.documentId, "")
            }
            is PhotoIDScreenNavigationState.SelectPhotoScreen -> {
                Log.d(TAG, "PhotoIDPage: Navigate to Select Photo Screen")
                mainRouter.navigateToSelectPhotoScreen(
                    documentId = navigationState.documentId
                )
            }
        }
    }

    viewModel.refreshListState.collectAsEffect {
        documentsPaging.refresh()
    }

    sharedViewModel.bottomItem.collectAsEffect {
        Log.d(TAG, "PhotoIDPage: Clicked on item: ${it.page}")
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

    PhotoIDScreen(
        documents = documentsToShow,
        uiState = uiState,
        lazyGridState = lazyGridState,
        onDocumentClick = viewModel::onDocumentClicked,
        onQueryChange = viewModel::onSearch,
        onSeeAllClick = viewModel::onSeeAllClicked
    )
}

@Composable
private fun PhotoIDScreen(
    documents: LazyPagingItems<DocumentListItem>,
    uiState: PhotoIDScreenUiState,
    lazyGridState: LazyGridState,
    onDocumentClick: (documentId: Int) -> Unit,
    onQueryChange: (query: String) -> Unit,
    onSeeAllClick: (type: String) -> Unit
) {

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    Surface(
        modifier = Modifier
    ) {

        val context = LocalContext.current
        val isLoading = uiState.showLoading
        var showNoDocumentsFound = uiState.showNoDocumentsFound
        val errorMessage = uiState.errorMessage
        var query: String by remember { mutableStateOf("") }

        showNoDocumentsFound = !uiState.showDefaultState && documents.itemCount == 0

        if (errorMessage != null) Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

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
                SearchView(
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
                        subtitle = stringResource(id = R.string.no_search_results_subtitle, query),
                        titleTextSize = 20.sp,
                        subtitleTextSize = 16.sp,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.padding(top = 80.dp, start = 24.dp, end = 24.dp)
                    )
                } else {
                    DocumentList(
                        documents,
                        onDocumentClick,
                        onSeeAllClick,
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

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun PhotoIDScreenPreview() {
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
        PhotoIDScreen(
            documents = documents,
            uiState = PhotoIDScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            lazyGridState = rememberLazyGridState(),
            onDocumentClick = {},
            onQueryChange = {},
            onSeeAllClick = {}
        )
    }
}