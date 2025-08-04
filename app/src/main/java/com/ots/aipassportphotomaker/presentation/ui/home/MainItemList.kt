package com.ots.aipassportphotomaker.presentation.ui.home

import androidx.compose.ui.graphics.Color
import com.ots.aipassportphotomaker.R

data class MainItem(
    val name: String,
    val title: String,
    val description: String,
    val backgroundColor: Color? = null,
    val textColor: Color? = null,
    val backgroundImage: Int? = null,
    val sparkleImage: Int? = null
)

val mainItems = listOf(
    MainItem(
        name = "PhotoID",
        title = "Photo ID",
        description = "Create a photo ID",
        backgroundColor = Color.Blue,
        backgroundImage = R.drawable.bg_card_photo_id
    ),
    MainItem(
        name = "Cutout",
        title = "Cut out",
        description = "Remove background",
        backgroundColor = Color.Green,
        backgroundImage = R.drawable.bg_card_cut_out
    ),
    MainItem(
        name = "ChangeBG",
        title = "Change BG",
        description = "Change background color",
        backgroundColor = Color.Cyan,
        backgroundImage = R.drawable.bg_card_change_bg
    ),
    MainItem(
        name = "AddSuits",
        title = "Add Suits",
        description = "Add suits to photo",
        backgroundColor = Color.Magenta,
        backgroundImage = R.drawable.bg_card_add_suits
    ),
    MainItem(
        name = "CustomSize",
        title = "Custom Size",
        description = "Resize photo to custom size",
        backgroundColor = Color.Yellow,
        backgroundImage = R.drawable.bg_card_custom_size
    ),
    MainItem(
        name = "SocialProfile",
        title = "Social Profile",
        description = "Create social profile photo",
        backgroundColor = Color.Red,
        backgroundImage = R.drawable.bg_card_social_profile
    )
)