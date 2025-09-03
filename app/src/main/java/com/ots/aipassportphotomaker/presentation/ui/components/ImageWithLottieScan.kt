package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ots.aipassportphotomaker.R
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

// Created by amanullah on 03/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun ImageWithLottieScan(
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {

// Load Lottie composition from raw resource
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.layer_scan_image))

    Box(
        modifier = modifier
            .background(Color.Transparent)
            .clip(RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Image as the background
        Image(
            painter = painterResource(id = R.drawable.scan_image_male),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(color = Color.Transparent, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        )

        // Lottie animation overlaid on the image
        LottieAnimation(
            composition = composition,
            modifier = Modifier
                .align(Alignment.Center) // Center the Lottie over the image
                .fillMaxWidth() // Adjust size to fit within the image
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp)), // Maintain aspect ratio (adjust based on your Lottie)
            iterations = LottieConstants.IterateForever // Loop the animation
        )
    }

}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ImageWithLottieScanPreview() {

    PreviewContainer {
        ImageWithLottieScan(
            modifier = Modifier
                .padding(16.dp),
            onTap = {}
        )
    }
}