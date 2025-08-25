package com.ots.aipassportphotomaker.presentation.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.domain.model.DocumentPixels
import com.ots.aipassportphotomaker.domain.model.DocumentSize
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentInfoScreenUiState
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom100
import com.ots.aipassportphotomaker.presentation.ui.theme.custom300
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom300

// Created by amanullah on 25/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Composable
fun ImageWithMeasurements(
    modifier: Modifier = Modifier,
    unitSize: DocumentSize,
    pixelSize: DocumentPixels,
    unit: String = "mm",
    imageRes: Int = R.drawable.sample_image_portrait,
    imageRatio: Float = 300/396f
) {
    val constraints = ConstraintSet {
        val image = createRefFor("image")
        val leftLine = createRefFor("leftLine")
        val topLine = createRefFor("topLine")
        val rightLine = createRefFor("rightLine")
        val bottomLine = createRefFor("bottomLine")
        val textBoxLeft = createRefFor("textBoxLeft")
        val textBoxTop = createRefFor("textBoxTop")
        val textBoxRight = createRefFor("textBoxRight")
        val textBoxBottom = createRefFor("textBoxBottom")


        constrain(image) {
            centerTo(parent)
            width = Dimension.preferredWrapContent
            height = Dimension.preferredWrapContent
        }

        constrain(leftLine) {
//            start.linkTo(parent.start)
            end.linkTo(image.start)
            top.linkTo(image.top)
            bottom.linkTo(image.bottom)
            height = Dimension.fillToConstraints
        }

        constrain(topLine) {
            start.linkTo(image.start)
            end.linkTo(image.end)
//            top.linkTo(parent.top)
            bottom.linkTo(image.top)
            width = Dimension.fillToConstraints
        }

        constrain(rightLine) {
            start.linkTo(image.end)
//            end.linkTo(parent.end)
            top.linkTo(image.top)
            bottom.linkTo(image.bottom)
            height = Dimension.fillToConstraints
        }

        constrain(bottomLine) {
            start.linkTo(image.start)
            end.linkTo(image.end)
            top.linkTo(image.bottom)
//            bottom.linkTo(image.bottom)
            width = Dimension.fillToConstraints
        }

        //Left TextBox
        constrain(textBoxLeft) {
            start.linkTo(leftLine.start)
            end.linkTo(leftLine.end)
            top.linkTo(leftLine.top)
            bottom.linkTo(leftLine.bottom)
            centerVerticallyTo(leftLine)
            centerHorizontallyTo(leftLine)
        }

        //Top TextBox
        constrain(textBoxTop) {
            start.linkTo(topLine.start)
            end.linkTo(topLine.end)
            top.linkTo(topLine.top)
            bottom.linkTo(topLine.bottom)
        }

        //Right TextBox
        constrain(textBoxRight) {
            start.linkTo(rightLine.start)
            end.linkTo(rightLine.end)
            top.linkTo(rightLine.top)
            bottom.linkTo(rightLine.bottom)
        }

        //Bottom TextBox
        constrain(textBoxBottom) {
            start.linkTo(bottomLine.start)
            end.linkTo(bottomLine.end)
            top.linkTo(bottomLine.top)
            bottom.linkTo(bottomLine.bottom)
        }


    }

    ConstraintLayout(constraints, modifier = modifier) {
        // image
        Box(
            modifier = Modifier
                .layoutId("image")
                .padding(16.dp)
                .background(color = colors.primary)
                .width(200.dp)
                .aspectRatio(imageRatio)
        ) {
            Image(
                painter = painterResource(id = imageRes), // Replace with your drawable resource ID
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                            },
                            onTap = {
                                // onDocumentClick(document.id)
                            }
                        )
                    }

            )
        }

        Box(
            modifier = Modifier
                .layoutId("leftLine"),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.box_line_vertical), // Replace with your drawable resource ID
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp),
                contentScale = ContentScale.FillHeight
            )
        }

        Box(
            modifier = Modifier
                .layoutId("topLine"),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.box_line_horizontal), // Replace with your drawable resource ID
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentScale = ContentScale.FillWidth
            )
        }

        Box(
            modifier = Modifier
                .layoutId("rightLine"),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.box_line_vertical), // Replace with your drawable resource ID
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp),
                contentScale = ContentScale.FillHeight
            )
        }

        Box(
            modifier = Modifier
                .layoutId("bottomLine"),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.box_line_horizontal), // Replace with your drawable resource ID
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentScale = ContentScale.FillWidth
            )
        }

        //Left TextBox
        TextBox(
            modifier = Modifier
                .layoutId("textBoxLeft"),
            value = "${pixelSize.height} px",
            rotation = -90f
        )

        // Top TextBox
        TextBox(
            modifier = Modifier
                .layoutId("textBoxTop"),
            value = "${pixelSize.width} px",
            rotation = 0f
        )

        // Right TextBox
        TextBox(
            modifier = Modifier
                .layoutId("textBoxRight"),
            value = "${unitSize.height} $unit",
            rotation = 90f
        )

        //Bottom TextBox
        TextBox(
            modifier = Modifier
                .layoutId("textBoxBottom"),
            value = "${unitSize.width} $unit",
            rotation = 0f
        )
    }
}

@Composable
fun TextBox(
    modifier: Modifier = Modifier,
    value: String,
    rotation: Float = 0f
) {
    Box(
        modifier = modifier
            .rotate(rotation)
            .background(color = colors.custom300, shape = RoundedCornerShape(4.dp))
            .border(width = 1.dp, color = colors.custom100, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = colors.onCustom300,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ImageWithMeasurementsPreview() {
    PreviewContainer {
        ImageWithMeasurements(
            modifier = Modifier.fillMaxSize(),
            unitSize = DocumentSize(35f, 45f, "Portrait"),
            pixelSize = DocumentPixels(413, 531),
            unit = "mm",
            imageRes = R.drawable.sample_image_portrait,

        )
    }
}