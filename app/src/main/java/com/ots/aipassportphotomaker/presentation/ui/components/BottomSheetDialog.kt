package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.AppColors
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400
import io.mhssn.colorpicker.ext.transparentBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDialogLayout(
    modifier: Modifier = Modifier
) {

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Change Background",
            color = colors.onCustom400,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Select a background color for your document.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        RadioButtonSingleSelection(
            radioButtonList = listOf("Keep Original", "Change background color")
        ) {

        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {

            Spacer(modifier = Modifier.width(40.dp))

            ColorItem(
                modifier = Modifier
                    .width(42.dp),
                color = Color.White,
                ratio = 1.2f,
                showEyeDropper = true

            ){  }

            ColorItem(
                modifier = Modifier
                    .width(42.dp),
                color = Color.White,
                ratio = 1.2f,
                showTransparentBg = true

            ){  }

            ColorItem(
                modifier = Modifier
                    .width(42.dp),
                color = Color.White,
                ratio = 1.2f

            ){  }

            ColorItem(
                modifier = Modifier
                    .width(42.dp),
                color = Color.Green,
                ratio = 1.2f

            ){  }

            ColorItem(
                modifier = Modifier
                    .width(42.dp),
                color = AppColors.LightPrimary,
                ratio = 1.2f

            ){  }

            ColorItem(
                modifier = Modifier
                    .width(42.dp),
                color = Color.Red,
                ratio = 1.2f

            ){  }


        }
        Button(
            onClick = {
                scope.launch { bottomSheetState.hide() }
                showBottomSheet = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
        ) {
            Text(
                text = "Apply",
                color = colors.onPrimary,
                fontSize = 16.sp
            )
        }
    }

}

@Composable
fun ColorItem(
    modifier: Modifier = Modifier,
    color: Color = colors.primary,
    radius: Dp = 8.dp,
    ratio: Float = 1f,
    showEyeDropper: Boolean = false,
    showTransparentBg: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .width(36.dp)
            .aspectRatio(ratio)
            .border(1.dp, colors.outline, shape = RoundedCornerShape(6.dp))
            .background(
                Color.Transparent,
                shape = RoundedCornerShape(radius)
            )
            .clickable {
                onClick.invoke()
            }
    ) {
        val itemBgModifier = if (showTransparentBg) {
            Modifier.fillMaxSize()
                .padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
//                .transparentBackground(verticalBoxesAmount = 20)
                .background(
                    Color.Transparent,
                    shape = RoundedCornerShape(radius)
                )
        } else {
            Modifier.fillMaxSize()
                .padding(2.dp)
                .background(
                    color,
                    shape = RoundedCornerShape(radius)
                )
        }

        Box(
            modifier = itemBgModifier
                .clickable(onClick = {
                    onClick.invoke()
                }),
            contentAlignment = Alignment.Center
        ) {
            if (showEyeDropper) {
                Image(
                    painter = painterResource(id = R.drawable.eye_dropper_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                )
            }

            if (showTransparentBg) {
                Image(
                    painter = painterResource(id = R.drawable.transparent_round_bg),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
        }

    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GreetingPreview() {
    PreviewContainer {
        BottomSheetDialogLayout()
    }
}