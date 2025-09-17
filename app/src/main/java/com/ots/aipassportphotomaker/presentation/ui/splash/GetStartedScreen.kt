package com.ots.aipassportphotomaker.presentation.ui.splash

import android.Manifest
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.DocumentPixels
import com.ots.aipassportphotomaker.domain.model.DocumentSize
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
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.BackgroundOption
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentInfoScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentInfoScreenUiState
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentInfoScreenViewModel
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
fun GetStartedPage(
    mainRouter: MainRouter,
    viewModel: GetStartedScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
    onGetStartedClick: () -> Unit
) {
    val TAG = "GetStartedPage"

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activityContext = context as ComponentActivity

    Logger.d(TAG, "GetStartedPage: UI State: $uiState")

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "GetStartedPage: Navigation State: $navigationState")
        when (navigationState) {
            is GetStartedScreenNavigationState.OnboardingScreen -> {
//                mainRouter.navigateToPhotoIDDetailScreen(navigationState.type)
            }

            is GetStartedScreenNavigationState.HomeScreen -> {
                Log.d(TAG, "GetStartedPage: Navigate to Select Photo Screen")
                /*mainRouter.navigateToSelectPhotoScreen(
                    documentId = navigationState.documentId,

                )*/
            }
        }
    }


    GetStartedScreen(
        uiState = uiState,

        onBackClick = {
            mainRouter.goBack()
        },
        onGetStartedClick = onGetStartedClick
    )

}


@Composable
private fun GetStartedScreen(
    uiState: GetStartedScreenUiState,
    onBackClick: () -> Unit,
    onGetStartedClick: () -> Unit
) {

    val TAG = "GetStartedScreen"

    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
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

//            Box(
//                modifier = Modifier
//                    .background(colors.background)
//                    .fillMaxSize(),
//            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.background),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.primaryContainer),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.after_image_girl),
                            contentDescription = stringResource(id = R.string.app_name),
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer { alpha = 0.99F }
                                .drawWithContent {
                                    val height = size.height

                                    val colors = listOf(Color.Transparent, colors.background)
                                    drawContent()
                                    drawRect(
                                        brush = Brush.verticalGradient(
                                            colors = colors,
                                            startY = height, // Start at the bottom
                                            endY = height - (height / 2f) // End halfway up
                                        ),
                                        blendMode = BlendMode.DstIn
                                    )
                                },

                            contentScale = ContentScale.Fit
                        )

                        Spacer(
                            Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            colors.background
                                        )
                                    )
                                )
                            .align(Alignment.BottomCenter)
                        )

                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .scale(textAnimatedScale)
                                .padding(horizontal = 16.dp),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Creation of Professional identification Photos.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .scale(textAnimatedScale)
                                .padding(horizontal = 16.dp),
                        )


                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                onGetStartedClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .scale(buttonAnimatedScale),
                        ) {
                            Text("Get Started")
                        }


                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Advertisement",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = colors.onSurfaceVariant,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .wrapContentSize(align = Alignment.Center)
                            )

                        }

                        Spacer(modifier = Modifier.height(10.dp))

                    }

                }




//            }
        }
    }
}

@Preview("Light", device = "id:pixel_5", showSystemUi = true)
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DocumentInfoScreenPreview() {
    PreviewContainer {
        GetStartedScreen(
            uiState = GetStartedScreenUiState(
                showLoading = false,
                errorMessage = null
            ),
            onBackClick = {},
            onGetStartedClick = {}
        )
    }
}