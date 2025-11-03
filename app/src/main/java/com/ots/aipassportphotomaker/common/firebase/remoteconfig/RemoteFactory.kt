package com.ots.aipassportphotomaker.common.firebase.remoteconfig

import com.ots.aipassportphotomaker.BuildConfig
import com.ots.aipassportphotomaker.adsmanager.admob.adids.RealAdIds
import com.ots.aipassportphotomaker.adsmanager.admob.adids.TestAdIds
import com.ots.aipassportphotomaker.data.model.NativeAdDesign
import com.ots.aipassportphotomaker.data.model.RemoteConfigModel

// Created by amanullah on 30/10/2025.
// Copyright (c) 2025 Ozi Technology. All rights reserved.
object RemoteFactory {

    private var tooltipVisibility: Long = 1L
    private var isVisible = true

    private var remoteConfigModel: RemoteConfigModel? = null

    init {
        remoteConfigModel = RemoteConfig.getConfigModel()
    }

    fun updateTooltipVisibility(value: Long) {
        tooltipVisibility = value
    }

    @JvmStatic
    fun getTooltipVisibility(): Long {
        return tooltipVisibility
    }

    fun updatePremiumDialogValue(isVisible: Boolean) {
        this.isVisible = isVisible
    }

    @JvmStatic
    fun getPremiumDialogValue(): Boolean {
        return isVisible
    }

    @JvmStatic
    fun isOnboardingCrossVisible(): Boolean {
        return remoteConfigModel?.isOnboardingCrossVisible ?: false
    }

    @JvmStatic
    fun getOnboardingFlow(): Int {
        return remoteConfigModel?.onboardingFlow ?: 0
    }

    @JvmStatic
    fun getPremiumCloseFlow(): Int {
        return remoteConfigModel?.premiumCloseFlow ?: 0
    }

    @JvmStatic
    fun getSplashFirstLaunch(): Int {
        return remoteConfigModel?.splashFirstLaunch ?: 0
    }

    @JvmStatic
    fun getSplashSecondLaunch(): Int {
        return remoteConfigModel?.splashSecondLaunch ?: 2
    }

    @JvmStatic
    fun getMainUIPlacement(): Int {
        return remoteConfigModel?.mainUIPlacement ?: 0
    }

    @JvmStatic
    fun getIsNewCardsLayoutsVisible(): Boolean {
        return remoteConfigModel?.isNewCardsLayoutVisible ?: false
    }

    @JvmStatic
    fun getInterstitialAdCount(): Int {
        return remoteConfigModel?.interstitialAdCount ?: 5
    }

    @JvmStatic
    fun getInterstitialAdDelay(): Int {
        return remoteConfigModel?.interstititalAdDelay ?: 30
    }

    @JvmStatic
    fun getResumeAdDelay(): Int {
        return remoteConfigModel?.resumeAdDelay ?: 15
    }

    @JvmStatic
    fun getNativeAdDesign(): NativeAdDesign {
        return remoteConfigModel?.nativeAdDesign ?: NativeAdDesign()
    }

    @JvmStatic
    fun getAppOpenAdId(): String {
        return if (BuildConfig.DEBUG) TestAdIds.OPEN_APP_AD_ID else remoteConfigModel?.appOpenAdId?: RealAdIds.OPEN_APP_AD_ID

    }

    @JvmStatic
    fun getAppOpenAdIdResume(): String {
        return if (BuildConfig.DEBUG) TestAdIds.RESUME_AD_ID else remoteConfigModel?.appOpenAdIdResume?: RealAdIds.RESUME_AD_ID
    }

    @JvmStatic
    fun getWelcomeInterstitialAdId(): String {
        return if (BuildConfig.DEBUG) TestAdIds.INTERSTITIAL_AD_ID_WELCOME else remoteConfigModel?.welcomeInterstitialAdId?: RealAdIds.INTERSTITIAL_AD_ID_WELCOME
    }


    @JvmStatic
    fun getInterstitialAdId(): String {
        return if (BuildConfig.DEBUG) TestAdIds.INTERSTITIAL_AD_ID else remoteConfigModel?.interstitialAdId?: RealAdIds.INTERSTITIAL_AD_ID
    }

    @JvmStatic
    fun getInterstitialPremiumCloseAdId(): String {
        return if (BuildConfig.DEBUG) TestAdIds.INTERSTITIAL_AD_ID_WELCOME else remoteConfigModel?.interstitialPremiumCloseAdId?: RealAdIds.INTERSTITIAL_AD_ID_WELCOME
    }

    @JvmStatic
    fun getBannerAdId(): String {

        return if (BuildConfig.DEBUG) TestAdIds.BANNER_AD_ID else remoteConfigModel?.bannerAdId?: RealAdIds.BANNER_AD_ID
    }

    @JvmStatic
    fun getBannerExitAdId(): String {
        return if (BuildConfig.DEBUG) TestAdIds.BANNER_AD_ID_HOME else remoteConfigModel?.bannerExitAdId?: RealAdIds.BANNER_AD_ID_HOME
    }

    @JvmStatic
    fun getNativeAdId(): String {
        return if (BuildConfig.DEBUG) TestAdIds.NATIVE_AD_ID else remoteConfigModel?.nativeAdId?: RealAdIds.NATIVE_AD_ID
    }

    @JvmStatic
    fun getNativeAdIdOnboarding(): String {
        return if (BuildConfig.DEBUG) TestAdIds.NATIVE_AD_ID_ONBOARDING else remoteConfigModel?.nativeAdIdOnBoarding?: RealAdIds.NATIVE_AD_ID_ONBOARDING
    }

    @JvmStatic
    fun getNativeAdIdLanguage(): String {
        return if (BuildConfig.DEBUG) TestAdIds.NATIVE_AD_ID_LANGUAGE else remoteConfigModel?.nativeAdIdOnLanguage?: RealAdIds.NATIVE_AD_ID_LANGUAGE
    }

    @JvmStatic
    fun getRewardedVideoAdId(): String {
        return if (BuildConfig.DEBUG) TestAdIds.REWARDED_AD_ID else remoteConfigModel?.rewardedVideoAdId?: RealAdIds.REWARDED_AD_ID
    }

    @JvmStatic
    fun getRewardedInterstitialAdId(): String {
        return if (BuildConfig.DEBUG) TestAdIds.REWARDED_INTERSTITIAL_AD_ID else remoteConfigModel?.rewardedInterstitialAdId?: RealAdIds.REWARDED_INTERSTITIAL_AD_ID
    }
}