package com.ots.aipassportphotomaker.presentation.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.android.billingclient.BuildConfig
import com.ots.aipassportphotomaker.App
import com.ots.aipassportphotomaker.adsmanager.admob.MyAdsManager
import com.ots.aipassportphotomaker.common.managers.AdsConsentManager
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.managers.PreferencesHelper
import com.ots.aipassportphotomaker.common.utils.AdsConstants
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.common.utils.SharedPrefUtils
import com.ots.aipassportphotomaker.common.utils.UrlFactory
import com.ots.aipassportphotomaker.di.AppSettingsSharedPreference
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import com.ots.aipassportphotomaker.domain.bottom_nav.route
import com.ots.aipassportphotomaker.domain.permission.PermissionsHelper
import com.ots.aipassportphotomaker.domain.repository.DocumentRepository
import com.ots.aipassportphotomaker.domain.repository.SharedRepository
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.domain.util.onError
import com.ots.aipassportphotomaker.domain.util.onSuccess
import com.ots.aipassportphotomaker.presentation.ui.components.CameraPermissionTextProvider
import com.ots.aipassportphotomaker.presentation.ui.components.ChangeThemDialog
import com.ots.aipassportphotomaker.presentation.ui.components.ExitDialog
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
import kotlin.compareTo

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    @Inject
    @AppSettingsSharedPreference
    lateinit var appSettings: SharedPreferences

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var permissionsHelper: PermissionsHelper

    @Inject
    lateinit var adsManager: MyAdsManager

    @Inject
    lateinit var adsConsentManager: AdsConsentManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var documentRepository: DocumentRepository

    @Inject
    lateinit var sharedRepository: SharedRepository

    private var isPremium: Boolean = false

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    private val viewModel: MainViewModel by viewModels()

    private var isHomeScreen = false
    val isOnHomeTab = mutableStateOf(true)
    private var showExitDialog = false
    // Add this function to set the home screen state
    fun setIsHomeScreen(isHome: Boolean) {
        isHomeScreen = isHome
    }

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
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            )
        )

        setContent {

            isPremium = preferencesHelper.getBoolean(AdsConstants.IS_NO_ADS_ENABLED, false)
            val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

            val navController = rememberNavController()
            var darkMode by remember { mutableStateOf(isDarkModeEnabled()) }
            enableDarkMode(darkMode)

            var showDeleteIcon by remember { mutableStateOf(false) }
            var showDeleteAllDialog by remember { mutableStateOf(false) }
            var isHistoryItemsAvailable by remember { mutableStateOf(false) }

            val scope = rememberCoroutineScope()

            val customBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val changeThemeBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val exitBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            var showSettingsDialog by rememberSaveable { mutableStateOf(false) }
            var showChangeThemeDialog by rememberSaveable { mutableStateOf(false) }
//            var showExitDialog by rememberSaveable { mutableStateOf(false) }
//            var showExitDialogState by remember { mutableStateOf(false) }
            val showExitDialog by viewModel.showExitDialog.collectAsState()

            var selectedTheme by rememberSaveable { mutableIntStateOf(getInitialThemeIndex()) }


            AppTheme(darkMode) {

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

                LaunchedEffect(navController) {
                    navController.addOnDestinationChangedListener { _, destination, _ ->
                        Logger.d(TAG, "Destination changed to: ${destination.route} = ${Page.NavigationBar.route()}")
                        analyticsManager.sendAnalytics(AnalyticsConstants.OPENED, "homeScreen")
                        isHomeScreen = destination.route == Page.NavigationBar.route()
                    }
                }

                // Show delete all dialog
                if (showDeleteAllDialog) {
                    analyticsManager.sendAnalytics(AnalyticsConstants.ACTION_VIEW, "deleteAllDialog")
                    AlertDialog(
                        onDismissRequest = { showDeleteAllDialog = false },
                        title = { Text("Delete All Images") },
                        text = { Text("Are you sure you want to delete all images?") },
                        confirmButton = {
                            analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnYes_DeleteAllDialog")
                            TextButton(onClick = {
                                lifecycleScope.launch {
                                    documentRepository.deleteAllCreatedImages()
                                    documentRepository.triggerRefreshEvent()
                                    showDeleteAllDialog = false
                                }
                            }) {
                                Text("Yes")
                            }
                        },
                        dismissButton = {
                            analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnNo_DeleteAllDialog")
                            TextButton(onClick = { showDeleteAllDialog = false }) {
                                Text("No")
                            }
                        }
                    )
                }

                /*LaunchedEffect(Unit) {
                    lifecycleScope.launch {
                        val result = documentRepository.getAllCreatedImages()
                        result.onSuccess {
                            isHistoryItemsAvailable = it.isNotEmpty()
                        }
                        result.onError {
                            isHistoryItemsAvailable = false
                        }
                    }
                }*/

                // Observe database changes
                LaunchedEffect(Unit) {
                    documentRepository.observeCreatedImagesCount().collect { count ->
                        isHistoryItemsAvailable = count > 0
                    }
                }

                Column(
                    modifier = Modifier
                ) {
                    val networkStatus by networkMonitor.networkState.collectAsState(null)

                    /*networkStatus?.let {
                        if (it.isOnline.not()) {
                            NoInternetConnectionBanner()
                        }
                    }*/

                    MainGraph(
                        isPremium = isPremium,
                        systemBarsPadding = systemBarsPadding,
                        mainNavController = navController,
                        darkMode = darkMode,
                        isFirstLaunch = isFirstLaunch(),
                        onSettingClick = {
                            Logger.d("MainActivity", "Settings icon clicked")
                            analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnSettings_HomeScreen")
                            showSettingsDialog = true
                        },
                        showDeleteIcon = showDeleteIcon && isHistoryItemsAvailable,
                        onDeleteClick = {
                            analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnDeleteAllIcon_HomeScreen")
                            if (isHistoryItemsAvailable) {
                                showDeleteAllDialog = true
                            } else {
                                Toast.makeText(this@MainActivity, "No history available", Toast.LENGTH_SHORT).show()
                            }
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
                        },
                        onBottomNavTabChanged = { tabIndex ->
                            Logger.d("MainActivity", "Bottom nav tab changed, isHomeScreen: $isHomeScreen")
                            isOnHomeTab .value = tabIndex == 1
                            showDeleteIcon = tabIndex == 3
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

                    analyticsManager.sendAnalytics(AnalyticsConstants.OPENED, "settingsScreen")
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
                                analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnClose_SettingsScreen")
                                scope.launch {
                                    customBottomSheetState.hide()
                                }
                                showSettingsDialog = false
                            },
                            onChangeThemeClick = {
                                analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnChangeTheme_SettingsScreen")
                                Logger.i("MainActivity", "Change Theme Clicked")
                                scope.launch {
                                    customBottomSheetState.hide()
                                    delay(500L)
                                }
                                showSettingsDialog = false
                                showChangeThemeDialog = true
                            },
                            onLanguageClick = {
                                analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnLanguage_SettingsScreen")
                                Logger.i("MainActivity", "Language Clicked")

                            },
                            onPremiumClick = {
                                analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnGoPremium_SettingsScreen")
                                Logger.i("MainActivity", "Go Premium Clicked")
                                scope.launch {
                                    customBottomSheetState.hide()
                                }
                                showSettingsDialog = false

                                navController.navigate(Page.Premium) {
                                }

                            },
                            onShareApp = {
                                analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnShareApp_SettingsScreen")
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
                                analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnRateUs_SettingsScreen")
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
                                analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnPrivacyPolicy_SettingsScreen")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(UrlFactory.PRIVACY_POLICY_URL))
                                startActivity(intent)
                            },
                            appVersion = appVersion
                        )
                    }
                }

                if (showChangeThemeDialog) {
                    analyticsManager.sendAnalytics(AnalyticsConstants.OPENED, "changeThemeDialog")
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

                            adsManager.showInterstitial(this@MainActivity, true) { isAdShown -> }
                            when (themeIndex) {
                                0 -> {
                                    analyticsManager.sendAnalytics(AnalyticsConstants.SELECTED, "themeSystem")
                                    val systemDarkMode = isSystemInDarkMode()
                                    enableDarkMode(systemDarkMode)
                                    darkMode = systemDarkMode
                                    selectedTheme = 0

                                }

                                1 -> {
                                    analyticsManager.sendAnalytics(AnalyticsConstants.SELECTED, "themeLight")
                                    enableDarkMode(false)
                                    darkMode = false
                                    selectedTheme = 1
                                }

                                2 -> {
                                    analyticsManager.sendAnalytics(AnalyticsConstants.SELECTED, "themeDark")
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

                if (showExitDialog) {
                    analyticsManager.sendAnalytics(AnalyticsConstants.OPENED, "exitDialog")
                    ModalBottomSheet(
                        onDismissRequest = {
                            scope.launch {
                                exitBottomSheetState.hide()
                            }
                            viewModel.hideExitDialog()
                        },
                        containerColor = colors.background,
                        sheetState = exitBottomSheetState
                    ) {
                        ExitDialog(
                            isPremium = isPremium,
                            onCancelClick = {
                                analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnNo_ExitDialog")
                                scope.launch {
                                    exitBottomSheetState.hide()
                                }
                                viewModel.hideExitDialog()
                            },
                            onExitClick = {
                                analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnYes_ExitDialog")
                                scope.launch {
                                    exitBottomSheetState.hide()
                                }
                                viewModel.hideExitDialog()
                                finishAffinity()
                            }
                        )

                    }
                }
            }
            onBackPressedDispatcher.addCallback(
                this, // LifecycleOwner
                object : androidx.activity.OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        Logger.d(TAG, "Back pressed, isHomeScreen: $isHomeScreen")
                        analyticsManager.sendAnalytics(AnalyticsConstants.CLICKED, "btnBackPress")
                        if (isHomeScreen && isOnHomeTab.value) {
                            viewModel.showExitDialog()
                        } else {
                            isEnabled = false
                            onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
            )
        }

//        initConsent()
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

    /*@Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isHomeScreen && isOnHomeTab.value) {
            Logger.d(TAG, "Back pressed on Home Screen, showing exit dialog")
            // Use runOnUiThread to ensure UI updates happen on the main thread
            lifecycleScope.launch {
                showExitDialog = true
            }
        } else {
            Logger.d(TAG, "Back pressed on non-Home Screen, going back")
            super.onBackPressed()
        }
    }*/


    private fun initConsent() {
        val canRequestAds: Boolean = adsConsentManager.canRequestAds

        if (canRequestAds != null && !canRequestAds && preferencesHelper.getBoolean(
                AdsConstants.IS_NO_ADS_ENABLED,
                false
            ) == false
        ) {

            adsConsentManager.canRequestAds.apply {
                if (this == false) {
                    adsConsentManager.showGDPRConsent(
                        this@MainActivity,
                        true
                    ) { consentError ->

                        if (consentError != null) {
                            Logger.e(
                                TAG,
                                "Error during consent gathering: ${consentError.message}"
                            )
                        }
                        Logger.d(TAG, "Consent gathering complete")
                        //Can request ads

                    }
                } else {
                    Logger.d(TAG, "Consent already gathered")
                    //can request ads
                }
            }
        } else {
            Log.d(TAG, "Ads can be requested or user is premium")
            //can request ads
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

