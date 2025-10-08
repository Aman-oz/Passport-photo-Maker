package com.ots.aipassportphotomaker.presentation.ui.home

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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

fun mainItems(context: Context) = listOf(
    MainItem(
        name = "PhotoID",
        title = context.getString(R.string.photo_id),
        description = "Create a photo ID",
        backgroundColor = Color.Blue,
        backgroundImage = R.drawable.bg_card_photo_id
    ),
    MainItem(
        name = "Cutout",
        title = context.getString(R.string.cut_out),
        description = "Remove background",
        backgroundColor = Color.Green,
        backgroundImage = R.drawable.bg_card_cut_out
    ),
    MainItem(
        name = "ChangeBG",
        title = context.getString(R.string.change_bg),
        description = "Change background color",
        backgroundColor = Color.Cyan,
        backgroundImage = R.drawable.bg_card_change_bg
    ),
    MainItem(
        name = "AddSuits",
        title = context.getString(R.string.add_suits),
        description = "Add suits to photo",
        backgroundColor = Color.Magenta,
        backgroundImage = R.drawable.bg_card_add_suits
    ),
    MainItem(
        name = "CustomSize",
        title = context.getString(R.string.custom_size),
        description = "Resize photo to custom size",
        backgroundColor = Color.Yellow,
        backgroundImage = R.drawable.bg_card_custom_size
    ),
    MainItem(
        name = "SocialProfile",
        title = context.getString(R.string.social_profile),
        description = "Create social profile photo",
        backgroundColor = Color.Red,
        backgroundImage = R.drawable.bg_card_social_profile
    )
)