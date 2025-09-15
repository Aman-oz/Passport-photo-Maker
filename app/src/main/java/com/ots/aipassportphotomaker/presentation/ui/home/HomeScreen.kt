package com.ots.aipassportphotomaker.presentation.ui.home

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.model.CustomDocumentData
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.ChooseOrPickImage
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.createImageUri
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom300
import com.ots.aipassportphotomaker.presentation.ui.theme.customError
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom300
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

            is HomeScreenNavigationState.PhotoIDDetails -> mainRouter.navigateToPhotoIDDetailScreen(
                navigationState.type
            )
// To be implemented later for custom size
            /*is HomeScreenNavigationState.DocumentInfoScreen -> mainRouter.navigateFromCustomHomeToDocumentInfoScreen(
                navigationState.customData
            )*/
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
            when (name) {
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
        },
        onCustomSizeClick = { customData ->
            viewModel.onCustomSizeClick(customData)

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
    onCustomSizeClick: (customData: CustomDocumentData) -> Unit = {},
    onGalleryClick: (String) -> Unit,
    onCameraImage: (String) -> Unit = {},
) {
    val context = LocalContext.current
    var cameraUri: Uri? by remember { mutableStateOf(null) }
    val galleryUri: String? by remember { mutableStateOf(null) }
    var itemClicked: String by remember { mutableStateOf("") }

    var showCustomBottomSheet by rememberSaveable { mutableStateOf(false) }
    val customBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                                } else {
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
                    var showErrorText by remember { mutableStateOf(false) }
                    var selectedUnit by remember { mutableStateOf("inch") } // Default unit
                    var width by remember { mutableStateOf("") }
                    var height by remember { mutableStateOf("") }

                    var pxHint by remember { mutableStateOf("320 - 1920") }
                    var inchHint by remember { mutableStateOf("1.07 - 6.0") }
                    var mmHint by remember { mutableStateOf("20 - 150") }

                    var isValidWidth by remember { mutableStateOf(false) }
                    var isValidHeight by remember { mutableStateOf(false) }


                    val hint = when (selectedUnit) {
                        "px" -> pxHint
                        "inch" -> inchHint
                        "mm" -> mmHint
                        else -> ""
                    }



                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Title and Close Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Choose custom Size",
                                color = colors.onCustom400,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        customBottomSheetState.hide()
                                    }
                                    showCustomBottomSheet = false
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.close_circled_icon), // Replace with your cross icon if needed
                                    contentDescription = "Close",
                                    tint = colors.onSurfaceVariant
                                )
                            }
                        }

                        // Three Selectable Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { selectedUnit = "px" },
                                modifier = Modifier
                                    .weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedUnit == "px") colors.primary else colors.custom300
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "px",
                                    color = if (selectedUnit == "px") colors.onPrimary else colors.onCustom300,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Button(
                                onClick = { selectedUnit = "inch" },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedUnit == "inch") colors.primary else colors.custom300
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "inch",
                                    color = if (selectedUnit == "inch") colors.onPrimary else colors.onCustom300,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Button(
                                onClick = { selectedUnit = "mm" },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedUnit == "mm") colors.primary else colors.custom300
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "mm",
                                    color = if (selectedUnit == "mm") colors.onPrimary else colors.onCustom300,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Width and Height Inputs
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, bottom = 2.dp, end = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Width",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onBackground,
                                )
                                Text(
                                    text = selectedUnit,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primary,
                                )
                            }

                            OutlinedTextField(
                                value = width,
                                shape = RoundedCornerShape(32.dp),
                                onValueChange = {
                                    width = it
                                    isValidWidth = when (selectedUnit) {
                                        "px" -> {
                                            val widthValue = width.toFloatOrNull() ?: 0f
                                            widthValue in 320f..1920f
                                        }

                                        "inch" -> {
                                            val widthValue = width.toFloatOrNull() ?: 0f
                                            widthValue in 1.07f..6.0f
                                        }

                                        "mm" -> {
                                            val widthValue = width.toFloatOrNull() ?: 0f
                                            widthValue in 20f..150f
                                        }

                                        else -> false
                                    }
                                },
                                label = {
                                    Text(
                                        hint,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.outline
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = colors.outline,
                                    focusedBorderColor = colors.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, bottom = 2.dp, end = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Height",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onBackground,
                                )
                                Text(
                                    text = selectedUnit,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primary,
                                )
                            }
                            OutlinedTextField(
                                value = height,
                                shape = RoundedCornerShape(32.dp),
                                onValueChange = {
                                    height = it
                                    if (it.isNotEmpty()) {
                                        isValidHeight = when (selectedUnit) {
                                            "px" -> {
                                                val heightValue = height.toFloatOrNull() ?: 0f
                                                heightValue in 320f..1920f
                                            }

                                            "inch" -> {
                                                val heightValue = height.toFloatOrNull() ?: 0f
                                                heightValue in 1.07f..6.0f
                                            }

                                            "mm" -> {
                                                val heightValue = height.toFloatOrNull() ?: 0f
                                                heightValue in 20f..150f
                                            }

                                            else -> false
                                        }
                                    }
                                },
                                label = {
                                    Text(
                                        hint,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.outline
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        // Error Text
                        AnimatedVisibility(showErrorText) {
                            Text(
                                text = "Please fill all fields",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.customError,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            LaunchedEffect(Unit) {
                                delay(4000)
                                showErrorText = false
                            }
                        }

                        // Done Button
                        Button(
                            onClick = {
                                // Validate inputs
                                if (!isValidWidth || !isValidHeight)
                                    return@Button

                                val customData = CustomDocumentData(
                                    documentName = "Custom Document",
                                    documentSize = "$width x $height",
                                    documentUnit = selectedUnit,
                                    documentPixels = "${width}x${height}",
                                    documentResolution = "300",
                                    documentImage = null,
                                    documentType = "custom",
                                    documentCompleted = null
                                )

                                onCustomSizeClick(customData)
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isValidWidth && isValidHeight) colors.primary else colors.surfaceVariant)
                        ) {
                            Text(
                                text = "Done",
                                color = if (isValidWidth && isValidHeight) colors.onPrimary else colors.onSurfaceVariant,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CustomDialogBottomSheetPreview() {
    PreviewContainer {
        var showErrorText by remember { mutableStateOf(false) }
        var selectedUnit by remember { mutableStateOf("inch") } // Default unit
        var width by remember { mutableStateOf("") }
        var height by remember { mutableStateOf("") }

        var pxHint by remember { mutableStateOf("320 - 1920") }
        var inchHint by remember { mutableStateOf("1.07 - 6.0") }
        var mmHint by remember { mutableStateOf("20 - 150") }

        var isValidWidth by remember { mutableStateOf(false) }
        var isValidHeight by remember { mutableStateOf(false) }

        var isButtonEnabled by remember { mutableStateOf(false) }

        val hint = when (selectedUnit) {
            "px" -> pxHint
            "inch" -> inchHint
            "mm" -> mmHint
            else -> ""
        }



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title and Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Choose custom Size",
                    color = colors.onCustom400,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(
                    onClick = {

                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.close_circled_icon), // Replace with your cross icon if needed
                        contentDescription = "Close",
                        tint = colors.onSurfaceVariant
                    )
                }
            }

            // Three Selectable Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { selectedUnit = "px" },
                    modifier = Modifier
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedUnit == "px") colors.primary else colors.custom300
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "px",
                        color = if (selectedUnit == "px") colors.onPrimary else colors.onCustom300,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Button(
                    onClick = { selectedUnit = "inch" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedUnit == "inch") colors.primary else colors.custom300
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "inch",
                        color = if (selectedUnit == "inch") colors.onPrimary else colors.onCustom300,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Button(
                    onClick = { selectedUnit = "mm" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedUnit == "mm") colors.primary else colors.custom300
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "mm",
                        color = if (selectedUnit == "mm") colors.onPrimary else colors.onCustom300,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Width and Height Inputs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 2.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Width",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground,
                    )
                    Text(
                        text = selectedUnit,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                    )
                }

                OutlinedTextField(
                    value = width,
                    shape = RoundedCornerShape(32.dp),
                    onValueChange = {
                        width = it
                        isValidWidth = when (selectedUnit) {
                            "px" -> {
                                val widthValue = width.toFloatOrNull() ?: 0f
                                widthValue in 320f..1920f
                            }

                            "inch" -> {
                                val widthValue = width.toFloatOrNull() ?: 0f
                                widthValue in 1.07f..6.0f
                            }

                            "mm" -> {
                                val widthValue = width.toFloatOrNull() ?: 0f
                                widthValue in 20f..150f
                            }

                            else -> false
                        }
                                    },
                    label = {
                        Text(
                            hint,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = colors.outline
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colors.outline,
                        focusedBorderColor = colors.primary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 2.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Height",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground,
                    )
                    Text(
                        text = selectedUnit,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                    )
                }
                OutlinedTextField(
                    value = height,
                    shape = RoundedCornerShape(32.dp),
                    onValueChange = {
                        height = it
                        if (it.isNotEmpty()) {
                            isValidHeight = when (selectedUnit) {
                                "px" -> {
                                    val heightValue = height.toFloatOrNull() ?: 0f
                                    heightValue in 320f..1920f
                                }

                                "inch" -> {
                                    val heightValue = height.toFloatOrNull() ?: 0f
                                    heightValue in 1.07f..6.0f
                                }

                                "mm" -> {
                                    val heightValue = height.toFloatOrNull() ?: 0f
                                    heightValue in 20f..150f
                                }

                                else -> false
                            }
                        }
                    },
                    label = {
                        Text(
                            hint,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = colors.outline
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Error Text
            AnimatedVisibility(showErrorText) {
                Text(
                    text = "Please fill all fields",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.customError,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LaunchedEffect(Unit) {
                    delay(4000)
                    showErrorText = false
                }
            }

            // Done Button
            Button(
                onClick = {
                    // Validate inputs
                    if (!isValidWidth || !isValidHeight)
                        return@Button


                    if (width.isNotEmpty() && height.isNotEmpty()) {
                        // Handle save with width, height, and selectedUnit
                    } else {
                        showErrorText = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isValidWidth && isValidHeight) colors.primary else colors.surfaceVariant)
            ) {
                Text(
                    text = "Done",
                    color = if (isValidWidth && isValidHeight) colors.onPrimary else colors.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
