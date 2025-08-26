package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom300

@Composable
fun RadioButtonSingleSelection(
    modifier: Modifier = Modifier,
    radioButtonList: List<String> = listOf("Keep Original", "Change background color"),
    selectedIndex: Int = 0,
    onSelectionChange: (Int) -> Unit
) {
    val (selectedOption, onRadioButtonSelected) = remember { mutableStateOf(radioButtonList[selectedIndex]) }
    Column(modifier.selectableGroup()) {
        radioButtonList.forEach { text ->

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onSelectionChange(radioButtonList.indexOf(text))
                            onRadioButtonSelected(text)
                        },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(
                        id = if (text == selectedOption) R.drawable.radio_checked else R.drawable.radio_unchecked
                    ),
                    colorFilter = ColorFilter.tint(colors.onCustom400),
                    contentDescription = null
                )
                Text(
                    text = text,
                    color = colors.onCustom400,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES) // UI_MODE_NIGHT_YES
@Composable
fun RadioButtonPreview() {
    PreviewContainer {
        RadioButtonSingleSelection(
            modifier = Modifier.padding(16.dp),
            onSelectionChange = {}
        )

    }
}