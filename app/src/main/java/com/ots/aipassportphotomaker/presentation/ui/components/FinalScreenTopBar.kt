package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.R

// Created by amanullah on 21/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalScreenTopBar(
    title: String,
    showGetProButton: Boolean = true,
    onBackClick: () -> Unit,
    onGetProClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.check_circle),
                    contentDescription = "Success",
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        },
        /*navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Back"
                )
            }
        },*/
        actions = {
            Row {

                if (showGetProButton)
                    GetProButton {
                        onGetProClick()
                    }

                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.delete_icon),
                    contentDescription = "Success",
                    modifier = Modifier
                        .clickable(onClick = {
                            onDeleteClick()
                        })
                )

                Spacer(modifier = Modifier.width(4.dp))
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colors.background
        )
    )
}

@Preview("Light")
@Preview("Dark", uiMode =  Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FinalScreenBarPreview() {
    PreviewContainer {
        FinalScreenTopBar(
            title = "Image Saved!",
            onBackClick = {},
            onGetProClick = {},
            onDeleteClick = {}
        )
    }
    
}