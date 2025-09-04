package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun ImageEditorView(
    modifier: Modifier = Modifier,
    imagePath: String? = null,
    onTap: () -> Unit
) {

// Load Lottie composition from raw resource
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.layer_scan_image))

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // assign image path to Image composable if available else use placeholder

        AsyncImage(
            model = imagePath,
            contentDescription = null,
            modifier = Modifier
                .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            placeholder = painterResource(id = R.drawable.scan_image_male)

        )
    }

}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ImageEditorViewPreview() {

    PreviewContainer {

        ImageEditorView(
            modifier = Modifier,
            onTap = {}
        )

    }
}