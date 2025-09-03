package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom100

// Created by amanullah on 03/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun PremiumButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val gradientBrush = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF4D50FB), // Start: #4D50FB (0% - 33%)
            0.33f to Color(0xFFEF15AA), // Mid: #EF15AA (33% - 66%)
            0.66f to Color(0xFFFA7451)  // End: #FA7451 (66% - 100%)
        ),
        start = Offset(0f, 0f), // Left
        end = Offset(300f, 0f)  // Right (adjust based on button width)
    )

    val horizontalGradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4D50FB),
            Color(0xFFEF15AA),
            Color(0xFFFA7451)
        )
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .background(brush = horizontalGradientBrush, shape = RoundedCornerShape(10.dp)), // Apply gradient background
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, // Remove default background
                contentColor = colors.onPrimary // Ensure text/icon contrast
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.crown_icon),
                    contentDescription = "Premium Icon",
                    tint = colors.onPrimary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = "HD (1080)",
                    color = Color.White,
                )
            }
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PremiumButtonPreview() {
    PreviewContainer {
        PremiumButton(
            modifier = Modifier
                .padding(16.dp),
            onClick = {}
        )
    }

}