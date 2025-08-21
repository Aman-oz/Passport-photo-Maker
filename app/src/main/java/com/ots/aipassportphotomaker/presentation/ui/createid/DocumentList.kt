package com.ots.aipassportphotomaker.presentation.ui.createid

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.ots.aipassportphotomaker.common.ext.ImageSize
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.presentation.ui.components.DocumentItem
import com.ots.aipassportphotomaker.presentation.ui.components.Separator
import com.ots.aipassportphotomaker.presentation.ui.theme.colors


// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun DocumentList(
    documents: LazyPagingItems<DocumentListItem>,
    onDocumentClick: (documentId: Int) -> Unit,
    onSeeAllClick: (type: String) -> Unit,
) {
    val groupedDocuments = groupDocumentsByType(documents)
    val imageSize = ImageSize.getImageFixedSize()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        groupedDocuments.forEach { (type, docs) ->
            item {
                Separator(type) { selectedType ->
                    onSeeAllClick(selectedType)
                    Logger.d("DocumentList", "Separator clicked: $selectedType")

                }
            }
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(docs) { document ->
                        DocumentItem(
                            modifier = Modifier
                                .width(140.dp)   // fixed card height
                                .padding(vertical = 4.dp)
                                .aspectRatio(0.8f), // 0.8:1 ratio (width:height)
                            document = document,
                            imageSize = imageSize,
                            itemVisible = true,
                            onDocumentClick = onDocumentClick
                        )
                    }
                }
            }
        }
    }
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

private fun groupDocumentsByType(
    documents: LazyPagingItems<DocumentListItem>
): Map<String, List<DocumentListItem.Document>> {
    return (0 until documents.itemCount)
        .mapNotNull { index -> documents[index] as? DocumentListItem.Document }
        .groupBy { it.type }
}


@Preview("Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SeparatorAndMovieItemPreview() {
    PreviewContainer {
        Surface {
            val imageSize = ImageSize.getImageFixedSize()
            Column {
                Separator("Passport")
                Row {
                    DocumentItem(
                        document = DocumentListItem.Document(
                            id = 1,
                            name = "United States Passport",
                            size = "2.0 x 2.0",
                            unit = "inch",
                            pixels = "600x600 px",
                            resolution = "300 dpi",
                            image = "https://i.stack.imgur.com/lDFzt.jpg",
                            type = "Passport"
                        ),
                        imageSize = imageSize,
                        itemVisible = true
                    )
                }
            }
        }
    }
}