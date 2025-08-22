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
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDScreenNavigationState.PhotoIDDetails
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import kotlinx.coroutines.flow.flowOf
import kotlin.text.ifEmpty

@Composable
fun PhotoIDPage2(
    mainRouter: MainRouter,
    viewModel: PhotoIDScreen2ViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "PhotoIDPage"

    val documentsPaging = viewModel.documents.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    Logger.d(TAG, "PhotoIDPage: UI State: $uiState")
    Logger.d(TAG, "PhotoIDPage: Documents Paging: ${documentsPaging.itemCount} items loaded")
    // val pullToRefreshState = rememberPullRefreshState(uiState.showLoading, { viewModel.onRefresh() })
    val lazyGridState = rememberLazyGridState()


    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "HomePage: Navigation State: $navigationState")
        when (navigationState) {
            is PhotoIDScreen2NavigationState.PhotoIDDetails -> mainRouter.navigateToPhotoIDDetailScreen(navigationState.type)
            is PhotoIDScreen2NavigationState.SelectPhotoScreen -> {
                Log.d(TAG, "HomePage: Navigate to Select Photo Screen")
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
        Log.d(TAG, "HomePage: Clicked on item: ${it.page}")
        if (it.page == Page.Home) {
            lazyGridState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(key1 = documentsPaging.loadState) {
        viewModel.onLoadStateUpdate(documentsPaging.loadState)
    }

    PhotoIDScreen2(
        documents = documentsPaging,
        uiState = uiState,
        lazyGridState = lazyGridState,
        onDocumentClick = viewModel::onDocumentClicked,
        onSeeAllClick = viewModel::onSeeAllClicked
    )
}

@Composable
private fun PhotoIDScreen2(
    documents: LazyPagingItems<DocumentListItem>,
    uiState: PhotoIDScreen2UiState,
    lazyGridState: LazyGridState,
    onDocumentClick: (documentId: Int) -> Unit,
    onSeeAllClick: (type: String) -> Unit,
    onBackClick: () -> Unit = {},
    onGetProClick: () -> Unit = {}
) {
    Surface {
        Column {
            CommonTopBar(
                title = "Photo ID",
                onBackClick = onBackClick,
                onGetProClick = onGetProClick
            )

            if (uiState.showLoading) {
                LoaderFullScreen()
            } else {
                DocumentList(
                    documents,
                    onDocumentClick,
                    onSeeAllClick
                )
            }
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun PhotoIDScreen2Preview() {
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
        PhotoIDScreen2(
            documents = documents,
            uiState = PhotoIDScreen2UiState(
                showLoading = false,
                errorMessage = null,
            ),
            lazyGridState = rememberLazyGridState(),
            onDocumentClick = {},
            onSeeAllClick = {}
        )
    }
}