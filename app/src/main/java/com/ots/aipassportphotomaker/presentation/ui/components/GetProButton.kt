package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400

// Created by amanullah on 21/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun GetProButton(
    modifier: Modifier = Modifier,
    onGetProClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .border(
                width = 1.dp,
                color = colors.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onGetProClick() },
        color = colors.custom400,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "Get Pro",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = colors.onCustom400
        )
    }

}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GetProButtonPreview(modifier: Modifier = Modifier) {
    PreviewContainer {
        GetProButton(
            modifier = modifier,
            onGetProClick = {}
        )
    }
}