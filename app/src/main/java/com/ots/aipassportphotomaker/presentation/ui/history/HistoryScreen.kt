package com.ots.aipassportphotomaker.presentation.ui.history

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.model.dbmodels.CreatedImageEntity
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.EmptyStateIcon
import com.ots.aipassportphotomaker.presentation.ui.components.EmptyStateView
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.home.HomeCardItem
import com.ots.aipassportphotomaker.presentation.ui.home.mainItems
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom100
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HistoryPage(
    mainRouter: MainRouter,
    viewModel: HistoryScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "HomePage"
    val uiState by viewModel.uiState.collectAsState()
    val lazyGridState = rememberLazyGridState()
    val types = listOf("All", "Passport", "Visa", "Standard", "National ID", "Driver's License", "Resident Card", "Profile")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { types.size })
    val coroutineScope = rememberCoroutineScope()

    val documents by viewModel.documents.collectAsState()

    viewModel.navigationState.collectAsEffect { navigationState ->
        Log.d(TAG, "HomePage: Navigation State: $navigationState")
        when (navigationState) {
            is HistoryScreenNavigationState.DocumentInfoScreen -> mainRouter.navigateToDocumentInfoScreen(
                documentId = navigationState.documentId,
                imagePath = navigationState.imagePath
            )
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

    // Debounced tab/pager change handling
    LaunchedEffect(pagerState.currentPage) {
        val job = coroutineScope.launch {
            delay(250) // Debounce delay of 300ms
            val type = types[pagerState.currentPage]
            viewModel.getHistoryByType(type) // Fetch data based on current page type
        }
        awaitCancellation() // Keep the job alive until cancellation
        job.cancel() // Cancel previous job on new launch
    }

    HistoryScreen(
        uiState = uiState,
        pagerState = pagerState,
        documents = documents,
        lazyGridState = lazyGridState,
        onTabSelected = { tab ->
            val index = types.indexOf(tab)
            if (index != -1) {
                viewModel.getHistoryByType(tab) // Fetch data on tab click
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index) // Sync pager
                }
            }

        },
        onItemClick = { name ->
            viewModel.onItemClick(name)
        }
    )
}

@Composable
private fun HistoryScreen(
    uiState: HistoryScreenUiState,
    pagerState: PagerState,
    lazyGridState: LazyGridState,
    documents: List<CreatedImageEntity>,
    onTabSelected: (String) -> Unit = {},
    onItemClick: (name: String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {
        if (uiState.showLoading) {
            LoaderFullScreen()
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top: Scrollable Types
                val types = listOf("All", "Passport", "Visa", "Standard", "National ID", "Driver's License", "Resident Card", "Profile")
                val coroutineScope = rememberCoroutineScope()
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(colors.background),
                    edgePadding = 0.dp,
                    containerColor = colors.background,
                    indicator = { tabPositions -> // No default indicator, handled by tab background
                        Box {}
                    },
                    divider = { /* Disable divider */ Box {} }
                    /*indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = colors.primary
                        )
                    }*/
                ) {
                    types.forEachIndexed { index, type ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                onTabSelected(type) // Trigger data fetch on tab click
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            modifier = Modifier
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .background(
                                    color = if (pagerState.currentPage == index) colors.primary else colors.custom400, // Custom background
                                    shape = RoundedCornerShape(8.dp) // 8dp rounded corners
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .background(
                                        color = if (pagerState.currentPage == index) colors.primary else colors.custom400,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    color = if (pagerState.currentPage == index) colors.onPrimary else colors.onCustom400, // Contrast text color
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Medium else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Bottom: HorizontalPager with Lists
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) { page ->
                    val currentType = types[page]
//                    onTabSelected(currentType)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.background),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(documents.size) { index ->
                            val item = documents[index]
                            DocumentItem(
                                item = item,
                                onClick = { onItemClick(item.name) }
                            )
                        }

                        if (documents.isEmpty()) {
                            item {
                                Text(
                                    text = stringResource(id = R.string.no_history_found_subtitle),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.onSurfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentItem(
    item: CreatedImageEntity, // Updated to use CreatedImageEntity
    onClick: (CreatedImageEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick(item) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.custom400),
        border = BorderStroke(1.dp, colors.custom100 )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Load image from path
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.createdImage) // Use the image path
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = when (item.type) {
                    "Passports" -> R.drawable.passport_united_state
                    "Visas" -> R.drawable.passport_united_state
                    "Standards" -> R.drawable.passport_united_state
                    else -> R.drawable.passport_united_state
                }),
                error = painterResource(id = when (item.type) {
                    "Passports" -> R.drawable.passport_united_state
                    "Visas" -> R.drawable.passport_united_state
                    "Standards" -> R.drawable.passport_united_state
                    else -> R.drawable.passport_united_state
                })
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onCustom400
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${item.documentSize} -- ${item.unit}", // Combine size and unit
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${item.resolution} DPI" ?: "N/A", // Using resolution as a placeholder
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurfaceVariant
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = "View Details",
                tint = colors.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DocumentItemPreview() {
    PreviewContainer {
        DocumentItem(
            item = CreatedImageEntity(
                id = 1,
                name = "US Passport Photo",
                type = "Passport",
                documentImage = "",
                createdImage = "", // Path to the final saved image
                documentSize = "2x2",
                unit = "inches",
                pixel = "600x600",
                resolution = "300 DPI"
            ),
            onClick = {}
        )
    }
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun HomeScreenPreview() {
    val uiState = HistoryScreenUiState(showLoading = false)
    val lazyGridState = LazyGridState()
    PreviewContainer {
        HistoryScreen(
            uiState = uiState,
            pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 }),
            documents = emptyList(),
            lazyGridState = lazyGridState,
            onItemClick = {}
        )
    }
}