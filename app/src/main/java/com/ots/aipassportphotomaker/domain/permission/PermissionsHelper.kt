package com.ots.aipassportphotomaker.domain.permission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ots.aipassportphotomaker.common.utils.Logger

// Created by amanullah on 28/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class PermissionsHelper(
) {

    @Volatile
    private var activityResultLauncherPermissionActivity: ActivityResultLauncher<String>? = null
    @Volatile
    private var activityResultLauncherMultiplePermissionsActivity: ActivityResultLauncher<Array<String>>? = null

    // Permission constants
    private val CAMERA_PERMISSION = Manifest.permission.CAMERA
    private val STORAGE_PERMISSION = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES // Android 13+
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE // Android 9-12
    }

    // Check if camera permission is granted
    internal fun isCameraPermissionGranted(activity: Activity): Boolean =
        ActivityCompat.checkSelfPermission(activity, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED

    internal fun isCameraPermissionGranted(fragment: Fragment): Boolean =
        fragment.context?.checkSelfPermission(CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED

    // Check if storage permission is granted
    internal fun isStoragePermissionGranted(activity: Activity): Boolean =
        ActivityCompat.checkSelfPermission(activity, STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED

    internal fun isStoragePermissionGranted(fragment: Fragment): Boolean =
        fragment.context?.checkSelfPermission(STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED

    // Initialize single permission request for Activity
    internal fun initRegisterForRequestPermissionInActivity(
        activity: ComponentActivity,
        onPermissionGranted: (permission: String) -> Unit,
        onPermissionDenied: (permission: String) -> Unit = {}
    ) {
        if (activityResultLauncherPermissionActivity == null) {
            activityResultLauncherPermissionActivity = activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                val permission = if (ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)) {
                    CAMERA_PERMISSION // Default to camera
                } else {
                    CAMERA_PERMISSION // Simplify for now; enhance if tracking multiple permissions
                }
                if (isGranted) {
                    onPermissionGranted(permission)
                } else {
                    onPermissionDenied(permission)
                    Logger.w("PermissionsHelper", "Permission denied: $permission")
                }
            }
        }
    }

    // Initialize multiple permissions request for Activity
    internal fun initRegisterForRequestMultiplePermissionsInActivity(
        activity: ComponentActivity,
        onPermissionsGranted: (Map<String, Boolean>) -> Unit,
        onPermissionsDenied: (Map<String, Boolean>) -> Unit = {}
    ) {
        if (activityResultLauncherMultiplePermissionsActivity == null) {
            activityResultLauncherMultiplePermissionsActivity = activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val allGranted = permissions.all { it.value }
                if (allGranted) {
                    onPermissionsGranted(permissions)
                } else {
                    onPermissionsDenied(permissions)
                    Logger.w("PermissionsHelper", "Some permissions denied: $permissions")
                }
            }
        }
    }

    // Request specific permission
    internal fun requestCameraPermission(activity: Activity) {
        if (activityResultLauncherPermissionActivity != null) {
            activityResultLauncherPermissionActivity?.launch(CAMERA_PERMISSION)
        } else {
            Logger.e("PermissionsHelper", "Launcher not initialized for camera permission")
        }
    }

    internal fun requestStoragePermission(activity: Activity) {
        if (activityResultLauncherPermissionActivity != null) {
            activityResultLauncherPermissionActivity?.launch(STORAGE_PERMISSION)
        } else {
            Logger.e("PermissionsHelper", "Launcher not initialized for storage permission")
        }
    }

    // Request both permissions
    internal fun requestAllPermissions(activity: Activity) {
        if (activityResultLauncherMultiplePermissionsActivity != null) {
            activityResultLauncherMultiplePermissionsActivity?.launch(arrayOf(CAMERA_PERMISSION, STORAGE_PERMISSION))
        } else {
            Logger.e("PermissionsHelper", "Launcher not initialized for multiple permissions")
        }
    }

    // Check if launcher is initialized
    internal fun isLauncherInitialized(): Boolean =
        activityResultLauncherPermissionActivity != null || activityResultLauncherMultiplePermissionsActivity != null
}