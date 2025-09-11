package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

// Created by amanullah on 03/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun ImageWithLottieScan(
    modifier: Modifier = Modifier,
    isPortrait: Boolean = true,
    imagePath: String? = null,
    onTap: () -> Unit
) {

// Load Lottie composition from raw resource
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.layer_scan_image))

    Box(
        modifier = modifier
            .width(250.dp)
            .aspectRatio(if (isPortrait) 0.75f else 1f) // Adjust aspect ratio based on orientation
            .clipToBounds()
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
            model = ImageRequest.Builder(LocalContext.current)
                .data(imagePath)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.transparent_bg)

        )
        // Image as the background
        /*Image(
            painter = painterResource(id = R.drawable.scan_image_male),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )*/

        // Lottie animation overlaid on the image
        LottieAnimation(
            composition = composition,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center) // Center the Lottie over the image
                .clip(RoundedCornerShape(16.dp)), // Maintain aspect ratio (adjust based on your Lottie)
            iterations = LottieConstants.IterateForever, // Loop the animation
            contentScale = ContentScale.Crop
        )
    }

}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ImageWithLottieScanPreview() {

    PreviewContainer {

        Row {
            ImageWithLottieScan(
                modifier = Modifier,
                isPortrait = true,
                onTap = {}
            )

            Spacer(modifier = Modifier.width(8.dp))

            ImageWithLottieScan(
                modifier = Modifier,
                isPortrait = false,
                onTap = {}
            )
        }

    }
}