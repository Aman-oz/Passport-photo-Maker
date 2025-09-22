package com.ots.aipassportphotomaker.presentation.ui.documentinfo

import android.Manifest
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.ads.AdSize
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.AdMobBanner
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
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
import com.ots.aipassportphotomaker.presentation.ui.components.CameraPermissionTextProvider
import com.ots.aipassportphotomaker.presentation.ui.components.ColorItem
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.DpiItem
import com.ots.aipassportphotomaker.presentation.ui.components.ImageWithMeasurements
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.PermissionDialog
import com.ots.aipassportphotomaker.presentation.ui.components.PremiumButton
import com.ots.aipassportphotomaker.presentation.ui.components.RadioButtonSingleSelection
import com.ots.aipassportphotomaker.presentation.ui.components.StoragePermissionTextProvider
import com.ots.aipassportphotomaker.presentation.ui.components.createImageUri
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.main.openAppSettings
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
import kotlin.text.compareTo

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun DocumentInfoPage(
    mainRouter: MainRouter,
    viewModel: DocumentInfoScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "DocumentInfoPage"

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activityContext = context as ComponentActivity

    val selectedColor by viewModel.selectedColor.collectAsState(initial = Color.Transparent)
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
                mainRouter.navigateToImageProcessingScreen(
                    documentId = navigationState.documentId,
                    imagePath = viewModel.selectedImagesList.firstOrNull()?.uriString?.toString(),
                    filePath = viewModel.selectedImagesList.firstOrNull()?.filepath?.toString(),
                    documentName = uiState.documentName,
                    documentSize = uiState.documentSize,
                    documentUnit = uiState.documentUnit,
                    documentPixels = uiState.documentPixels,
                    selectedDpi = viewModel.selectedDpi,
                    selectedBackgroundColor = selectedColor,
                    sourceScreen = "DocumentInfoScreen"

                )
            }

            is DocumentInfoScreenNavigationState.SelectPhotoScreen -> {
                Log.d(TAG, "DocumentInfoPage: Navigate to Select Photo Screen")
                mainRouter.navigateToSelectPhotoScreen(
                    documentId = navigationState.documentId,

                )
            }
        }
    }


    val dialogQueue = viewModel.visiblePermissionDialogQueue
    //Camera and Read External Storage for android 13 and below
    val permissionsForAndroid13AndBelow = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    //Camera and Read Media Images for android 14 and above
    val permissionsForAndroid14AndAbove = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES
    )

    val permissionsToRequest: List<String>
     = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        permissionsForAndroid14AndAbove
    } else {
        permissionsForAndroid13AndBelow
    }
    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                viewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }
        }
    )


    DocumentInfoScreen(
        uiState = uiState,
        isImageSelected = isImageSelected,
        selectedColor = selectedColor,
        colorFactory = colorFactory,
        onSelectDpi = { dpi ->
            viewModel.onUpdateDpi(dpi)
        },
        onOpenGalleryClick = {
            showAssetPicker.value = true
        },
        onTakePhotoClick = { uri ->
            isImageSelected = uri.toString().isNotEmpty()
            Logger.i(TAG, "DocumentInfoPage: Camera image URI: $uri")

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
        isPremium = viewModel.isPremiumUser(),
        onCreatePhotoClick = { type ->
            viewModel.onCreatePhotoClicked()
            viewModel.showInterstitialAd(activityContext) {  }
        },
        onReselectDocument = {
            mainRouter.goBack()

            viewModel.showInterstitialAd(activityContext) {  }
        },
        onBackClick = {
            mainRouter.goBack()

            viewModel.showInterstitialAd(activityContext) {  }
        },
        onGetProClick = {
            mainRouter.navigateToPremiumScreen()
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
        },
        onPermissionResult = { permission, isGranted ->
            viewModel.onPermissionResult(
                permission = permission,
                isGranted = isGranted
            )
            Log.d(TAG, "Permission result: $permission granted: $isGranted")
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

    dialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.CAMERA -> {
                        CameraPermissionTextProvider()
                    }
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        StoragePermissionTextProvider()
                    }
                    else -> return@forEach
                },
                isPermanentlyDeclined = !activityContext.shouldShowRequestPermissionRationale(
                    permission
                ),
                onDismiss = {
                    viewModel.dismissDialog()
                },
                onOkClick = {
                    viewModel.dismissDialog()
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission)
                    )
                },
                onGoToAppSettingsClick = { activityContext.openAppSettings() }
            )
        }

    BackHandler {
        mainRouter.goBack()
        viewModel.showInterstitialAd(activityContext) {  }
    }

}


