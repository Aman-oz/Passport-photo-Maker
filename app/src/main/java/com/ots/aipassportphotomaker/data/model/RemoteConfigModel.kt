package com.ots.aipassportphotomaker.data.model

import com.google.gson.annotations.SerializedName

// Created by amanullah on 30/10/2025.
// Copyright (c) 2025 Ozi Technology. All rights reserved.
data class RemoteConfigModel(
    @SerializedName("forcefullyUpdate")
    val forcefullyUpdate: Boolean = false,

    @SerializedName("isOnboardingCrossVisible")
    val isOnboardingCrossVisible: Boolean = false,

    @SerializedName("onboardingFlow")
    val onboardingFlow: Int = 0,

    @SerializedName("premiumCloseFlow")
    val premiumCloseFlow: Int = 0,

    @SerializedName("splashFirstLaunch")
    val splashFirstLaunch: Int = 0,

    @SerializedName("splashSecondLaunch")
    val splashSecondLaunch: Int = 0,

    @SerializedName("mainUIPlacement")
    val mainUIPlacement: Int = 0,

    @SerializedName("isNewCardsLayoutVisible")
    val isNewCardsLayoutVisible: Boolean = false,

    @SerializedName("interstitialAdCount")
    val interstitialAdCount: Int = 5,

    @SerializedName("interstititalAdDelay")
    val interstititalAdDelay: Int = 30,

    @SerializedName("resumeAdDelay")
    val resumeAdDelay: Int = 15,

    @SerializedName("nativeAdDesign")
    val nativeAdDesign: NativeAdDesign = NativeAdDesign(),

    @SerializedName("appOpenAdId")
    val appOpenAdId: String = "ca-app-pub-7883837684925648/7452473782",

    @SerializedName("appOpenAdIdResume")
    val appOpenAdIdResume: String = "ca-app-pub-7883837684925648/7927100593",

    @SerializedName("welcomeInterstitialAdId")
    val welcomeInterstitialAdId: String = "ca-app-pub-7883837684925648/4443167060",

    @SerializedName("interstitialWhenOpenFailedAdId")
    val interstitialWhenOpenFailedAdId: String = "ca-app-pub-7883837684925648/8977308440",

    @SerializedName("interstitialAdId")
    val interstitialAdId: String = "ca-app-pub-7883837684925648/6917271257",

    @SerializedName("interstitialPremiumCloseAdId")
    val interstitialPremiumCloseAdId: String = "ca-app-pub-7883837684925648/6616130415",

    @SerializedName("bannerAdId")
    val bannerAdId: String = "ca-app-pub-7883837684925648/7743267761",

    @SerializedName("bannerExitAdId")
    val bannerExitAdId: String = "ca-app-pub-7883837684925648/3097445740",

    @SerializedName("bannerCollasableAdId")
    val bannerCollasableAdId: String = "ca-app-pub-7883837684925648/7743267761",

    @SerializedName("nativeAdId")
    val nativeAdId: String = "ca-app-pub-7883837684925648/7316742415",

    @SerializedName("nativeAdIdOnBoarding")
    val nativeAdIdOnBoarding: String = "ca-app-pub-7883837684925648/8673206057",

    @SerializedName("nativeAdIdOnLanguage")
    val nativeAdIdOnLanguage: String = "ca-app-pub-7883837684925648/8785736757",

    @SerializedName("rewardedVideoAdId")
    val rewardedVideoAdId: String = "ca-app-pub-7883837684925648/6614018923",

    @SerializedName("rewardedInterstitialAdId")
    val rewardedInterstitialAdId: String = "ca-app-pub-7883837684925648/6614018923"
)

data class NativeAdDesign(
    @SerializedName("callActionButtonColor")
    val callActionButtonColor: String = "#486AEC",

    @SerializedName("backgroundColor")
    val backgroundColor: String = "#E9EDFB",

    @SerializedName("ctaText")
    val ctaText: String = "#FFFFFF",

    @SerializedName("heading")
    val heading: String = "#000000",

    @SerializedName("description")
    val description: String = "#111111"
)