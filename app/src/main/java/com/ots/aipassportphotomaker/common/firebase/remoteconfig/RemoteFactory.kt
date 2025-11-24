package com.ots.aipassportphotomaker.common.firebase.remoteconfig

import com.ots.aipassportphotomaker.data.model.RemoteConfigModel

// Created by amanullah on 30/10/2025.
// Copyright (c) 2025 Ozi Technology. All rights reserved.
object RemoteFactory {

    private var remoteConfigModel: RemoteConfigModel? = null

    init {
        remoteConfigModel = RemoteConfig.getConfigModel()
    }

    @JvmStatic
    fun isOnboardingCrossVisible(): Boolean {
        return remoteConfigModel?.isOnboardingCrossVisible ?: false
    }

    @JvmStatic
    fun getSplashFirstLaunch(): Int {
        return remoteConfigModel?.splashFirstLaunch ?: 0
    }

    @JvmStatic
    fun getSplashSecondLaunch(): Int {
        return remoteConfigModel?.splashSecondLaunch ?: 2
    }
}