package com.ots.aipassportphotomaker.presentation.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.AppTheme
import com.ots.aipassportphotomaker.presentation.ui.theme.colors

@Composable
fun HomeCardItem(
    title: String,
    description: String,
    backgroundColor: Color? = null,
    textColor: Color? = null,
    backgroundImage: Int? = null,
    sparkleImage: Int? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .aspectRatio(1.78f)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        if (backgroundImage != null) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = backgroundImage),
                contentDescription = title,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier
                .matchParentSize()
                .background(backgroundColor ?: Color.LightGray))
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor ?: Color.Black
            )

            if (sparkleImage != null) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = sparkleImage),
                    contentDescription = "Sparkle",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeCardItemPreview() {
    PreviewContainer {
        HomeCardItem(
            title = "Sample Title",
            description = "This is a sample description for the HomeCardItem.",
            backgroundColor = colors.primary,
            textColor = colors.onPrimary,
            backgroundImage = R.drawable.bg_card_photo_id,
            sparkleImage = null,
            onClick = {}
        )
    }
}