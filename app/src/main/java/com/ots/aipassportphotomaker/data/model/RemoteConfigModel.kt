package com.ots.aipassportphotomaker.data.model

import com.google.gson.annotations.SerializedName

// Created by amanullah on 30/10/2025.
// Copyright (c) 2025 Ozi Technology. All rights reserved.
data class RemoteConfigModel(
    @SerializedName("forcefullyUpdate")
    val forcefullyUpdate: Boolean = false,

    @SerializedName("isOnboardingCrossVisible")
    val isOnboardingCrossVisible: Boolean = false,

    @SerializedName("splashFirstLaunch")
    val splashFirstLaunch: Int = 0,

    @SerializedName("splashSecondLaunch")
    val splashSecondLaunch: Int = 2,
)