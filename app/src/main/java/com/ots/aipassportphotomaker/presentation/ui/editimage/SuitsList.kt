package com.ots.aipassportphotomaker.presentation.ui.editimage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.domain.model.SuitsEntity
import com.ots.aipassportphotomaker.presentation.ui.components.SuitItem

// Created by amanullah on 10/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Composable
fun SuitsList(
    suits: LazyPagingItems<SuitsEntity>,
    onItemClick: (SuitsEntity) -> Unit,
    selectedSuitId: String, // Track selected item by _id
    onSelectionChange: (SuitsEntity, Boolean) -> Unit
) {
    val context = LocalContext.current
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {

        item {
            SuitItem(
                suit = SuitsEntity(
                    _id = "none",
                    name = "None",
                    image = "",
                    thumbnail = "android.resource://${context.packageName}/${R.drawable.none_icon}",
                    isPremium = false
                ),
                onItemClick = onItemClick,
                isSelected = selectedSuitId == "none", // Check if "none" is selected
                onSelectionChange = onSelectionChange
            )
        }

        items(count = suits.itemCount) { index ->
            val suit = suits[index]
            suit?.let {
                SuitItem(
                    suit = it,
                    onItemClick = onItemClick,
                    isSelected = selectedSuitId == suit._id, // Check if this suit is selected
                    onSelectionChange = onSelectionChange
                )
            }
        }
    }
}