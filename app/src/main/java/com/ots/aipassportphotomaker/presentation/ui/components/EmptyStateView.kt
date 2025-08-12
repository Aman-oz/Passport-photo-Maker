package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

// Created by amanullah on 12/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun EmptyStateView (
    modifier: Modifier = Modifier,
    icon: EmptyStateIcon = EmptyStateIcon(),
    title: String? = null,
    subtitle: String? = null,
    titleTextSize: TextUnit = 22.sp,
    subtitleTextSize: TextUnit = 20.sp,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Column(
        modifier = modifier.fillMaxSize()
            .background(colors.background),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        icon.iconRes?.let {
            Image(
                modifier = Modifier.size(icon.size),
                painter = painterResource(id = it),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.padding(icon.spacing))
        title?.let {
            Text(
                text = it,
                textAlign = TextAlign.Center,
                fontSize = titleTextSize,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))

        subtitle?.let {
            Subtitle(it, subtitleTextSize)
        }

        Spacer(modifier = Modifier.padding(8.dp))

        CustomElevatedButton(
            modifier = Modifier
                .background(colors.background),
            buttonText = stringResource(R.string.create_id),
            iconContentDescription = "Button Icon",
            cornerRadius = 40.dp,
            onClick = {
                Logger.i("EmptyStateView", "Create ID Button clicked")
            },
            buttonColor = colors.primary,
            contentColor = colors.onPrimary,
            disabledContainerColor = colors.surface,
            disabledContentColor = colors.onSurface.copy(alpha = 0.38f)
        )

    }
}

data class EmptyStateIcon(
    @DrawableRes val iconRes: Int? = null,
    val size: Dp = 100.dp,
    val spacing: Dp = 0.dp,
)

@Composable
private fun Subtitle(text: String, subtitleTextSize: TextUnit) {
    Text(
        text = text,
        fontSize = subtitleTextSize,
        lineHeight = 24.sp,
        textAlign = TextAlign.Center,
    )
}

@Preview("Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyStateViewPreview() {
    PreviewContainer {
        Surface {
            EmptyStateView(
                icon = EmptyStateIcon(R.drawable.history_empty),
                title = stringResource(id = R.string.no_history_found_title),
                subtitle = stringResource(id = R.string.no_history_found_subtitle),
            )
        }
    }
}