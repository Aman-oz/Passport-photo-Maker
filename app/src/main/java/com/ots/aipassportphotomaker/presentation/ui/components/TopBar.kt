package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import android.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.AppColors
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom100
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    darkMode: Boolean,
    fontFamily: FontFamily = FontFamily.Serif             ,
    fontSize: TextUnit = 25.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    onThemeUpdated: () -> Unit,
    onGetProClick: () -> Unit
) {
    Column {
        TopAppBar(
            title = {
                /*Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    lineHeight = fontSize * 1.2f,
                    letterSpacing = 0.15.sp,
                    fontFamily = fontFamily,
                    color = colors.onBackground,
                    fontWeight = fontWeight
                )*/

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        lineHeight = fontSize * 1.2f,
                        letterSpacing = 0.15.sp,
                        fontFamily = fontFamily,
                        color = colors.onBackground,
                        fontWeight = fontWeight
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.sparkle_1),
                        contentDescription = "Sparkle 1",
                        modifier = Modifier.size(24.dp),
                        tint = colors.onBackground
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.sparkle_2),
                        contentDescription = "Sparkle 2",
                        modifier = Modifier.size(18.dp),
                    )
                }
            },
            actions = {
                /*IconButton(
                    onClick = { onSearchClick() }
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }*/

                GetProButton {
                    onGetProClick()
                }

                IconButton(
                    onClick = {
//                        onThemeUpdated()
                    }
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.settings_icon),
                        contentDescription = "Dark Mode",
                        modifier = Modifier.size(24.dp),
                        tint = colors.onBackground
                    )
                }

                IconButton(
                    onClick = { onThemeUpdated() }
                ) {
                    val icon = if (darkMode) {
                        R.drawable.baseline_light_mode_24
                    } else {
                        R.drawable.baseline_dark_mode_24
                    }
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "Dark Mode",
                        modifier = Modifier.size(24.dp),
                        tint = colors.onBackground
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colors.background,
                titleContentColor = colors.onBackground,
                actionIconContentColor = colors.onBackground
            ),
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TopBarPreview() {
    PreviewContainer {
        Column {
            TopBar("AppName", true, onThemeUpdated = {}, onGetProClick = {})
            Spacer(modifier = Modifier.padding(10.dp))
            TopBar(stringResource(id = R.string.photo_id), false, onThemeUpdated = {}, onGetProClick = {})
        }
    }
}