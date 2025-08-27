package com.ots.aipassportphotomaker.image_picker.model

data class AssetPickerConfig(
    val maxAssets: Int = 1,
    val gridCount: Int = 3,
    val requestType: RequestType = RequestType.COMMON,
)