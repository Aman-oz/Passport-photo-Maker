package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

// Created by amanullah on 11/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun ChooseOrPickImage(
    modifier: Modifier = Modifier,
    context: Context,
    onChooseImage: () -> Unit,
    onCameraImage: () -> Unit,
    isChooseEnabled: Boolean = true,
    isPickEnabled: Boolean = true
) {

    // Click handlers
    val onCameraClick: () -> Unit = {
        if (isPickEnabled) {
            onCameraImage()
        }
    }
    val onGalleryClick: () -> Unit = {
        if (isChooseEnabled) {
            onChooseImage()
        }
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.primary)
            .aspectRatio(5.06F),

        verticalAlignment = Alignment.CenterVertically
    ) {
        // Camera icon at the start
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clickable(enabled = isPickEnabled, onClick = onCameraClick)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.camera_icon),
                contentDescription = "Camera",
                tint = colors.onPrimary
            )
        }

        // Divider
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight(),
            thickness = 2.dp,
            color = colors.onPrimary.copy(alpha = 0.4f)
        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .clickable(enabled = isChooseEnabled, onClick = onGalleryClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {

            Text(
                text = stringResource(R.string.choose_image_to_create_id),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onPrimary,
                fontSize = 16.sp
            )

            // Sparkle icon at the end
            Icon(
                painter = painterResource(id = R.drawable.sparkle_1),
                contentDescription = "Sparkle",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd),
                tint = colors.onPrimary.copy(alpha = 0.5f)
            )
        }
    }
}

fun createImageUri(context: Context): Uri {
    val contentValues = android.content.ContentValues().apply {
        put(android.provider.MediaStore.Images.Media.TITLE, "Photo")
        put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return context.contentResolver.insert(
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ) ?: throw IllegalStateException("Failed to create image URI")
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChooseOrPickImagePreview() {
    PreviewContainer {
        ChooseOrPickImage(
            context = LocalContext.current,
            onChooseImage = {},
            onCameraImage = {},
            isChooseEnabled = true,
            isPickEnabled = true
        )
    }
}