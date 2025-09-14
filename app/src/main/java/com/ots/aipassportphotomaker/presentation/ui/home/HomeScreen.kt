package com.ots.aipassportphotomaker.presentation.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.image_picker.model.AssetPickerConfig
import com.ots.aipassportphotomaker.image_picker.view.AssetPicker
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.ChooseOrPickImage
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.createImageUri
import com.ots.aipassportphotomaker.presentation.ui.editimage.EditImageScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.processimage.ImageProcessingScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomePage(
    mainRouter: MainRouter,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "HomePage"
    val uiState by viewModel.uiState.collectAsState()
    val lazyGridState = rememberLazyGridState()
    val showAssetPicker = remember { mutableStateOf(false) }
    val uiScope = rememberCoroutineScope()

    var itemClicked: String by remember { mutableStateOf("") }

    viewModel.navigationState.collectAsEffect { navigationState ->
        Logger.d(TAG, "HomePage: Navigation State: $navigationState")
        when (navigationState) {
            is HomeScreenNavigationState.PhotoID -> {
                navigationState
                mainRouter.navigateToPhotoIDScreen2()
            }

            is HomeScreenNavigationState.CutOutScreen -> mainRouter.navigateFromHomeToCutOutScreen(
                imageUrl = uiState.imagePath,
                sourceScreen = "HomeScreen"
            )

            is HomeScreenNavigationState.EditImageScreen -> mainRouter.navigateToEditImageScreen(
                documentId = navigationState.documentId,
                imageUrl = uiState.imagePath,
                selectedBackgroundColor = Color.Unspecified,
                sourceScreen = "HomeScreen"
            )
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            Logger.d(TAG, "HomePage: No image selected from gallery")
            return@rememberLauncherForActivityResult
        }
        Logger.d(TAG, "HomePage: Gallery image selected: $uri")
        viewModel.updateImagePath(uri.toString())
        uiScope.launch {
            delay(500L)
        }
        viewModel.onItemClick(itemClicked)
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
            when(name) {
                "PhotoID" -> {
                    viewModel.onItemClick(name)
                }
                "Cutout" -> {
                    itemClicked = "Cutout"
                    galleryLauncher.launch("image/*")

                }
                "ChangeBG" -> {
                    itemClicked = "ChangeBG"
                    galleryLauncher.launch("image/*")
                }
            }
        },
        onGalleryClick = {
            showAssetPicker.value = true
        }
    )

   /* if (showAssetPicker.value) {
        AssetPicker(
            assetPickerConfig = AssetPickerConfig(gridCount = 3),
            onPicked = { assetInfo ->
//                isImageSelected = it.isNotEmpty()
                Logger.i(
                    "DocumentInfoPage",
                    "Selected images: ${assetInfo.size}, asset: ${assetInfo.firstOrNull()?.uriString}"
                )
                viewModel.selectedImagesList.clear()
                viewModel.selectedImagesList.addAll(assetInfo)
                showAssetPicker.value = false
            },
            onClose = {
                viewModel.selectedImagesList.clear()
                showAssetPicker.value = false
            }
        )
    }*/
}

@Composable
private fun HomeScreen(
    uiState: HomeScreenUiState,
    lazyGridState: LazyGridState,
    onItemClick: (name: String) -> Unit,
    oneCutOutClick: () -> Unit = {},
    onChangeBgClick: () -> Unit = {},
    onAddSuitClick: () -> Unit = {},
    onGalleryClick: (String) -> Unit,
    onCameraImage: (String) -> Unit = {},
) {
    val context = LocalContext.current
    var cameraUri: Uri? by remember { mutableStateOf(null) }
    val galleryUri: String? by remember { mutableStateOf(null) }
    var itemClicked: String by remember { mutableStateOf("") }

    // Launcher for camera and gallery
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let {
                onCameraImage(it.toString())
            }
        }
    }
//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri ->
//        when(itemClicked) {
//            "Choose from Gallery" -> {
//                onGalleryClick(uri.toString())
//                Logger.d("HomeScreen", "Gallery image selected: $uri")
//            }
//            "PhotoID" -> {
//                onItemClick("PhotoID")
//                Logger.d("HomeScreen", "Photo ID image selected: $uri")
//            }
//            "Cutout" -> {
//                onItemClick("Cutout")
//                Logger.d("HomeScreen", "Cut Out image selected: $uri")
//            }
//            "ChangeBG" -> {
//                onItemClick("ChangeBG")
//                Logger.d("HomeScreen", "Change BG image selected: $uri")
//            }
//            "AddSuits" -> {
//                onItemClick("AddSuits")
//                Logger.d("HomeScreen", "Add Suit image selected: $uri")
//            }
//        }
//
//    }
//
//    when(itemClicked) {
//        "Choose from Gallery" -> {
//            galleryLauncher.launch("image/*")
//        }
//
//        "PhotoID" -> {
//            onItemClick("Photo ID")
//        }
//
//        "Cutout" -> {
//            galleryLauncher.launch("image/*")
//        }
//
//        "ChangeBG" -> {
//            galleryLauncher.launch("image/*")
//        }
//
//        "AddSuits" -> {
//            galleryLauncher.launch("image/*")
//        }
//    }

    Surface(
        modifier = Modifier
            .background(colors.background)
    ) {
        if (uiState.showLoading) {
            LoaderFullScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                                Logger.i("HomeScreen", "Clicked on item: ${item.name}")
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
                        cameraUri = createImageUri(context)
                        cameraUri?.let {
                            cameraLauncher.launch(it)
                        }
                        Logger.d("HomeScreen", "Camera image clicked")
                    },
                    onChooseImage = {
                        itemClicked = "Choose from Gallery"
//                        galleryLauncher.launch("image/*")
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
        HomeScreen(
            uiState = uiState,
            lazyGridState = lazyGridState,
            onItemClick = { name -> },
            onGalleryClick = {}
        )
    }
}