@Composable
private fun DocumentInfoScreen(
    uiState: DocumentInfoScreenUiState,
    isImageSelected: Boolean = false,
    selectedColor: Color,
    isPremium: Boolean,
    onSelectDpi: (dpi: String) -> Unit = {},
    colorFactory: ColorFactory,
    onOpenGalleryClick: () -> Unit,
    onTakePhotoClick: (Uri) -> Unit,
    onCreatePhotoClick: (type: String) -> Unit,
    onReselectDocument: () -> Unit = {},
    onBackClick: () -> Unit,
    onGetProClick: () -> Unit = {},
    onSetCustomColor: (Color) -> Unit = {},
    selectPredefinedColor: (ColorFactory.ColorType) -> Unit = {},
    onBackgroundOptionChanged: (BackgroundOption) -> Unit = {},
    onApplySelectedColor: () -> Unit = {},
    onPermissionResult: (String, Boolean) -> Unit = { _, _ -> }
) {

    val TAG = "DocumentInfoScreen"

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    val context = LocalContext.current

    // Define permissions based on Android version
    val permissionsToRequest: List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
    // Camera launcher
    var preparedUri: Uri? by remember { mutableStateOf(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            preparedUri?.let {
                Logger.i(TAG, "Photo captured successfully: $it")
                onTakePhotoClick(it)
            }
        } else {
            Logger.w(TAG, "Photo capture canceled or failed")
        }
    }


    // Camera-specific permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Camera permission granted, prepare URI and launch camera
                preparedUri = createImageUri(context)
                if (preparedUri != null) {
                    cameraLauncher.launch(preparedUri!!)
                    Logger.i(TAG, "Camera URI: $preparedUri")
                } else {
                    Logger.e(TAG, "Failed to create image URI")
                    Toast.makeText(context, "Failed to prepare camera", Toast.LENGTH_SHORT).show()
                }
            } else {

                onPermissionResult(Manifest.permission.CAMERA, isGranted)
                Logger.w(TAG, "Camera permission denied")
                //Toast.makeText(context, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Gallery permission launcher
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),

        /*onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                val isGranted = perms[permission] == true
                if (isGranted)
                onPermissionResult(permission, perms[permission] == true)
            }
        }*/
        onResult = { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                Logger.i(TAG, "Gallery permissions granted")
                onOpenGalleryClick()
            } else {
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
                onPermissionResult(permission, allGranted)
                Logger.w(TAG, "Some gallery permissions denied")
//                Toast.makeText(context, "Storage permissions are required to access photos", Toast.LENGTH_SHORT).show()
            }
        }
    )



    Surface(
        modifier = Modifier
            .padding(bottom = systemBarsPadding.calculateBottomPadding())
            .fillMaxSize(),
        color = colors.background
    ) {

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
                        showGetProButton = !isPremium,
                        onBackClick = onBackClick,
                        onGetProClick = {
                            onGetProClick()
                        })

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
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(uiState.documentImage)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Passport Icon",
                                    modifier = Modifier.size(40.dp),
                                    placeholder = painterResource(id = R.drawable.passport_united_state)
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
                                colorFactory = colorFactory
                            )
                            ChecklistItem(
                                uiState,
                                selectedColor = selectedColor,
                                colorFactory = colorFactory,
                                text = "Document Size",
                                isChecked = true)
                            ChecklistItem(
                                uiState,
                                selectedColor = selectedColor,
                                colorFactory = colorFactory,
                                text = "Unit",
                                isChecked = true)
                            ChecklistItem(
                                uiState,
                                selectedColor = selectedColor,
                                colorFactory = colorFactory,
                                text = "Pixel",
                                isChecked = true)
                            ChecklistItem(
                                uiState,
                                selectedColor = selectedColor,
                                colorFactory = colorFactory,
                                text = "Resolution",
                                isChecked = true,
                                onSelectDpi = { dpi ->
                                    onSelectDpi(dpi)
                                },
                                onGetProClick = {
                                    onGetProClick()
                                }
                            )
                            ChecklistItem(
                                uiState,
                                selectedColor = selectedColor,
                                colorFactory = colorFactory,
                                text = "Background",
                                isChecked = true,
                                onSetCustomColor = { color -> onSetCustomColor(color) },
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

                Column(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                ) {
                    if (isImageSelected) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
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

                    }
                    else {
                        var preparedUri: Uri? by remember { mutableStateOf(null) }
                        val cameraLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.TakePicture()
                        ) { success ->
                            if (success) {
                                preparedUri?.let {
                                    onTakePhotoClick(it)
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        galleryPermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
                                    } else {
                                        galleryPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                                    }
                                },
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
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
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

                    if (!isPremium) {
                        var adLoadState by remember { mutableStateOf(false) }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp) // match banner height
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (!adLoadState) {
                                    Text(
                                        text = "Advertisement",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.onSurfaceVariant,
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .fillMaxWidth()
                                            .wrapContentSize(align = Alignment.Center)
                                    )
                                }

                                AdMobBanner(
                                    adUnit = AdIdsFactory.getBannerAdId(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.Center),
                                    adSize = AdSize.BANNER, // or adaptive size if needed
                                    onAdLoaded = { isLoaded ->
                                        adLoadState = isLoaded
                                        Logger.d(TAG, "OnboardingScreen: Ad Loaded: $isLoaded")
                                    }
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
    onSelectDpi: (String) -> Unit = { "300" },
    selectedColor: Color = Color.Transparent,
    colorFactory: ColorFactory,
    onSetCustomColor: (Color) -> Unit = {},
    onBackgroundOptionChanged: (BackgroundOption) -> Unit = {},
    selectPredefinedColor: (ColorFactory.ColorType) -> Unit = {},
    onApplySelectedColor: () -> Unit = {},
    onGetProClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var showColorPickerBottomSheet by rememberSaveable { mutableStateOf(false) }
    val colorBottomSheetState = rememberModalBottomSheetState()

    var showResolutionBottomSheet by rememberSaveable { mutableStateOf(false) }
    val resolutionBottomSheetState = rememberModalBottomSheetState()

    var selectedDpi by rememberSaveable { mutableStateOf("300") }

    var color by remember {
        mutableStateOf(Color.Red)
    }

    //custom color picker dialog
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
                                colorBottomSheetState.show()
                                colorBottomSheetState.expand()
                            }
                            showColorPickerBottomSheet = true
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
                        .clickable {
                            scope.launch {
                                resolutionBottomSheetState.show()
                                resolutionBottomSheetState.expand()
                            }
                            showResolutionBottomSheet = true
                        }
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
                            text = "$selectedDpi DPI",
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

    // Color Picker Bottom Sheet
    if (showColorPickerBottomSheet) {
        var showErrorText by remember { mutableStateOf(false) }

        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    colorBottomSheetState.hide()
                }
                showColorPickerBottomSheet = false
            },
            containerColor = colors.background,
            sheetState = colorBottomSheetState
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
                            colorBottomSheetState.hide()
                        }
                        showColorPickerBottomSheet = false
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

    // Color Picker Bottom Sheet
    if (showResolutionBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    resolutionBottomSheetState.hide()
                }
                showResolutionBottomSheet = false
                onSelectDpi(selectedDpi)
            },
            containerColor = colors.background,
            sheetState = resolutionBottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Change resolution (DPI)",
                    color = colors.onCustom400,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Higher resolution means better image quality.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {

                    // 1st: 300 DPI
                    val dpiList = listOf("300", "350", "450", "600")
                    dpiList.forEach { dpi ->
                        DpiItem(
                            modifier = Modifier,
                            value = dpi,
                            isSelected = dpi == selectedDpi,
                            onClick = {
                                selectedDpi = dpi
                            }
                        )
                    }

                }

                PremiumButton {
                    scope.launch {
                        resolutionBottomSheetState.hide()
                    }
                    showResolutionBottomSheet = false

                    onGetProClick()
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PremiumButtonPreview2(modifier: Modifier = Modifier) {
    PreviewContainer {
        PremiumButton {

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
            isPremium = false,
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