package com.ots.aipassportphotomaker.presentation.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.repository.ColorFactory
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.ChooseOrPickImage
import com.ots.aipassportphotomaker.presentation.ui.components.ColorItem
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.RadioButtonSingleSelection
import com.ots.aipassportphotomaker.presentation.ui.components.createImageUri
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.BackgroundOption
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.AppColors
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.customError
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400
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

            is HomeScreenNavigationState.EditImageScreen -> mainRouter.navigateFromHomeToEditImageScreen(
                documentId = navigationState.documentId,
                imageUrl = uiState.imagePath,
                selectedBackgroundColor = Color.Unspecified,
                editPosition = uiState.editPosition,
                selectedDpi = "300",
                sourceScreen = "HomeScreen"
            )

            is HomeScreenNavigationState.PhotoIDDetails -> mainRouter.navigateToPhotoIDDetailScreen(navigationState.type)
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
                "AddSuits" -> {
                    itemClicked = "AddSuits"
                    galleryLauncher.launch("image/*")
                }

                "SocialProfile" -> {
                    itemClicked = "SocialProfile"
                    viewModel.onItemClick("SocialProfile")
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

@OptIn(ExperimentalMaterial3Api::class)
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

    var showCustomBottomSheet by rememberSaveable { mutableStateOf(false) }
    val customBottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

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
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        Logger.d("HomeScreen", "Gallery image selected: $uri")

    }

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
                                if (item.name == "CustomSize") {
                                    showCustomBottomSheet = true
                                }else {
                                    onItemClick(item.name)
                                }
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
                        galleryLauncher.launch("image/*")
                        Logger.d("HomeScreen", "Choose image clicked")
                    },
                    isChooseEnabled = true,
                    isPickEnabled = true
                )
            }

            if (showCustomBottomSheet) {
                var showErrorText by remember { mutableStateOf(false) }

                ModalBottomSheet(
                    onDismissRequest = {
                        scope.launch {
                            customBottomSheetState.hide()
                        }
                        showCustomBottomSheet = false
                    },
                    containerColor = colors.background,
                    sheetState = customBottomSheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Custom Size",
                            color = colors.onCustom400,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Select custom size option and apply",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // show this error text animatedly for 4 seconds when user tries to apply without selecting "Change background color" option
                        AnimatedVisibility(showErrorText) {
                            Text(
                                text = "Select change background color option",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.customError,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    customBottomSheetState.hide()
                                }
                                showCustomBottomSheet = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                        ) {
                            Text(
                                text = "Apply",
                                color = colors.onPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }

                }
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
