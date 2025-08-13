package com.ots.aipassportphotomaker.presentation.ui.createid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.ots.aipassportphotomaker.common.ext.ImageSize
import com.ots.aipassportphotomaker.data.model.DocumentData
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.domain.util.JsonDataReader
import com.ots.aipassportphotomaker.presentation.ui.components.DocumentItem
import com.ots.aipassportphotomaker.presentation.ui.components.Loader
import com.ots.aipassportphotomaker.presentation.ui.components.Separator
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import kotlinx.coroutines.launch

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun DocumentList(
    documents: LazyPagingItems<DocumentListItem>,
    onDocumentClick: (documentId: Int) -> Unit,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    config: DocumentSpanSizeConfig = DocumentSpanSizeConfig(3),
) {

    val imageSize = ImageSize.getImageFixedSize()

    LazyVerticalGrid(
        modifier = Modifier.background(colors.background),
        columns = GridCells.Fixed(config.gridSpanSize),
        state = lazyGridState
    ) {
        items(documents.itemCount, span = { index ->
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

            when (val document = documents[index]) {
                is DocumentListItem.Document -> DocumentItem(
                    document = document,
                    imageSize = imageSize,
                    onDocumentClick = onDocumentClick,
                    itemVisible = itemVisible
                )

                is DocumentListItem.Separator -> Separator(document.category)
                else -> Loader()
            }
        }
    }


    /*val context = LocalContext.current
    var documentData by remember { mutableStateOf<DocumentData?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val jsonReader = JsonDataReader(context)
            documentData = jsonReader.readDocumentData()
        }
    }

    documentData?.let { data ->
        LazyColumn {
            items(data.passports) { document ->
                PhotoDataItem(
                    name = document.name,
                    size = document.size,
                    unit = document.unit,
                    imageUrl = document.image ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    } ?: run {
        Text("Loading data...")
    }*/
}

/**
 * @property gridSpanSize - The total number of columns in the grid.
 * @property separatorColumnSpanSize - Returns the number of columns that the item occupies.
 * @property footerColumnSpanSize - Returns the number of columns that the item occupies.
 **/
data class DocumentSpanSizeConfig(val gridSpanSize: Int) {
    val documentColumnSpanSize: Int = 1
    val separatorColumnSpanSize: Int = gridSpanSize
    val footerColumnSpanSize: Int = gridSpanSize
}