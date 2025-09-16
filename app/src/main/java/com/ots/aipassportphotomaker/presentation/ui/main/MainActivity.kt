package com.ots.aipassportphotomaker.presentation.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.farimarwat.permissionmate.PMate
import com.farimarwat.permissionmate.rememberPermissionMateState
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.common.utils.SharedPrefUtils
import com.ots.aipassportphotomaker.di.AppSettingsSharedPreference
import com.ots.aipassportphotomaker.domain.model.CustomDocumentData
import com.ots.aipassportphotomaker.domain.permission.PermissionsHelper
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.presentation.ui.components.CameraPermissionTextProvider
import com.ots.aipassportphotomaker.presentation.ui.components.ChangeThemDialog
import com.ots.aipassportphotomaker.presentation.ui.components.NoInternetConnectionBanner
import com.ots.aipassportphotomaker.presentation.ui.components.PermissionDialog
import com.ots.aipassportphotomaker.presentation.ui.components.SettingsScreen
import com.ots.aipassportphotomaker.presentation.ui.components.StoragePermissionTextProvider
import com.ots.aipassportphotomaker.presentation.ui.theme.AIPassportPhotoMakerTheme
import com.ots.aipassportphotomaker.presentation.ui.theme.AppTheme
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom300
import com.ots.aipassportphotomaker.presentation.ui.theme.customError
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom300
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400
import com.ots.aipassportphotomaker.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @AppSettingsSharedPreference
    lateinit var appSettings: SharedPreferences

    @Inject
    lateinit var networkMonitor: NetworkMonitor
    @Inject
    lateinit var permissionsHelper: PermissionsHelper

    //Camera and Read External Storage for android 13 and below
    private val permissionsForAndroid13AndBelow = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    //Camera and Read Media Images for android 14 and above
    private val permissionsForAndroid14AndAbove = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES
    )

    val permissionsToRequest: List<String>
        get() = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionsForAndroid14AndAbove
        } else {
            permissionsForAndroid13AndBelow
        }

    private fun isDarkModeEnabled() = appSettings.getBoolean(SharedPrefUtils.DARK_MODE, false)

    private fun enableDarkMode(enable: Boolean) = appSettings.edit().putBoolean(SharedPrefUtils.DARK_MODE, enable).commit()

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        /*permissionsHelper.initRegisterForRequestMultiplePermissionsInActivity(
            this,
            onPermissionsGranted = { permissions ->
                Logger.i("MainActivity", "Permissions granted: $permissions")
            },
            onPermissionsDenied = { permissions ->
                Logger.w("MainActivity", "Permissions denied: $permissions")
            }
        )*/

        setContent {

            val navController = rememberNavController()
            var darkMode by remember { mutableStateOf(isDarkModeEnabled()) }

            val scope = rememberCoroutineScope()

            val customBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val changeThemeBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            var showSettingsDialog by rememberSaveable { mutableStateOf(false) }
            var showChangeThemeDialog by rememberSaveable { mutableStateOf(false) }

            AppTheme(darkMode) {

                val viewModel = viewModel<MainViewModel>()
                val dialogQueue = viewModel.visiblePermissionDialogQueue

                val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        viewModel.onPermissionResult(
                            permission = Manifest.permission.CAMERA,
                            isGranted = isGranted
                        )
                    }
                )

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

                // Trigger permission request after composition
                LaunchedEffect(Unit) {
                    lifecycleScope.launch {
                        multiplePermissionResultLauncher.launch(permissionsToRequest.toTypedArray())
                    }
                }

                Column {
                    val networkStatus by networkMonitor.networkState.collectAsState(null)

                    networkStatus?.let {
                        if (it.isOnline.not()) {
                            NoInternetConnectionBanner()
                        }
                    }

                    MainGraph(
                        mainNavController = navController,
                        darkMode = darkMode,
                        onSettingClick = {
                            Logger.d("MainActivity", "Settings icon clicked")
                            showSettingsDialog = true
                        },
                        onThemeUpdated = {
                            val updated = !darkMode
                            enableDarkMode(updated)
                            darkMode = updated
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
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                permission
                            ),
                            onDismiss = viewModel::dismissDialog,
                            onOkClick = {
                                viewModel.dismissDialog()
                                multiplePermissionResultLauncher.launch(
                                    arrayOf(permission)
                                )
                            },
                            onGoToAppSettingsClick = ::openAppSettings
                        )
                    }


                if (showSettingsDialog) {

                    ModalBottomSheet(
                        onDismissRequest = {
                            scope.launch {
                                customBottomSheetState.hide()
                            }
                            showSettingsDialog = false
                        },
                        containerColor = colors.background,
                        sheetState = customBottomSheetState
                    ) {
                        SettingsScreen(
                            onCloseClick = {
                                scope.launch {
                                    customBottomSheetState.hide()
                                }
                                showSettingsDialog = false
                            },
                            onChangeThemeClick = {
                                Logger.i("MainActivity", "Change Theme Clicked")
                                scope.launch {
                                    customBottomSheetState.hide()
                                    delay(500L)
                                }
                                showSettingsDialog = false
                                showChangeThemeDialog = true
                            },
                            onLanguageClick = {
                                Logger.i("MainActivity", "Language Clicked")

                            },
                            onPremiumClick = {
                                Logger.i("MainActivity", "Go Premium Clicked")

                            },
                            onShareApp = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Check out this app: https://play.google.com/store/apps/details?id=$packageName"
                                    )
                                    type = "text/plain"
                                }
                                startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Share via"
                                    )
                                )

                            },
                            onRateUs = {
                                val uri = Uri.parse("market://details?id=$packageName")
                                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                                goToMarket.addFlags(
                                    Intent.FLAG_ACTIVITY_NO_HISTORY or
                                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                                )
                                try {
                                    startActivity(goToMarket)
                                } catch (e: Exception) {
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                                        )
                                    )
                                }
                            },
                            onPrivacyPolicy = {
                                val privacyPolicyUrl = "https://ozipublishing.com/privacy-policy/"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                                startActivity(intent)
                            },
                            appVersion = "1.0.0"
                        )
                    }
                }

                if (showChangeThemeDialog) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            scope.launch {
                                changeThemeBottomSheetState.hide()
                            }
                            showChangeThemeDialog = false
                        },
                        containerColor = colors.background,
                        sheetState = customBottomSheetState
                    ) {
                        ChangeThemDialog {
                            val updated = !darkMode
                            enableDarkMode(updated)
                            darkMode = updated

                            scope.launch {
                                changeThemeBottomSheetState.hide()
                            }
                            showChangeThemeDialog = false

                        }

                    }
                }
            }
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}