package com.ots.aipassportphotomaker.image_picker.view

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.SharedPrefUtils
import com.ots.aipassportphotomaker.image_picker.component.AssetImageItem
import com.ots.aipassportphotomaker.image_picker.model.AssetInfo
import com.ots.aipassportphotomaker.image_picker.model.AssetResourceType
import com.ots.aipassportphotomaker.image_picker.model.RequestType
import com.ots.aipassportphotomaker.image_picker.viewmodel.AssetViewModel
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom300
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom300
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400
import kotlinx.coroutines.launch

@Composable
internal fun AssetDisplayScreen(
    viewModel: AssetViewModel,
    navigateToDropDown: (String) -> Unit,
    onPicked: (List<AssetInfo>) -> Unit,
    onClose: (List<AssetInfo>) -> Unit,
) {
    val showTab by remember { mutableStateOf(false) }
    BackHandler {
        if (viewModel.selectedList.isNotEmpty()) {
            viewModel.clear()
        } else {
            onClose(viewModel.selectedList)
        }
    }

    Scaffold(
        modifier = Modifier.background(colors.background),
        topBar = {
            DisplayTopAppBar(
                directory = viewModel.directory,
                selectedList = viewModel.selectedList,
                navigateUp = onClose,
                navigateToDropDown = navigateToDropDown
            )
        },
        bottomBar = {
//            DisplayBottomBar(viewModel, onPicked)
        }
    ) { paddingValues ->

        // Showing only images
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .background(colors.background)
                .fillMaxSize()
        ) {

            val maxAssets = LocalAssetConfig.current.maxAssets
            AssetContent(viewModel, RequestType.IMAGE)

            if (viewModel.selectedList.isNotEmpty()) {
                SelectPhotoButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .background(Color.Transparent)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    onPicked(viewModel.selectedList)
                                    Log.d(
                                        "AssetDisplayScreen",
                                        "Settings icon tapped"
                                    )
                                }
                            )
                        },
                    maxAssets
                )
            }
        }

        //currently not showing the tab for all, videos, images. If want to show just unwrap the bellow box
        if (showTab) {
            Box(modifier = Modifier.padding(paddingValues)) {
                val tabs = listOf(TabItem.All, TabItem.Video, TabItem.Image)
                val pagerState = rememberPagerState(pageCount = tabs::size)

                Column {
                    AssetTab(tabs = tabs, pagerState = pagerState)
                    HorizontalPager(state = pagerState, userScrollEnabled = false) { page ->
                        tabs[page].screen(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DisplayTopAppBar(
    directory: String,
    selectedList: List<AssetInfo>,
    navigateUp: (List<AssetInfo>) -> Unit,
    navigateToDropDown: (String) -> Unit,
) {
    TopAppBar(
        modifier = Modifier
            .statusBarsPadding()
            .background(colors.background),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
        navigationIcon = {
            IconButton(onClick = { navigateUp(selectedList) }) {
                Icon(painter = painterResource(R.drawable.arrow_back), contentDescription = "")
            }
        },
        title = {
            Row(modifier = Modifier.clickable { navigateToDropDown(directory) }) {
                Column {
                    Text(
                        text = directory,
                        color = colors.onBackground,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = directory,
                        color = colors.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(colors.custom300)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    navigateToDropDown(directory)
                                    Log.d(
                                        "AssetDisplayScreen",
                                        "Settings icon tapped"
                                    )
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        painter = painterResource(R.drawable.chevron_down),
                        contentDescription = ""
                    )
                }
            }
        },
    )
}


@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DisplayTopAppBarPreview() {
    PreviewContainer {

        DisplayTopAppBar(
            directory = "All Photos",
            selectedList = listOf(),
            navigateUp = {},
            navigateToDropDown = {}
        )
    }

}

@Composable
fun SelectPhotoButton(modifier: Modifier = Modifier, numOfSelectedImages: Int = 1) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(colors.primary)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.button_select_photo, numOfSelectedImages),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.onPrimary,
            fontSize = 16.sp
        )
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SelectPhotoButtonPreview() {
    PreviewContainer {
        SelectPhotoButton()
    }
}

@Composable
private fun DisplayBottomBar(viewModel: AssetViewModel, onPicked: (List<AssetInfo>) -> Unit) {
    var cameraUri: Uri? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let { scope.launch { viewModel.initDirectories() } }
        } else {
            viewModel.deleteImage(cameraUri)
        }
    }

    if (viewModel.selectedList.isEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TextButton(
                onClick = {
                    cameraUri = viewModel.getUri()
                    cameraLauncher.launch(cameraUri!!)
                },
                content = {
                    Text(
                        text = stringResource(R.string.label_camera),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            )
            TextButton(
                onClick = {},
                content = { Text(text = stringResource(R.string.label_album), fontSize = 16.sp) }
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.text_select_tip),
                fontSize = 12.sp,
                color = Color.Gray
            )
            AppBarButton(
                size = viewModel.selectedList.size,
                onPicked = { onPicked(viewModel.selectedList) }
            )
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DisplayBottomBarPreview() {
    val isListEmpty by remember { mutableStateOf(true) }
    var cameraUri: Uri? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    PreviewContainer {
        if (isListEmpty) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(
                    onClick = {
                        cameraUri = "viewModel.getUri()".toUri()
                    },
                    content = {
                        Text(
                            text = stringResource(R.string.label_camera),
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                )
                TextButton(
                    onClick = {},
                    content = {
                        Text(
                            text = stringResource(R.string.label_album),
                            fontSize = 16.sp
                        )
                    }
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.text_select_tip),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                AppBarButton(
                    size = 0,
                    onPicked = { }
                )
            }
        }
    }
}

@Composable
private fun AssetTab(tabs: List<TabItem>, pagerState: PagerState) {
    val scope = rememberCoroutineScope()

    TabRow(selectedTabIndex = pagerState.currentPage, indicator = {}) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = pagerState.currentPage == index,
                text = { Text(text = stringResource(tab.resourceId)) },
                selectedContentColor = MaterialTheme.colorScheme.onSurface,
                unselectedContentColor = Color.Gray,
                onClick = { scope.launch { pagerState.animateScrollToPage(index) } }
            )
        }
    }
}

/*@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AssetTabPreview() {
    val tabs = listOf(TabItem.All, TabItem.Video, TabItem.Image)
    val pagerState = rememberPagerState(pageCount = tabs::size)
    PreviewContainer {
        AssetTab(tabs = tabs, pagerState = pagerState)
    }
}*/

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun AssetContent(viewModel: AssetViewModel, requestType: RequestType) {
    val assets = viewModel.getGroupedAssets(requestType)
    val context = LocalContext.current
    val gridCount = LocalAssetConfig.current.gridCount
    val maxAssets = LocalAssetConfig.current.maxAssets
    val errorMessage = stringResource(R.string.message_selected_exceed, maxAssets)
    val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp / gridCount)

    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val isPhotoGuideShown = remember {
        mutableStateOf(
            sharedPreferences.getBoolean(
                SharedPrefUtils.SHOW_PHOTO_GUIDE_DIALOG,
                false
            )
        )
    }

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    var cameraUri: Uri? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let { scope.launch { viewModel.initDirectories() } }
        } else {
            viewModel.deleteImage(cameraUri)
        }
    }

    // Flatten the grouped assets into a single list
    val allAssets = assets.values.flatten()

    // Define sample images
    val sampleImages = listOf(
        AssetInfo(
            id = -1L,
            uriString = "android.resource://${context.packageName}/${R.drawable.sample_image_male}",
            filepath = "",
            filename = "Sample Male",
            directory = "Samples",
            mimeType = "image/png",
            mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
            date = System.currentTimeMillis(),
            duration = 0L,
            size = 0L
        ),
        AssetInfo(
            id = -2L,
            uriString = "android.resource://${context.packageName}/${R.drawable.sample_image_female}",
            filepath = "",
            filename = "Sample Female",
            directory = "Samples",
            mimeType = "image/png",
            mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
            date = System.currentTimeMillis(),
            duration = 0L,
            size = 0L
        )
    )

    // Select the first sample image by default
    /*if (viewModel.selectedList.isEmpty()) {
        viewModel.toggleSelect(true, sampleImages[0])
    }*/

    if (allAssets.isEmpty()) {
        return Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "The corresponding resource is empty",
                textAlign = TextAlign.Center
            )
        }
    }

    LazyColumn {
        item {
            FlowRow(maxItemsInEachRow = gridCount) {
                // Add Camera item
                Box(
                    modifier = Modifier
                        .size(itemSize)
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.custom300)
                        .clickable {

                            if (!isPhotoGuideShown.value) {
                                scope.launch {
                                    bottomSheetState.expand()
                                }

                                showBottomSheet = true
                                sharedPreferences.edit()
                                    .putBoolean(SharedPrefUtils.SHOW_PHOTO_GUIDE_DIALOG, true)
                                    .apply()
                                isPhotoGuideShown.value = true

                            } else {
                                cameraUri = viewModel.getUri()
                                cameraLauncher.launch(cameraUri!!)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Icon(
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterHorizontally),
                            painter = painterResource(R.drawable.camera_icon),
                            contentDescription = stringResource(R.string.label_camera),
                            tint = Color.Black
                        )
                        Text(
                            text = stringResource(R.string.label_camera),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onCustom300,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                    }

                }

                // Add sample images
                sampleImages.forEach { sampleImage ->
                    AssetImage(
                        modifier = Modifier
                            .size(itemSize)
                            .padding(horizontal = 1.dp, vertical = 1.dp),
                        assetInfo = sampleImage,
                        navigateToPreview = { selected ->
                            viewModel.toggleSelect(selected, sampleImage)
//                            viewModel.navigateToPreview(0, "", requestType)
                        },
                        selectedList = viewModel.selectedList,
                        onLongClick = { selected -> viewModel.toggleSelect(selected, sampleImage) }
                    )
                }

                allAssets.forEachIndexed { index, assetInfo ->
                    AssetImage(
                        modifier = Modifier
                            .size(itemSize)
                            .padding(horizontal = 1.dp, vertical = 1.dp),
                        assetInfo = assetInfo,
                        navigateToPreview = { selected ->
                            viewModel.toggleSelect(selected, assetInfo)
//                            viewModel.navigateToPreview(index, "", requestType)
                        },
                        selectedList = viewModel.selectedList,
                        onLongClick = { selected -> viewModel.toggleSelect(selected, assetInfo) }
                    )
                }
            }
        }
        /*assets.forEach { (dateString, resources) ->
            val allSelected = viewModel.isAllSelected(resources)
            val isAlreadyFull = viewModel.selectedList.size == maxAssets
            val hasSelected = viewModel.hasSelected(resources)

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

            item {
                val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp / gridCount)
                FlowRow(maxItemsInEachRow = gridCount) {
                    resources.forEachIndexed { index, assetInfo ->
                        AssetImage(
                            modifier = Modifier
                                .size(itemSize)
                                .padding(horizontal = 1.dp, vertical = 1.dp),
                            assetInfo = assetInfo,
                            navigateToPreview = { viewModel.navigateToPreview(index, dateString, requestType) },
                            selectedList = viewModel.selectedList,
                            onLongClick = { selected -> viewModel.toggleSelect(selected, assetInfo) }
                        )
                    }
                }
            }
        }*/
    }

    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    bottomSheetState.hide()
                }
                showBottomSheet = false
            },
            containerColor = colors.background,
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                // Header with Title and Close Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Photo Guide",
                        color = colors.onCustom400,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    IconButton(onClick = {
                        showBottomSheet = false
                        scope.launch {
                            bottomSheetState.hide()
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.close_circled_icon),
                            contentDescription = "Close",
                        )
                    }
                }

                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    painter = painterResource(R.drawable.photo_guide_image),
                    contentDescription = "Photo Guide",
                )

                // Spacer to ensure padding at the bottom
                Spacer(modifier = Modifier.height(16.dp))
                // Text Section with Heading
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = "Background:",
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onCustom400,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append(" • Plain white or light-colored background.\n")
                            append(" • No shadows, patterns, or objects behind you.\n")
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = "Face Position:",
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onCustom400,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append(" • Face should be centered and fully visible.\n")
                            append(" • Neutral expression (no smile, mouth closed).\n")
                            append(" • Eyes open and clearly visible.\n")
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = "Glasses & Accessories:",
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onCustom400,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append(" • No sunglasses.\n")
                            append(" • If you wear glasses, ensure no glare.\n")
                            append(" • No headphones covering the face.\n")
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                }

            }

        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GuideDialogPreview() {
    PreviewContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            // Header with Title and Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Photo Guide",
                    color = colors.onCustom400,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                IconButton(onClick = {

                }) {
                    Icon(
                        painter = painterResource(R.drawable.close_circled_icon),
                        contentDescription = "Close",
                        tint = colors.onSurfaceVariant
                    )
                }
            }
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                painter = painterResource(R.drawable.photo_guide_image),
                contentDescription = "Photo Guide",
            )

            // Spacer to ensure padding at the bottom
            Spacer(modifier = Modifier.height(16.dp))
            // Text Section with Heading
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "Background:",
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onCustom400,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        append(" • Plain white or light-colored background.\n")
                        append(" • No shadows, patterns, or objects behind you.\n")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "Face Position:",
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onCustom400,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        append(" • Face should be centered and fully visible.\n")
                        append(" • Neutral expression (no smile, mouth closed).\n")
                        append(" • Eyes open and clearly visible.\n")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "Glasses & Accessories:",
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onCustom400,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        append(" • No sunglasses.\n")
                        append(" • If you wear glasses, ensure no glare.\n")
                        append(" • No headphones covering the face.\n")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            }


        }
    }
}

