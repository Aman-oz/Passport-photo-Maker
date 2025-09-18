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
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.common.utils.SharedPrefUtils
import com.ots.aipassportphotomaker.common.utils.UrlFactory
import com.ots.aipassportphotomaker.di.AppSettingsSharedPreference
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.permission.PermissionsHelper
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.presentation.ui.components.CameraPermissionTextProvider
import com.ots.aipassportphotomaker.presentation.ui.components.ChangeThemDialog
import com.ots.aipassportphotomaker.presentation.ui.components.NoInternetConnectionBanner
import com.ots.aipassportphotomaker.presentation.ui.components.PermissionDialog
import com.ots.aipassportphotomaker.presentation.ui.components.SettingsScreen
import com.ots.aipassportphotomaker.presentation.ui.components.StoragePermissionTextProvider
import com.ots.aipassportphotomaker.presentation.ui.theme.AppTheme
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
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

    private fun isDarkModeEnabled() =
        appSettings.getBoolean(SharedPrefUtils.DARK_MODE, isSystemInDarkMode())

    private fun enableDarkMode(enable: Boolean) =
        appSettings.edit().putBoolean(SharedPrefUtils.DARK_MODE, enable).commit()

    private fun isFirstLaunch(): Boolean = appSettings.getBoolean(SharedPrefUtils.FIRST_LAUNCH, true)

    private fun setFirstLaunch(launched: Boolean) = appSettings.edit().putBoolean(SharedPrefUtils.FIRST_LAUNCH, launched).commit()

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val navController = rememberNavController()
            var darkMode by remember { mutableStateOf(isDarkModeEnabled()) }

            val scope = rememberCoroutineScope()

            val customBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val changeThemeBottomSheetState =
                rememberModalBottomSheetState(skipPartiallyExpanded = true)

            var showSettingsDialog by rememberSaveable { mutableStateOf(false) }
            var showChangeThemeDialog by rememberSaveable { mutableStateOf(false) }

            var selectedTheme by rememberSaveable { mutableIntStateOf(getInitialThemeIndex()) }

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
               /* LaunchedEffect(Unit) {
                    lifecycleScope.launch {
                        multiplePermissionResultLauncher.launch(permissionsToRequest.toTypedArray())
                    }
                }*/

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
                        isFirstLaunch = isFirstLaunch(),
                        onSettingClick = {
                            Logger.d("MainActivity", "Settings icon clicked")
                            showSettingsDialog = true
                        },
                        onThemeUpdated = {
                            val updated = !darkMode
                            enableDarkMode(updated)
                            darkMode = updated
                            selectedTheme = if (updated) 2 else 1
                        },
                        onGetStartedCompleted = { destination ->

                            if (isFirstLaunch()) {
                                setFirstLaunch(false)
                                navController.navigate(destination) {
                                    popUpTo(Page.GetStartedScreen) { inclusive = true }
                                }
                            } else {
                                navController.navigate(destination) {
                                    popUpTo(Page.GetStartedScreen) { inclusive = true }
                                }
                            }
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

                    val appVersion = try {
                        packageManager.getPackageInfo(packageName, 0).versionName
                    } catch (e: Exception) {
                        "1.0.0"
                    } ?: run {
                        "1.0.0"
                    }

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
                            themeSelectedIndex = selectedTheme,
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
                                scope.launch {
                                    customBottomSheetState.hide()
                                }
                                showSettingsDialog = false

                                navController.navigate(Page.Premium) {
                                }

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
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(UrlFactory.PRIVACY_POLICY_URL))
                                startActivity(intent)
                            },
                            appVersion = appVersion
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
                        sheetState = changeThemeBottomSheetState
                    ) {
                        ChangeThemDialog(
                            selectedOption = selectedTheme
                        ) { themeIndex ->

                            when (themeIndex) {
                                0 -> {
                                    val systemDarkMode = isSystemInDarkMode()
                                    enableDarkMode(systemDarkMode)
                                    darkMode = systemDarkMode
                                    selectedTheme = 0

                                }

                                1 -> {
                                    enableDarkMode(false)
                                    darkMode = false
                                    selectedTheme = 1
                                }

                                2 -> {
                                    enableDarkMode(true)
                                    darkMode = true
                                    selectedTheme = 2
                                }
                            }

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

    private fun isSystemInDarkMode(): Boolean {
        val currentNightMode =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    private fun getInitialThemeIndex(): Int {
        return if (appSettings.contains(SharedPrefUtils.DARK_MODE)) {
            if (isDarkModeEnabled()) 2 else 1
        } else {
            0 // Default to system theme if no preference set
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

