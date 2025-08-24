package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom300

@Composable
fun BackgroundView(
    modifier: Modifier = Modifier,
    border: Boolean = true,
    radius: Dp = 8.dp,
) {
    Box(
        modifier = modifier
            .then(
                if (border) {
                    Modifier
                        .border(
                            width = 1.dp,
                            color = colors.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                } else {
                    Modifier
                }
            )
            .background(
                color = colors.custom300,
                shape = RoundedCornerShape(radius)
            )
    )

}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BackgroundViewPreview() {
    PreviewContainer {
        BackgroundView(
            modifier = Modifier
                .background(color = colors.custom300)
                .padding(16.dp)
                .then(Modifier)
                .fillMaxWidth()
                .aspectRatio(1f),
            border = true
        )
    }
}