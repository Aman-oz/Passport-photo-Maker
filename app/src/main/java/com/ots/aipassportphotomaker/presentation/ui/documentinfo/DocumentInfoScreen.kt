package com.ots.aipassportphotomaker.presentation.ui.documentinfo

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.repository.ColorFactory
import com.ots.aipassportphotomaker.domain.util.determineOrientation
import com.ots.aipassportphotomaker.domain.util.determinePixels
import com.ots.aipassportphotomaker.image_picker.model.AssetInfo
import com.ots.aipassportphotomaker.image_picker.model.AssetPickerConfig
import com.ots.aipassportphotomaker.image_picker.util.StringUtil
import com.ots.aipassportphotomaker.image_picker.view.AssetPicker
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.ColorItem
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.ImageWithMeasurements
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.RadioButtonSingleSelection
import com.ots.aipassportphotomaker.presentation.ui.components.createImageUri
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.AppColors
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom100
import com.ots.aipassportphotomaker.presentation.ui.theme.custom300
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400
import com.ots.aipassportphotomaker.presentation.ui.theme.customError
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom300
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400
import io.mhssn.colorpicker.ColorPickerDialog
import io.mhssn.colorpicker.ColorPickerType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.addAll
import kotlin.text.clear

@Composable
fun DocumentInfoPage(
    mainRouter: MainRouter,
    viewModel: DocumentInfoScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "DocumentInfoPage"

    val uiState by viewModel.uiState.collectAsState()

    val selectedColor by viewModel.selectedColor.collectAsState(initial = Color.White)
    val colorFactory = viewModel.colorFactory

    val showAssetPicker = remember { mutableStateOf(false) }
    var isImageSelected by remember { mutableStateOf(viewModel.selectedImagesList.isNotEmpty()) }

    Logger.d(TAG, "DocumentInfoPage: UI State: $uiState")
    Logger.i(TAG, "DocumentInfoPage: Document name: ${uiState.documentName}")

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "DocumentInfoPage: Navigation State: $navigationState")
        when (navigationState) {
            is DocumentInfoScreenNavigationState.TakePhotoScreen -> {
//                mainRouter.navigateToPhotoIDDetailScreen(navigationState.type)
            }

            is DocumentInfoScreenNavigationState.ProcessingScreen -> {
//                mainRouter.navigateToPhotoIDDetailScreen(navigationState.type)
            }

            is DocumentInfoScreenNavigationState.SelectPhotoScreen -> {
                Log.d(TAG, "DocumentInfoPage: Navigate to Select Photo Screen")
                mainRouter.navigateToSelectPhotoScreen(
                    documentId = navigationState.documentId
                )
            }
        }
    }



    DocumentInfoScreen(
        uiState = uiState,
        isImageSelected = isImageSelected,
        selectedColor = selectedColor,
        colorFactory = colorFactory,
        onOpenGalleryClick = {
            showAssetPicker.value = true
        },
        onTakePhotoClick = { uri ->
            viewModel.selectedImagesList.clear()
            viewModel.selectedImagesList.addAll(
                listOf(AssetInfo(
                    id = 0L,
                    uriString = uri.toString(),
                    filepath = uri.toString(),
                    filename = StringUtil.randomString(10),
                    "Camera", 0L,
                    0,
                    "",
                    0L,
                    0L
                )))
//            viewModel.onOpenCameraClicked()
        },
        onCreatePhotoClick = { type ->
            viewModel.onCreatePhotoClicked()
        },
        onReselectDocument = {
            mainRouter.goBack()
        },
        onBackClick = {
            mainRouter.goBack()
        },
        onSetCustomColor = { color ->
            viewModel.setCustomColor(color)
        },
        selectPredefinedColor = { colorType ->
            viewModel.selectPredefinedColor(colorType)
        },
        onBackgroundOptionChanged = { option ->
            viewModel.onBackgroundOptionChanged(option)
        },
        onApplySelectedColor = {
            viewModel.applySelectedColor()
        }
    )

    if (showAssetPicker.value) {
        AssetPicker(
            assetPickerConfig = AssetPickerConfig(gridCount = 3),
            onPicked = {
                isImageSelected = it.isNotEmpty()
                Logger.i("DocumentInfoPage", "Selected images: ${it.size}, asset: ${it.firstOrNull()?.uriString}")
                viewModel.selectedImagesList.clear()
                viewModel.selectedImagesList.addAll(it)
                showAssetPicker.value = false
//                mainRouter.goBack()
            },
            onClose = {
                viewModel.selectedImagesList.clear()
                showAssetPicker.value = false
//                mainRouter.goBack()
            }
        )
    }

}


