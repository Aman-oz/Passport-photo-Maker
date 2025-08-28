package com.ots.aipassportphotomaker.presentation.ui.main

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.farimarwat.permissionmate.PMate
import com.farimarwat.permissionmate.rememberPermissionMateState
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.common.utils.SharedPrefUtils
import com.ots.aipassportphotomaker.di.AppSettingsSharedPreference
import com.ots.aipassportphotomaker.domain.permission.PermissionsHelper
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.presentation.ui.components.NoInternetConnectionBanner
import com.ots.aipassportphotomaker.presentation.ui.theme.AIPassportPhotoMakerTheme
import com.ots.aipassportphotomaker.presentation.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
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

    private fun isDarkModeEnabled() = appSettings.getBoolean(SharedPrefUtils.DARK_MODE, false)

    private fun enableDarkMode(enable: Boolean) = appSettings.edit().putBoolean(SharedPrefUtils.DARK_MODE, enable).commit()

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

            AppTheme(darkMode) {
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
                        onThemeUpdated = {
                            val updated = !darkMode
                            enableDarkMode(updated)
                            darkMode = updated
                        }
                    )


                }
            }
        }
    }
}