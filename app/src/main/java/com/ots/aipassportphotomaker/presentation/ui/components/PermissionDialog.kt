package com.ots.aipassportphotomaker.presentation.ui.components

// Created by amanullah on 15/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.permission_required),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Text(
                text = permissionTextProvider.getDescription(isPermanentlyDeclined = isPermanentlyDeclined)
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isPermanentlyDeclined) {
                        onGoToAppSettingsClick()
                    } else {
                        onOkClick()
                    }
                }
            ) {
                Text(
                    text = if (isPermanentlyDeclined) stringResource(R.string.grant_permission) else stringResource(
                        R.string.ok
                    ),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.cancel),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth())
            }
        },
        modifier = modifier
    )
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class NotificationPermissionTextProvider(private val context: Context) : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            context.getString(R.string.it_seems_you_permanently_declined_notification_permission)
        } else {
            context.getString(R.string.this_app_needs_access_to_send_you_notifications_so_that_you_can_stay_updated_with_the_latest_news_and_updates)
        }
    }
}

class CameraPermissionTextProvider(private val context: Context) : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            context.getString(R.string.it_seems_you_permanently_declined_camera_permission_you_can_go_to_the_app_settings_to_grant_it)
        } else {
            context.getString(R.string.this_app_needs_access_to_your_camera_so_that_you_can_tak_picture_for_photo_id)
        }
    }
}

class StoragePermissionTextProvider(private val context: Context) : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            context.getString(R.string.it_seems_you_permanently_declined_storage_permission_you_can_go_to_the_app_settings_to_grant_it)
        } else {
            context.getString(R.string.this_app_needs_access_to_your_device_storage_so_that_you_can_select_and_save_photos)
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PermissionDialogPreview() {
    val context = LocalContext.current
    PreviewContainer {
        PermissionDialog(
            permissionTextProvider = CameraPermissionTextProvider(context = context),
            isPermanentlyDeclined = false,
            onDismiss = {},
            onOkClick = {},
            onGoToAppSettingsClick = {}
        )
    }
}