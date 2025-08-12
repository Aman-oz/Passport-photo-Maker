package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom100
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400

// Created by amanullah on 12/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun PhotoDataItem(
    name: String,
    size: String,
    unit: String,
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(0.8f) // 0.8:1 ratio (width:height)
            .background(colors.custom400, RoundedCornerShape(8.dp))
            .border(1.dp, colors.custom100, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SubcomposeAsyncImage(
            model = imageUrl,
            loading = { PhotoDataItemPlaceholder() },
            error = { PhotoDataItemPlaceholder() },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(bottom = 8.dp),
        )
        /*AsyncImage(
            model = imageUrl,
            contentDescription = "$name image",
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(bottom = 8.dp),
            contentScale = ContentScale.Fit,
            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery), // Placeholder
            error = painterResource(id = android.R.drawable.ic_dialog_alert) // Error
        )*/
        Text(
            text = name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onCustom400
        )
        Text(
            text = "$size — $unit",
            fontSize = 14.sp,
            color = colors.onCustom400.copy(alpha = 0.6f),
        )
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PhotoDataItemPreview() {
    PreviewContainer {
        PhotoDataItem(
            name = "Sample Photo",
            size = "1024x768",
            unit = "pixels",
            imageUrl = "https://lh3.googleusercontent.com/d/1-6TCzKDOOcY-rVFqkBCd5ScOfgvcX7BK",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}