@Composable
private fun AssetImage(
    modifier: Modifier = Modifier,
    assetInfo: AssetInfo,
    selectedList: SnapshotStateList<AssetInfo>,
    navigateToPreview: (Boolean) -> Unit,
    onLongClick: (Boolean) -> Unit,
) {
    val selected = selectedList.any { it.id == assetInfo.id }
    val context = LocalContext.current
    val maxAssets = LocalAssetConfig.current.maxAssets
    val errorMessage = stringResource(R.string.message_selected_exceed, maxAssets)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.TopEnd,
    ) {
        AssetImageItem(
            filterQuality = FilterQuality.None,
            urlString = assetInfo.uriString,
            isSelected = selected,
            resourceType = assetInfo.resourceType,
            durationString = assetInfo.formatDuration(),
            navigateToPreview = {
                val isSelected = !selected
                if (isSelected) {
                    selectedList.clear()
                    selectedList.add(assetInfo)
                } else {
                    selectedList.clear()
                }
                navigateToPreview(isSelected)
            },
            onLongClick = {

                val isSelected = !selected
                if (isSelected) {
                    selectedList.clear()
                    selectedList.add(assetInfo)
                } else {
                    selectedList.clear()
                }

                onLongClick(isSelected)

                /*val selectResult = !selected
                if (selected || selectedList.size < maxAssets) {
                    onLongClick(selectResult)
                } else {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }*/
            }
        )
        AssetImageIndicator(
            assetInfo = assetInfo,
            selected = selected,
            assetSelected = selectedList
        )
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AssetImagePreview() {
    PreviewContainer {

        val context = LocalContext.current
        val maxAssets = LocalAssetConfig.current.maxAssets
        val errorMessage = stringResource(R.string.message_selected_exceed, maxAssets)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(colors.onBackground),
            contentAlignment = Alignment.TopEnd,
        ) {
            AssetImageItem(
                filterQuality = FilterQuality.None,
                urlString = "",
                isSelected = true,
                resourceType = AssetResourceType.IMAGE,
                durationString = "",
                navigateToPreview = {},
                onLongClick = {

                }
            )
            AssetImageIndicator(
                assetInfo = AssetInfo(0L, "", "", "", "", 0L, 0, "", 0L, 0L),
                selected = true,
                assetSelected = SnapshotStateList<AssetInfo>()
            )
        }
    }
}

private sealed class TabItem(
    @StringRes val resourceId: Int,
    val screen: @Composable (AssetViewModel) -> Unit,
) {
    data object All :
        TabItem(R.string.tab_item_all, { viewModel -> AssetContent(viewModel, RequestType.COMMON) })

    data object Video : TabItem(
        R.string.tab_item_video,
        { viewModel -> AssetContent(viewModel, RequestType.VIDEO) })

    data object Image : TabItem(
        R.string.tab_item_image,
        { viewModel -> AssetContent(viewModel, RequestType.IMAGE) })
}
