package com.ots.aipassportphotomaker.presentation.ui.createid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.ots.aipassportphotomaker.common.ext.ImageSize
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.presentation.ui.components.DocumentItem
import com.ots.aipassportphotomaker.presentation.ui.components.Loader
import com.ots.aipassportphotomaker.presentation.ui.components.Separator
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

// Created by amanullah on 21/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun DocumentDetailList(
    documents: LazyPagingItems<DocumentListItem>,
    onDocumentClick: (documentId: Int) -> Unit,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    config: DocumentSpanSizeConfig = DocumentSpanSizeConfig(2),
) {

    val imageSize = ImageSize.getImageFixedSize()

    LazyVerticalGrid(
        modifier = Modifier.background(colors.background),
        columns = GridCells.Fixed(config.gridSpanSize),
        state = lazyGridState,
    ) {
        items(documents.itemCount, span = { index ->
            Logger.d("DocumentList", "index: $index, item: ${documents[index]}")
            val spinSize = when (documents[index]) {
                is DocumentListItem.Document -> config.documentColumnSpanSize
                is DocumentListItem.Separator -> config.separatorColumnSpanSize
                null -> config.footerColumnSpanSize
            }
            GridItemSpan(spinSize)
        }) { index ->

            val itemVisible by remember {
                derivedStateOf {
                    val visibleItems = lazyGridState.layoutInfo.visibleItemsInfo
                    visibleItems.any { it.index == index }
                }
            }

            // Add padding to each item for spacing
            val itemModifier = Modifier
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .aspectRatio(1f)

            when (val document = documents[index]) {
                is DocumentListItem.Document -> DocumentItem(
                    modifier = itemModifier,
                    document = document,
                    imageSize = imageSize,
                    onDocumentClick = onDocumentClick,
                    itemVisible = itemVisible
                )

                is DocumentListItem.Separator -> Separator(document.type, false)
                else -> Loader()
            }
        }
    }
}