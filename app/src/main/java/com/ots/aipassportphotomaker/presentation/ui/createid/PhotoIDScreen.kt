package com.ots.aipassportphotomaker.presentation.ui.createid

import android.util.Log
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDScreenNavigationState.PhotoIDDetails
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import kotlinx.coroutines.flow.flowOf

@Composable
fun PhotoIDPage(
    mainRouter: MainRouter,
    viewModel: PhotoIDScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "PhotoIDPage"

    val documentsPaging = viewModel.documents.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
   // val pullToRefreshState = rememberPullRefreshState(uiState.showLoading, { viewModel.onRefresh() })
    val lazyGridState = rememberLazyGridState()


    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "HomePage: Navigation State: $navigationState")
        when (navigationState) {
            is PhotoIDDetails -> mainRouter.navigateToPhotoIDDetailScreen(navigationState.documentId)
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

    PhotoIDScreen(
        documents = documentsPaging,
        uiState = uiState,
        lazyGridState = lazyGridState,
        onDocumentClick = viewModel::onDocumentClicked
    )
}

@Composable
private fun PhotoIDScreen(
    documents: LazyPagingItems<DocumentListItem>,
    uiState: PhotoIDScreenUiState,
    lazyGridState: LazyGridState,
    onDocumentClick: (documentId: Int) -> Unit
) {
    Surface {
        if (uiState.showLoading) {
            LoaderFullScreen()
        } else {
            DocumentList(
                documents,
                onDocumentClick,
                lazyGridState
            )
            /*LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = lazyGridState,
                modifier = Modifier
                    .background(colors.background)
            ) {
                val itemsList = mainItems
                items(itemsList.size) { index ->
                    val item = itemsList[index]
                    HomeCardItem(
                        title = item.title,
                        description = item.description,
                        backgroundColor = item.backgroundColor,
                        textColor = item.textColor,
                        backgroundImage = item.backgroundImage,
                        sparkleImage = item.sparkleImage,
                        onClick = { onItemClick(item.name) }
                    )
                }
            }*/
        }
    }
}
@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun HomeScreenPreview() {
    val documents = flowOf(
        PagingData.from(
            listOf<DocumentListItem>(
                DocumentListItem.Document(9, "", "", "","","","",""),
                DocumentListItem.Document(9, "", "", "","","","",""),
                DocumentListItem.Document(9, "", "", "","","","",""),
                DocumentListItem.Document(9, "", "", "","","","",""),
                DocumentListItem.Document(9, "", "", "","","","",""),
                DocumentListItem.Document(9, "", "", "","","","",""),
                DocumentListItem.Document(9, "", "", "","","","",""),
                DocumentListItem.Document(9, "", "", "","","","",""),
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
            onDocumentClick = {}
        )
    }
}