package com.ots.aipassportphotomaker.domain.model

import com.ots.aipassportphotomaker.R

// Created by amanullah on 17/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
data class OnBoardingItem(
    val title: Int,
    val text: Int,
    val Image: Int,
) {
    companion object {

        fun get() = listOf(
            OnBoardingItem(R.string.title1, R.string.text1, R.drawable.onboarding_1),
            OnBoardingItem(R.string.title2, R.string.text2, R.drawable.onboarding_2),
            OnBoardingItem(R.string.title3, R.string.text3, R.drawable.onboarding_3)
        )
    }
}
