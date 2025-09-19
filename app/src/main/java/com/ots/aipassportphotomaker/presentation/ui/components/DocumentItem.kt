package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.ext.ImageSize
import com.ots.aipassportphotomaker.common.ext.toPX
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
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
    modifier: Modifier = Modifier,
    document: DocumentListItem.Document,
    imageSize: ImageSize,
    itemVisible: Boolean,
    onDocumentClick: (documentId: Int) -> Unit = {}
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
            .background(colors.custom400, RoundedCornerShape(8.dp))
            .border(1.dp, colors.custom100, RoundedCornerShape(8.dp))
            .padding(8.dp)
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
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val defaultDrawableRes = when (document.type) {
            "Visa" -> R.drawable.default_visa_icon
            "Standard" -> R.drawable.default_standard_icon
            "Driver's License" -> R.drawable.default_drivers_icon
            "Resident Card" -> R.drawable.default_resident_card_icon
            "Profile" -> {
                when(document.name) {
                    "Facebook" -> R.drawable.facebook_default_icon
                    "Instagram" -> R.drawable.insta_default_icon
                    "Twitter" -> R.drawable.x_icon
                    "LinkedIn" -> R.drawable.linkdin_icon
                    "YouTube" -> R.drawable.youtube_icon
                    "TikTok" -> R.drawable.tiktok_icon
                    "Pinterest" -> R.drawable.pinterest_icon
                    "Snapchat" -> android.R.drawable.ic_menu_report_image
                    "Google" -> R.drawable.google_icon
                    "WhatsApp" -> R.drawable.whatsapp_icons
                    "Discord" -> R.drawable.discord_icon
                    else -> R.drawable.default_visa_icon
                }
            }
            "Passport" -> {
                R.drawable.passport_united_state
            } // Assuming you have this
            else -> R.drawable.default_standard_icon // Fallback
        }

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(when {
                    document.image.isNullOrEmpty() -> defaultDrawableRes
                    !document.image.startsWith("http") -> defaultDrawableRes
                    else -> document.image
                })
                .scale(Scale.FILL)
                .size(imageSize.width.toPX(), imageSize.height.toPX())
                .build(),
            loading = { DocumentItemPlaceholder() },
            error = { DocumentItemPlaceholder() },
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .weight(1f)
                .padding(3.dp)
                .aspectRatio(1f)
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

        Column(
            modifier = Modifier
                .weight(1f) // Takes up the other half
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = document.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.onCustom400,
                textAlign = TextAlign.Center,
                maxLines = 2, // Allow wrapping for long names
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${document.size} — ${document.unit}",
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun Separator(
    type: String,
    isSeeAllVisible: Boolean = true,
    onSeeAllClick: ((String) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = type,
            color = colors.onBackground,
            style = MaterialTheme.typography.titleMedium
        )
        if (!isSeeAllVisible) return@Row

        Text(
            text = stringResource(id = R.string.see_all),
            color = colors.primary,
            style = MaterialTheme.typography.labelLarge.copy(
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier
                .padding(start = 8.dp)
                .then(
                    if (onSeeAllClick != null) {
                        Modifier.clickable { onSeeAllClick(type) }
                    } else {
                        Modifier
                    }
                )
        )
    }
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
                    DocumentItem(
                        document = DocumentListItem.Document(
                            id = 2,
                            name = "Canada Passport",
                            size = "50.0 x 70.0",
                            unit = "mm",
                            pixels = "1181x1653 px",
                            resolution = "300 dpi",
                            image = "https://i.stack.imgur.com/lDFzt.jpg",
                            type = "Passport"
                        ),
                        imageSize = imageSize,
                        itemVisible = true
                    )
                    DocumentItem(
                        document = DocumentListItem.Document(
                            id = 3,
                            name = "United Kingdom Passport",
                            size = "35.0 x 45.0",
                            unit = "mm",
                            pixels = "413x531 px",
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