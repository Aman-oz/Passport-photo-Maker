package com.ots.aipassportphotomaker.presentation.ui.createid

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.home.HomeCardItem
import com.ots.aipassportphotomaker.presentation.ui.home.HomeScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.home.HomeScreenUiState
import com.ots.aipassportphotomaker.presentation.ui.home.mainItems
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

@Composable
fun PhotoIDPage(
    mainRouter: MainRouter,
    viewModel: PhotoIDScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "HomePage"
    val uiState by viewModel.uiState.collectAsState()
    val lazyGridState = rememberLazyGridState()

    viewModel.navigationState.collectAsEffect { navigationState ->
        Log.d(TAG, "HomePage: Navigation State: $navigationState")
        when (navigationState) {
            is PhotoIDScreenNavigationState.PhotoID -> mainRouter.navigateToItemDetailScreen(navigationState.name)
            is PhotoIDScreenNavigationState.PhotoIDDetails -> mainRouter.navigateToPhotoIDDetailScreen(navigationState.name)
        }
    }
    viewModel.refreshListState.collectAsEffect {
//        moviesPaging.refresh()
    }

    sharedViewModel.bottomItem.collectAsEffect {
        // log the item that was clicked
        Log.d(TAG, "HomePage: Clicked on item: ${it.page}")
        if (it.page == Page.Home) {
            lazyGridState.animateScrollToItem(0)
        }
    }

    PhotoIDScreen(
        uiState = uiState,
        lazyGridState = lazyGridState,
        onItemClick = { name ->
            viewModel.onItemClick(name)
        }
    )
}

@Composable
private fun PhotoIDScreen(
    uiState: PhotoIDScreenUiState,
    lazyGridState: LazyGridState,
    onItemClick: (name: String) -> Unit
) {
    Surface {
        if (uiState.showLoading) {
            LoaderFullScreen()
        } else {
            LazyVerticalGrid(
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
            }
        }
    }
}
@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun HomeScreenPreview() {
    val uiState = PhotoIDScreenUiState(showLoading = false)
    val lazyGridState = LazyGridState()
    PreviewContainer {
        PhotoIDScreen(uiState, lazyGridState) { name ->
            // Handle item click
        }
    }
}