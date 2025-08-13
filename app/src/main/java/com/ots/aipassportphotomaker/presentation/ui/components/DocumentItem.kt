package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.ots.aipassportphotomaker.common.ext.ImageSize
import com.ots.aipassportphotomaker.common.ext.toPX
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom100
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400
import kotlinx.coroutines.delay

// Created by amanullah on 12/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun DocumentItem(
    document: DocumentListItem.Document,
    imageSize: ImageSize,
    itemVisible: Boolean,
    onDocumentClick: (documentId: Int) -> Unit = {},
    modifier: Modifier = Modifier
) {

    var scale by remember { mutableFloatStateOf(0.70f) }
    val animatedScale by animateFloatAsState(targetValue = scale, label = "FloatAnimation")

    LaunchedEffect(itemVisible) {
        if (itemVisible) {
            delay(100)
            scale = 1f
        } else {
            scale = 0.70f
        }
    }

    Column(
        modifier = modifier
            .aspectRatio(0.8f) // 0.8:1 ratio (width:height)
            .background(colors.custom400, RoundedCornerShape(8.dp))
            .border(1.dp, colors.custom100, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(document.image)
                .scale(Scale.FILL)
                .size(imageSize.width.toPX(), imageSize.height.toPX())
                .build(),
            loading = { DocumentItemPlaceholder() },
            error = { DocumentItemPlaceholder() },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(3.dp)
                .aspectRatio(9 / 16f)
                .scale(animatedScale)
                .clip(RoundedCornerShape(2))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            scale = 0.90f
                            tryAwaitRelease()
                            scale = 1f
                        },
                        onTap = {
                            onDocumentClick(document.id)
                        }
                    )
                }
        )

        /*AsyncImage(
            model = imageUrl,
            contentDescription = "$name image",
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(bottom = 8.dp),
            contentScale = ContentScale.Fit,
            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery), // Placeholder
            error = painterResource(id = android.R.drawable.ic_dialog_alert) // Error
        )*/
        Text(
            text = document.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onCustom400
        )
        Text(
            text = "${document.size} — ${document.unit}",
            fontSize = 14.sp,
            color = colors.onCustom400.copy(alpha = 0.6f),
        )
    }
}

@Composable
fun Separator(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    )
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PhotoDataItemPreview() {
    PreviewContainer {
        Surface {
            val imageSize = ImageSize.getImageFixedSize()
            Column {
                Separator("Passport")
                Row {
                    DocumentItem(DocumentListItem.Document(1, "Passport", "2 X 2", "inch", "413x531 px", "300dpi", "https://i.stack.imgur.com/lDFzt.jpg", ""), imageSize, true)
                    DocumentItem(DocumentListItem.Document(1, "Passport", "2 X 2", "mm", "413x531 px", "300dpi", "https://i.stack.imgur.com/lDFzt.jpg", ""), imageSize, true)
                    DocumentItem(DocumentListItem.Document(1, "Passport", "2 X 2", "inch", "513x531 px", "300dpi", "https://i.stack.imgur.com/lDFzt.jpg", ""), imageSize, true)
                }
            }
        }
    }
}