@Composable
private fun DocumentInfoScreen(
    uiState: DocumentInfoScreenUiState,
    isImageSelected: Boolean = false,
    selectedColor: Color,
    colorFactory: ColorFactory,
    onOpenGalleryClick: () -> Unit,
    onTakePhotoClick: (Uri) -> Unit,
    onCreatePhotoClick: (type: String) -> Unit,
    onReselectDocument: () -> Unit = {},
    onBackClick: () -> Unit,
    onSetCustomColor: (Color) -> Unit = {},
    selectPredefinedColor: (ColorFactory.ColorType) -> Unit = {},
    onBackgroundOptionChanged: (BackgroundOption) -> Unit = {},
    onApplySelectedColor: () -> Unit = {},
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {

        val context = LocalContext.current
        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage

        if (errorMessage != null) Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

        if (isLoading) {
            LoaderFullScreen()
        } else {

            var scale0 by remember { mutableFloatStateOf(1f) }
            val imageAnimatedScale by animateFloatAsState(
                targetValue = scale0,
                label = "FloatAnimation"
            )
            var scale1 by remember { mutableFloatStateOf(1f) }
            val textAnimatedScale by animateFloatAsState(
                targetValue = scale1,
                label = "FloatAnimation"
            )
            var scale2 by remember { mutableFloatStateOf(1f) }
            val buttonAnimatedScale by animateFloatAsState(
                targetValue = scale2,
                label = "FloatAnimation"
            )

            Box(
                modifier = Modifier
                    .background(colors.background)
                    .fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .background(colors.background)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    CommonTopBar(
                        title = "Document Info",
                        showGetProButton = false,
                        onBackClick = onBackClick,
                        onGetProClick = {})

                    Box(
                        modifier = Modifier
                            .width(140.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        val unitSize = determineOrientation(uiState.documentSize)
                        val pixelSize = determinePixels(uiState.documentPixels)
                        Logger.i(
                            "DocumentInfoScreen",
                            "Document size: width: ${unitSize.width}, height: ${unitSize.height}, orientation: ${unitSize.orientation}"
                        )

                        val ratio = if (unitSize.orientation == "Landscape") {
                            if (unitSize.height != 0f) unitSize.width / unitSize.height else 1f
                        } else if (unitSize.orientation == "Portrait") {
                            300 / 396f
                        } else {
                            1f
                        }

                        val image = if (unitSize.orientation == "Landscape") {
                            R.drawable.sample_image_square
                        } else if (unitSize.orientation == "Portrait") {
                            R.drawable.sample_image_portrait
                        } else {
                            R.drawable.sample_image_square
                        }

                        ImageWithMeasurements(
                            modifier = Modifier
                                .fillMaxWidth(),
                            unitSize = unitSize,
                            pixelSize = pixelSize,
                            unit = uiState.documentUnit,
                            imageRes = image,
                            imageRatio = ratio
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Checklist Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.custom400)
                            .border(1.dp, colors.custom100, RoundedCornerShape(16.dp)),
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            // Title with Icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.passport_united_state), // Replace with passport icon
                                    contentDescription = "Passport Icon",
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = uiState.documentName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = colors.onCustom400,
                                        modifier = Modifier
                                    )
                                    Text(
                                        text = uiState.documentType,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.onSurfaceVariant,
                                        modifier = Modifier
                                    )
                                }


                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(32.dp))
                                        .background(colors.custom300)
                                        .border(
                                            BorderStroke(1.dp, colors.custom100),
                                            RoundedCornerShape(32.dp)
                                        )
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = {
                                                    onReselectDocument()
                                                    Log.d(
                                                        "DocumentInfoScreen",
                                                        "Settings icon tapped"
                                                    )
                                                }
                                            )
                                        },
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_circle),
                                        tint = colors.onCustom300,
                                        contentDescription = "Reselect Document",
                                        modifier = Modifier
                                            .padding(6.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Checklist Items
                            ChecklistItem(
                                uiState,
                                text = "Image Selection",
                                isChecked = isImageSelected,
                                selectedColor = selectedColor,
                                colorFactory = colorFactory,
                                onSetCustomColor = { color ->
                                    onSetCustomColor(color)
                                },
                                onBackgroundOptionChanged = {
                                    option -> onBackgroundOptionChanged(option)
                                },
                                selectPredefinedColor = { colorType: ColorFactory.ColorType ->
                                    selectPredefinedColor(colorType)

                                },
                                onApplySelectedColor = { onApplySelectedColor() }
                            )
                            ChecklistItem(
                                uiState,
                                selectedColor = selectedColor,
                                colorFactory = colorFactory,
                                text = "Document Size",
                                isChecked = true,
                                onBackgroundOptionChanged = {
                                    option -> onBackgroundOptionChanged(option)
                                },
                                selectPredefinedColor = { colorType: ColorFactory.ColorType ->
                                    selectPredefinedColor(colorType)

                                },
                                onApplySelectedColor = { onApplySelectedColor() })
                            ChecklistItem(
                                uiState,
                                selectedColor = selectedColor,
                                colorFactory = colorFactory,
                                text = "Unit",
                                isChecked = true,
                                onBackgroundOptionChanged = {
                                    option -> onBackgroundOptionChanged(option)
                                },
                                selectPredefinedColor = { colorType: ColorFactory.ColorType ->
                                    selectPredefinedColor(colorType)

                                },
                                onApplySelectedColor = { onApplySelectedColor() })
                            ChecklistItem(
                                uiState,
                                selectedColor = selectedColor,
                                colorFactory = colorFactory,
                                text = "Pixel",
                                isChecked = true,
                                onBackgroundOptionChanged = {
                                        option -> onBackgroundOptionChanged(option)
                                },
                                selectPredefinedColor = { colorType: ColorFactory.ColorType ->
                                    selectPredefinedColor(colorType)

                                },
                                onApplySelectedColor = { onApplySelectedColor() })
                            ChecklistItem(
                                uiState,
                                selectedColor = selectedColor,
                                colorFactory = colorFactory,
                                text = "Resolution",
                                isChecked = true,
                                onBackgroundOptionChanged = {
                                        option -> onBackgroundOptionChanged(option)
                                },
                                selectPredefinedColor = { colorType: ColorFactory.ColorType ->
                                    selectPredefinedColor(colorType)

                                },
                                onApplySelectedColor = { onApplySelectedColor() })
                            ChecklistItem(
                                uiState,
                                selectedColor = selectedColor,
                                colorFactory = colorFactory,
                                text = "Background",
                                isChecked = true,
                                onBackgroundOptionChanged = {
                                        option -> onBackgroundOptionChanged(option)
                                },
                                selectPredefinedColor = { colorType: ColorFactory.ColorType ->
                                    selectPredefinedColor(colorType)

                                },
                                onApplySelectedColor = { onApplySelectedColor() })

                        }
                    }
                    // Add spacer to push buttons to the bottom if needed
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Bottom Button Layout

                if (isImageSelected) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            scale1 = 0.90f
                                            tryAwaitRelease()
                                            scale1 = 1f
                                        },
                                        onTap = {
                                            onOpenGalleryClick()
                                        }
                                    )
                                }
                                .scale(textAnimatedScale),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_circle),
                                contentDescription = null,
                                tint = colors.onBackground,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )
                            Text(
                                text = "Retake image",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onBackground,
                                modifier = Modifier
                                    .padding(start = 10.dp) // Adjusted padding for better spacing
                            )
                        }

                        Button(
                            onClick = {
                                onCreatePhotoClick(uiState.documentType)
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .height(48.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            scale2 = 0.90f
                                            tryAwaitRelease()
                                            scale2 = 1f
                                        },
                                        onTap = {
                                            onCreatePhotoClick(uiState.documentType)
                                        }
                                    )
                                }
                                .scale(buttonAnimatedScale)
                        ) {
                            Text(
                                text = "Create Photo",
                                color = colors.onPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }

                } else {
                    val cameraLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.TakePicture()
                    ) { success ->
                        if (success) {
                            onTakePhotoClick(createImageUri(context))
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = onOpenGalleryClick,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary), // Blue color from image
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp, bottom = 8.dp)
                                .height(48.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.gallery_icon), // Replace with gallery icon
                                    contentDescription = "Open Gallery",
                                    tint = colors.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Open Gallery",
                                    color = colors.onPrimary,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val uri = createImageUri(context)
                                cameraLauncher.launch(uri)
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(2.dp, colors.primary),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp, bottom = 8.dp)
                                .height(48.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.camera_icon_outline), // Replace with camera icon
                                    contentDescription = "Take Photo",
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Take Photo",
                                    color = colors.primary,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChecklistItem(
    uiState: DocumentInfoScreenUiState,
    text: String,
    isChecked: Boolean,
    selectedColor: Color = Color.White,
    colorFactory: ColorFactory,
    onSetCustomColor: (Color) -> Unit = {},
    onBackgroundOptionChanged: (BackgroundOption) -> Unit = {},
    selectPredefinedColor: (ColorFactory.ColorType) -> Unit = {},
    onApplySelectedColor: () -> Unit = {},
) {
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()

    var color by remember {
        mutableStateOf(Color.Red)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    ColorPickerDialog(
        show = showDialog,
        type = ColorPickerType.Classic(),
        properties = DialogProperties(),
        onDismissRequest = {
            showDialog = false
        },
        onPickedColor = {
            showDialog = false
            color = it
            onSetCustomColor(it)
            Logger.i("ChecklistItem", "Selected color: $color")
        },
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {

        Image(
            painter = painterResource(id = if (isChecked) R.drawable.check_circle else R.drawable.cross_circle_red),
            contentDescription = if (isChecked) "Checked" else "Unchecked",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .animateContentSize()
                .weight(1f)
        )

        when (text) {
            "Image Selection" -> {
                Icon(
                    painter = painterResource(id = if (isChecked) R.drawable.tick_icon else R.drawable.cross_icon), // Replace with your drawable resource ID
                    contentDescription = null,
                    modifier = Modifier
                )

            }

            "Background" -> {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .aspectRatio(1.4f)
                        .border(1.dp, colors.outline, shape = RoundedCornerShape(6.dp))
                        .background(
                            Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable {
                            scope.launch {
                                bottomSheetState.show()
                                bottomSheetState.expand()
                            }
                            showBottomSheet = true
                        }
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                            .background(
                                if (selectedColor != Color.Unspecified) selectedColor else colors.background,
                                shape = RoundedCornerShape(6.dp)
                            )
                    )

                }

            }

            "Resolution" -> {
                Box(
                    modifier = Modifier
                        .background(colors.custom300, shape = RoundedCornerShape(6.dp))
                        .border(1.dp, colors.custom100, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.pen_icon),
                            contentDescription = "Edit Icon",
                            modifier = Modifier
                                .padding(horizontal = 6.dp),
                            colorFilter = ColorFilter.tint(colors.onCustom300)
                        )
                        Text(
                            text = uiState.documentResolution ?: "300 DPI",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onCustom300
                        )
                    }

                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .background(colors.custom300, shape = RoundedCornerShape(6.dp))
                        .border(1.dp, colors.custom100, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (text) {
                            "Document Size" -> uiState.documentSize ?: "2.0 x 2.0"
                            "Unit" -> uiState.documentUnit ?: "inch"
                            "Pixel" -> uiState.documentPixels ?: "600 x 600 px"
                            else -> ""
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onCustom300
                    )
                }
            }
        }

    }

    // Modal Bottom Sheet
    if (showBottomSheet) {
        var showErrorText by remember { mutableStateOf(false) }

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
                    .padding(16.dp)
            ) {
                Text(
                    text = "Change Background",
                    color = colors.onCustom400,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Select a background color for your document.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                RadioButtonSingleSelection(
                    radioButtonList = listOf("Keep Original", "Change background color"),
                    selectedIndex = if (uiState.backgroundOption == BackgroundOption.KEEP_ORIGINAL) 0 else 1
                ) { selectedIndex ->
                    val option = if (selectedIndex == 0) BackgroundOption.KEEP_ORIGINAL else BackgroundOption.CHANGE_BACKGROUND
                    onBackgroundOptionChanged(option)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {

                    Spacer(modifier = Modifier.width(40.dp))
                    val isChangeBackgroundSelected =
                        uiState.backgroundOption == BackgroundOption.CHANGE_BACKGROUND

                    // 1st: Custom Color (Color Picker)
                    val isCustomSelected = colorFactory.selectedColorType == ColorFactory.ColorType.CUSTOM &&
                            colorFactory.customColor == selectedColor
                    ColorItem(
                        modifier = Modifier.width(42.dp),
                        color = if (colorFactory.isCustomColorSelected()) colorFactory.customColor else Color.White,
                        ratio = 1.2f,
                        showEyeDropper = true,
                        isSelected = isCustomSelected,
                        onClick = {
//                            showBottomSheet = false
                            if (isChangeBackgroundSelected) {
                                onSetCustomColor(color)
                                showDialog = true
                            } else {
                                showErrorText = true
                                scope.launch {
                                    delay(4000)
                                    showErrorText = false
                                }
                            }
                        }
                    )

                    val isTransparentSelected = colorFactory.selectedColorType == ColorFactory.ColorType.TRANSPARENT &&
                            colorFactory.getColorByType(ColorFactory.ColorType.TRANSPARENT) == selectedColor
                    ColorItem(
                        modifier = Modifier
                            .width(42.dp),
                        color = Color.White,
                        ratio = 1.2f,
                        showTransparentBg = true,
                        isSelected = isTransparentSelected,
                        onClick = {
                            if (isChangeBackgroundSelected)
                                selectPredefinedColor(ColorFactory.ColorType.TRANSPARENT)
                            else {
                                showErrorText = true
                                scope.launch {
                                    delay(4000)
                                    showErrorText = false
                                }
                            }
                        }

                    )

                    val isWhiteSelected = colorFactory.selectedColorType == ColorFactory.ColorType.WHITE &&
                            colorFactory.getColorByType(ColorFactory.ColorType.WHITE) == selectedColor
                    ColorItem(
                        modifier = Modifier
                            .width(42.dp),
                        color = Color.White,
                        ratio = 1.2f,
                        isSelected = isWhiteSelected,
                        onClick = {
                            if (isChangeBackgroundSelected)
                                selectPredefinedColor(ColorFactory.ColorType.WHITE)
                            else {
                                showErrorText = true
                                scope.launch {
                                    delay(4000)
                                    showErrorText = false
                                }
                            }
                        }

                    )

                    val isGreenSelected = colorFactory.selectedColorType == ColorFactory.ColorType.GREEN &&
                            colorFactory.getColorByType(ColorFactory.ColorType.GREEN) == selectedColor
                    ColorItem(
                        modifier = Modifier
                            .width(42.dp),
                        color = Color.Green,
                        ratio = 1.2f,
                        isSelected = isGreenSelected,
                        onClick = {
                            if (isChangeBackgroundSelected)
                                selectPredefinedColor(ColorFactory.ColorType.GREEN)
                            else {
                                showErrorText = true
                                scope.launch {
                                    delay(4000)
                                    showErrorText = false
                                }
                            }
                        }

                    )

                    val isBlueSelected = colorFactory.selectedColorType == ColorFactory.ColorType.BLUE &&
                            colorFactory.getColorByType(ColorFactory.ColorType.BLUE) == selectedColor
                    ColorItem(
                        modifier = Modifier
                            .width(42.dp),
                        color = AppColors.LightPrimary,
                        ratio = 1.2f,
                        isSelected = isBlueSelected,
                        onClick = {
                            if (isChangeBackgroundSelected)
                                selectPredefinedColor(ColorFactory.ColorType.BLUE)
                            else {
                                showErrorText = true
                                scope.launch {
                                    delay(4000)
                                    showErrorText = false
                                }
                            }
                        }

                    )

                    val isRedSelected = colorFactory.selectedColorType == ColorFactory.ColorType.RED &&
                            colorFactory.getColorByType(ColorFactory.ColorType.RED) == selectedColor
                    ColorItem(
                        modifier = Modifier
                            .width(42.dp),
                        color = Color.Red,
                        ratio = 1.2f,
                        isSelected = isRedSelected,
                        onClick = {
                            if (isChangeBackgroundSelected)
                                selectPredefinedColor(ColorFactory.ColorType.RED)
                            else {
                                showErrorText = true
                                scope.launch {
                                    delay(4000)
                                    showErrorText = false
                                }
                            }
                        }

                    )


                }
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
                            bottomSheetState.hide()
                        }
                        showBottomSheet = false
                        onApplySelectedColor()
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

@Preview("Light", device = "id:pixel_5", showSystemUi = true)
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DocumentInfoScreenPreview() {
    PreviewContainer {
        DocumentInfoScreen(
            uiState = DocumentInfoScreenUiState(
                showLoading = false,
                errorMessage = null,
                documentName = "Sample Passport",
                documentType = "Passport",
                documentSize = "35 x 45",
                documentUnit = "mm",
                documentPixels = "413x531 px",
                documentResolution = "300 DPI",
                backgroundOption = BackgroundOption.CHANGE_BACKGROUND
            ),
            selectedColor = Color.White,
            colorFactory = ColorFactory(),
            onOpenGalleryClick = {},
            onTakePhotoClick = {},
            onCreatePhotoClick = {},
            onBackClick = {},

            onBackgroundOptionChanged = {

            },
            selectPredefinedColor = { colorType: ColorFactory.ColorType ->

            },
            onApplySelectedColor = {  }
        )
    }
}