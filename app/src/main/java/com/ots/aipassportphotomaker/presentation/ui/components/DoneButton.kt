package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400

// Created by amanullah on 10/10/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun DoneButton(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.done),
    onDoneClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .clickable { onDoneClick() },
        color = colors.primary,
        shape = RoundedCornerShape(32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.tick_icon),
                contentDescription = "Done",
                tint = colors.onPrimary,
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = 12.dp)
                    .align(Alignment.CenterVertically)
            )

            Text(
                text = text,
                modifier = Modifier
                    .animateContentSize()
                    .padding(start = 4.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onPrimary
            )
        }

    }

}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DoneButtonPreview(modifier: Modifier = Modifier) {
    PreviewContainer {
        DoneButton(
            modifier = modifier,
            onDoneClick = {}
        )
    }
}