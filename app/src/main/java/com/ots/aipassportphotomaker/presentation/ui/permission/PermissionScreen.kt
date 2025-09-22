package com.ots.aipassportphotomaker.presentation.ui.permission

import android.Manifest
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdSize
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.AdMobBanner
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CameraPermissionTextProvider
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.components.PermissionDialog
import com.ots.aipassportphotomaker.presentation.ui.components.StoragePermissionTextProvider
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.main.openAppSettings
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun PermissionPage(
    mainRouter: MainRouter,
    viewModel: PermissionScreenViewModel = hiltViewModel(),
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

            is PermissionScreenNavigationState.HomeScreen -> {
                Log.d(TAG, "GetStartedPage: Navigate to Select Photo Screen")
                /*mainRouter.navigateToSelectPhotoScreen(
                    documentId = navigationState.documentId,

                )*/
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

    val permissionsToRequest: List<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsForAndroid14AndAbove
        } else {
            permissionsForAndroid13AndBelow
        }
    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            val allGranted = permissionsToRequest.all { perms[it] == true }
            if (allGranted) {
                // ✅ Only trigger once when *all* are granted
                viewModel.showInterstitialAd(activityContext) {
                    onGetStartedClick()
                }
            } else {
                // Handle each denied separately
                permissionsToRequest.forEach { permission ->
                    if (perms[permission] != true) {
                        viewModel.onPermissionResult(
                            permission = permission,
                            isGranted = false
                        )
                    }
                }
            }
        }
    )



    PermissionScreen(
        uiState = uiState,
        isPremium = viewModel.isPremiumUser(),
        onCloseClick = {

            viewModel.showInterstitialAd(activityContext) { isAdShown ->

                onGetStartedClick()
            }
        },

        onAllowPermissionClick = {
            multiplePermissionResultLauncher.launch(permissionsToRequest.toTypedArray())
        }
    )

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

}


@Composable
private fun PermissionScreen(
    uiState: PermissionScreenUiState,
    onCloseClick: () -> Unit,
    onAllowPermissionClick: () -> Unit,
    isPremium: Boolean = false,
) {

    val TAG = "PermissionScreen"

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    val context = LocalContext.current
    val uiScope = rememberCoroutineScope()

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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.close_circled_icon),
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 40.dp, end = 28.dp)
                        .clickable(
                            onClick = {
                                onCloseClick()
                            }
                        )
                )

                Image(
                    painter = painterResource(id = R.drawable.permission_image),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .graphicsLayer { alpha = 0.99F },
                    contentScale = ContentScale.Fit
                )


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(id = R.string.storage_permission),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .scale(textAnimatedScale)
                            .padding(horizontal = 16.dp),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "To continue, we need access to your photos to process them and save them.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .scale(textAnimatedScale)
                            .padding(horizontal = 20.dp),
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "We respect you privacy - your photos are never stored on our servers.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .scale(textAnimatedScale)
                            .padding(horizontal = 20.dp),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onAllowPermissionClick()

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .scale(buttonAnimatedScale),
                    ) {
                        Text(
                            text = "Allow Permission",
                            modifier = Modifier
                                .padding(vertical = 4.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
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
                                    adUnit = AdIdsFactory.getOnboardingBannerAdId(),
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

@Preview("Light", device = "id:pixel_5", showSystemUi = true)
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DocumentInfoScreenPreview() {
    PreviewContainer {
        PermissionScreen(
            uiState = PermissionScreenUiState(
                showLoading = false,
                errorMessage = null
            ),
            onCloseClick = {},
            onAllowPermissionClick = {},
        )
    }
}