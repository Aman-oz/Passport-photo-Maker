package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400

// Created by amanullah on 11/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun MyTabIndicator(
    indicatorWidth: Dp,
    indicatorOffset: Dp,
    indicatorColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(width = indicatorWidth)
            .offset(x = indicatorOffset)
            .clip(shape = CircleShape,)
            .background(color = indicatorColor)
    )
    
}

@Composable
fun MyTabItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    tabWidth: Dp,
    id: Int
) {
    val tabIconColor: Color by animateColorAsState(targetValue =
    if (isSelected) {
        colors.primary
    } else {
        colors.onBackground
    },
        animationSpec = tween(easing = LinearEasing)
    )

    Icon(
        painter = painterResource(id = id),
        contentDescription = "Eye Dropper Icon",
        tint = tabIconColor,
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(tabWidth)
            .padding(
                vertical = 8.dp,
                horizontal = 12.dp,
            )
    )
}

@Composable
fun CustomTab(
    selectedItemIndex: Int,
    items: List<Int>,
    modifier: Modifier = Modifier,
    tabWidth: Dp = 100.dp,
    onClick: (index: Int) -> Unit,
) {
    val indicatorOffset: Dp by animateDpAsState(
        targetValue = tabWidth * selectedItemIndex,
        animationSpec = tween(easing = LinearEasing),
    )

    Box(
        modifier = modifier
            .background(color = colors.background)
            .height(intrinsicSize = IntrinsicSize.Min),
    ) {
        MyTabIndicator(
            indicatorWidth = tabWidth,
            indicatorOffset = indicatorOffset,
            indicatorColor = colors.custom400,
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.clip(CircleShape),
        ) {
            items.mapIndexed { index, id ->
                val isSelected = index == selectedItemIndex
                MyTabItem(
                    isSelected = isSelected,
                    onClick = {
                        onClick(index)
                    },
                    tabWidth = tabWidth,
                    id = id,
                )
            }
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MyCustomTabPreview() {
    PreviewContainer {
        val tabItems = listOf(R.drawable.ai_tab_item_icon, R.drawable.eraser_icon, R.drawable.brush_tab_item_icon, R.drawable.tick_icon)
        var selectedIndex by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0) }
        CustomTab(
            selectedItemIndex = selectedIndex,
            items = tabItems,
            modifier = Modifier.padding(16.dp),
            tabWidth = 100.dp,
            onClick = { index ->
                selectedIndex = index
            }
        )
    }
}