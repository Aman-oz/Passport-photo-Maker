package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import android.graphics.Color
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

// Created by amanullah on 12/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun CustomElevatedButton(
    modifier: Modifier = Modifier,
    buttonText: String? = null,
    @StringRes textResId: Int? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    buttonColor: androidx.compose.ui.graphics.Color = colors.primary,
    contentColor: androidx.compose.ui.graphics.Color = colors.onPrimary,
    disabledContainerColor: androidx.compose.ui.graphics.Color = colors.surface,
    disabledContentColor: androidx.compose.ui.graphics.Color = colors.onSurface.copy(alpha = 0.38f),
    cornerRadius: Dp = 8.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    iconResId: Int? = null,
    iconContentDescription: String? = null
) {
    // Validation to ensure at least one text source is provided
    require(buttonText != null || textResId != null) { "Either buttonText or textResId must be provided" }
    require(buttonText == null || textResId == null) { "Provide either buttonText or textResId, not both" }

    ElevatedButton(
        onClick = onClick,
        modifier = modifier
            .background(colors.background),
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonColors(
            containerColor = buttonColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor
        ),
    ) {
        // Handle icon and text layout
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconResId != null && iconContentDescription != null) {
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = iconContentDescription,
                    tint = if (enabled) contentColor else disabledContentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = buttonText ?: stringResource(id = textResId!!),
                style = textStyle,
                color = if (enabled) contentColor else disabledContentColor
            )
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CustomElevatedButtonPreview() {
    PreviewContainer {
        CustomElevatedButton(
            onClick = { /* Do something */ },
            buttonText = "Click Me",
            iconContentDescription = "Button Icon",
            cornerRadius = 20.dp,
            modifier = Modifier
                .background(colors.background)
        )
    }
}