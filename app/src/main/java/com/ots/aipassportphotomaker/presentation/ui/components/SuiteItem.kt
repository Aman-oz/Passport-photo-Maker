package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import coil.compose.AsyncImagePainter
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.domain.model.SuitsEntity
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom100

// Created by amanullah on 10/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun SuitItem(
    suit: SuitsEntity,
    onItemClick: (SuitsEntity) -> Unit,
    isSelected: Boolean,
    onSelectionChange: (SuitsEntity, Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .width(80.dp) // Fixed width for horizontal layout
            .aspectRatio(1f)
            .padding(2.dp)
            .clickable {
                onItemClick(suit)
                onSelectionChange(suit, !isSelected)
                       },
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) colors.primary else colors.custom100
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        var isSuitLoading by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colors.background),
            contentAlignment = Alignment.Center
        ) {
            val isNoneSuit by remember { mutableStateOf(suit.name.equals("none", ignoreCase = true)) }
            val modifier = if (isNoneSuit) {
                Modifier
                    .padding(16.dp)
                    .matchParentSize()
                    .clip(RoundedCornerShape(8.dp))
            } else {
                Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(8.dp))
            }
            // Thumbnail image
            AsyncImage(
                model = suit.thumbnail,
                contentDescription = "Suit Thumbnail: ${suit.name}",
                modifier = modifier,
                contentScale = ContentScale.Crop,
                onState = { state ->
                    isSuitLoading = state is AsyncImagePainter.State.Loading
                }
            )

            if (isSuitLoading ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp),
                    color = colors.primary
                )
            }

            if (suit.isPremium && !isNoneSuit) {
                Badge(
                    containerColor = Color.Red.copy(alpha = 0.8f),
                    contentColor = Color.White,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        .align(Alignment.BottomCenter),

                ) {
                    Text("Premium", fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SuitItemPreview() {
    PreviewContainer {
        SuitItem(
            suit = SuitsEntity(
                _id = "1",
                name = "Classic Suit",
                image = "https://example.com/suit1.png",
                thumbnail = "https://example.com/suit1_thumb.png",
                isPremium = true
            ),
            onItemClick = {},
            isSelected = true,
            onSelectionChange = { _, _ -> }
        )
    }
}