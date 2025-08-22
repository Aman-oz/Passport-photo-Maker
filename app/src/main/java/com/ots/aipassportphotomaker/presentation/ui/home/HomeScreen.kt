package com.ots.aipassportphotomaker.presentation.ui.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.ChooseOrPickImage
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

@Composable
fun HomePage(
    mainRouter: MainRouter,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "HomePage"
    val uiState by viewModel.uiState.collectAsState()
    val lazyGridState = rememberLazyGridState()

    viewModel.navigationState.collectAsEffect { navigationState ->
        Logger.d(TAG, "HomePage: Navigation State: $navigationState")
        when (navigationState) {
            is HomeScreenNavigationState.PhotoID ->  { navigationState
                mainRouter.navigateToPhotoIDScreen2()
            }
        }
    }
    viewModel.refreshListState.collectAsEffect {
//        moviesPaging.refresh()
    }

    sharedViewModel.bottomItem.collectAsEffect {
        // log the item that was clicked
        Logger.d(TAG, "HomePage: Clicked on item: ${it.page}")
        if (it.page == Page.Home) {
            lazyGridState.animateScrollToItem(0)
        }
    }

    HomeScreen(
        uiState = uiState,
        lazyGridState = lazyGridState,
        onItemClick = { name ->
            viewModel.onItemClick(name)
        }
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeScreenUiState,
    lazyGridState: LazyGridState,
    onItemClick: (name: String) -> Unit
) {
    Surface(
        modifier = Modifier
            .background(colors.background)
    ) {
        if (uiState.showLoading) {
            LoaderFullScreen()
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(colors.background),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    modifier = Modifier
                        .weight(1f)
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
                            onClick = {
                                onItemClick(item.name)
                            }
                        )
                    }
                }

                ChooseOrPickImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    context = LocalContext.current,
                    onCameraImage = {
                        Logger.d("HomeScreen", "Camera image clicked")
                    },
                    onChooseImage = {
                        Logger.d("HomeScreen", "Choose image clicked")
                    },
                    isChooseEnabled = true,
                    isPickEnabled = true
                )
            }
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun HomeScreenPreview() {
    val uiState = HomeScreenUiState(showLoading = false)
    val lazyGridState = LazyGridState()
    PreviewContainer {
        HomeScreen(uiState, lazyGridState) { name ->
            // Handle item click
        }
    }
}
