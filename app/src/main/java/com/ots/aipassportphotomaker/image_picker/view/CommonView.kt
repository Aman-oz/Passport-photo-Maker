package com.ots.aipassportphotomaker.image_picker.view

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.image_picker.model.AssetInfo
import com.ots.aipassportphotomaker.image_picker.model.AssetPickerConfig
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400
import kotlin.text.clear

internal val LocalAssetConfig = compositionLocalOf { AssetPickerConfig() }

@Composable
internal fun AppBarButton(size: Int, onPicked: () -> Unit) {
    val maxAssets = LocalAssetConfig.current.maxAssets
    val buttonText = stringResource(R.string.text_select_button, size, maxAssets)
    Button(
        modifier = Modifier.defaultMinSize(minHeight = 1.dp, minWidth = 1.dp),
        shape = RoundedCornerShape(5.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
        onClick = onPicked,
    ) {
        Text(buttonText, fontSize = 14.sp, color = Color.White)
    }
}

@Composable
fun AssetImageIndicator(
    assetInfo: AssetInfo,
    selected: Boolean,
    size: Dp = 24.dp,
    fontSize: TextUnit = 16.sp,
    assetSelected: SnapshotStateList<AssetInfo>,
    onClicks: ((Boolean) -> Unit)? = null,
) {
    val context = LocalContext.current
    val maxAssets = LocalAssetConfig.current.maxAssets
    val errorMessage = stringResource(R.string.message_selected_exceed, maxAssets)

    val checkBoxIcon = if (selected) {
        R.drawable.gallery_checked
    } else {
        R.drawable.gallery_unchecked
    }

    val (border, color) = if (selected) {
        Pair(null, Color(64, 151, 246))
    } else {
        Pair(BorderStroke(width = 1.dp, color = Color.White), Color.Black.copy(alpha = 0.3F))
    }
    Box(
        modifier = Modifier
//            .size(size)
//            .then(if (border == null) Modifier else Modifier.border(border, shape = CircleShape))
//            .background(color = color, shape = CircleShape)
            .clip(shape = CircleShape)
            .clickable {
                val isSelected = !selected
                if (onClicks != null) {
                    onClicks(isSelected)
                    return@clickable
                }

                if (isSelected) {
                    assetSelected.clear()
                    assetSelected.add(assetInfo)
                } else {
                    assetSelected.clear()
                }

                /*if (assetSelected.size == maxAssets && isSelected) {
                    Toast
                        .makeText(context, errorMessage, Toast.LENGTH_SHORT)
                        .show()
                    return@clickable
                }
                if (isSelected) assetSelected.add(assetInfo) else assetSelected.remove(assetInfo)*/
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = checkBoxIcon
            ),
            contentDescription = null
        )
        /*if (selected) {

            Text(
                text = "${assetSelected.indexOf(assetInfo) + 1}",
                color = Color.White,
                fontSize = fontSize,
            )
        }*/
    }
}


@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AssetImageIndicatorPreview() {
    PreviewContainer {
        val (border, color) = if (false) {
            Pair(null, Color(64, 151, 246))
        } else {
            Pair(BorderStroke(width = 1.dp, color = Color.White), Color.Black.copy(alpha = 0.3F))
        }
        val checkBoxIcon = if (false) {
            R.drawable.gallery_checked
        } else {
            R.drawable.gallery_unchecked
        }


        Box(
            modifier = Modifier

                .clip(shape = CircleShape)
//                .padding(6.dp)
//            .then(if (border == null) Modifier else Modifier.border(border, shape = CircleShape))
            /*.background(color = color, shape = CircleShape)*/,

            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(
                    id = checkBoxIcon
                ),
                contentDescription = null
            )
            /*Icon(
                modifier = Modifier,
                painter = painterResource(id = checkBoxIcon),
                contentDescription = null,
            )*/
            /*if (selected) {

                Text(
                    text = "${assetSelected.indexOf(assetInfo) + 1}",
                    color = Color.White,
                    fontSize = fontSize,
                )
            }*/
        }
    }
}