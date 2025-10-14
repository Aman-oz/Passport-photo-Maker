package com.ots.aipassportphotomaker.image_picker.view

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.image_picker.model.AssetDirectory
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom300
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom300
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AssetSelectorScreen(
    directory: String,
    assetDirectories: List<AssetDirectory>,
    navigateUp: () -> Unit,
    onSelected: (String) -> Unit,
) {
    Scaffold(
        modifier = Modifier.background(colors.background),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .statusBarsPadding()
                    .background(colors.background),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
                navigationIcon = {

                    IconButton(onClick = navigateUp) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = ""
                        )
                    }
                },
                title = {
                    Row(modifier = Modifier.clickable(onClick = navigateUp)) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = directory,
                            color = colors.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(colors.custom300)
                                .clickable(onClick = navigateUp),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                painter = painterResource(R.drawable.chevron_down),
                                contentDescription = ""
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .background(colors.background)
                .padding(padding)
        ) {
            items(items = assetDirectories) {
                val itemDirectory = it.directory
                ListItem(
                    modifier = Modifier
                        .background(colors.background)
                        .clickable { onSelected(itemDirectory) },
                    leadingContent = {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it.cover)
                                .build(),
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.onBackground, shape = RoundedCornerShape(6.dp))
                                .aspectRatio(1.0F),
                            filterQuality = FilterQuality.Low,
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )
                    },
                    headlineContent = {

                        Column {
                            Text(
                                text = itemDirectory,
                                color = colors.onCustom400,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row {
                                Text(
                                    text = "${it.counts}",
                                    color = colors.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.images),
                                    color = colors.outline,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                        }
                    },
                    trailingContent = {
                        if (directory == itemDirectory) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(colors.custom300)
                                    .clickable(onClick = navigateUp),
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    painter = painterResource(R.drawable.tick_icon),
                                    contentDescription = "",
                                    tint = colors.onCustom300
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SelectedItemTickPreview() {
    PreviewContainer {
        Box(
            modifier = Modifier
                .size(44.dp)
                .padding(4.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(colors.custom300)
                .clickable(onClick = {}),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = painterResource(R.drawable.tick_icon),
                contentDescription = "",
                tint = colors.onCustom300
            )
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AssetSelectorScreenPreview() {
    PreviewContainer {
        AssetSelectorScreen(
            directory = "All Photos",
            assetDirectories = listOf(
                AssetDirectory("All Photos", counts = 12458, cover = null, assets = listOf()),
                AssetDirectory("Camera", counts = 2458, cover = null, assets = listOf()),
                AssetDirectory("Downloads", counts = 1458, cover = null, assets = listOf()),
                AssetDirectory("WhatsApp", counts = 458, cover = null, assets = listOf()),
                AssetDirectory("Instagram", counts = 2458, cover = null, assets = listOf()),
                AssetDirectory("Facebook", counts = 1458, cover = null, assets = listOf()),
                AssetDirectory("Telegram", counts = 458, cover = null, assets = listOf()),
            ),
            navigateUp = {},
            onSelected = {}
        )
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ImagePreview() {
    PreviewContainer {
        Image(
            imageVector = Icons.Default.DateRange,
            modifier = Modifier
                .size(40.dp)
                .background(colors.onBackground, shape = RoundedCornerShape(6.dp))
                .aspectRatio(1.0F),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TextContentPreview() {
    PreviewContainer {
        Column {
            Text(
                text = "All Photos",
                color = colors.onCustom400,
                style = MaterialTheme.typography.titleMedium
            )
            Row {
                Text(
                    text = "12458",
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Images",
                    color = colors.outline,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
    }
}
