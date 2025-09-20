package com.ots.aipassportphotomaker.adsmanager.admob.adids

import com.ots.aipassportphotomaker.BuildConfig

// Created by amanullah on 20/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
object AdIdsFactory {

    
    fun getBannerAdId(): String {
        return if (BuildConfig.DEBUG){
            TestAdIds.BANNER_AD_ID
        }else{
            RealAdIds.BANNER_AD_ID
        }
    }

    fun getSplashBannerAdId(): String {
        return if (BuildConfig.DEBUG){
            TestAdIds.BANNER_AD_ID_SPLASH
        }else{
            RealAdIds.BANNER_AD_ID_SPLASH
        }
    }

    fun getOnboardingBannerAdId(): String {
        return if (BuildConfig.DEBUG){
            TestAdIds.BANNER_AD_ID_ONBOARDING
        }else{
            RealAdIds.BANNER_AD_ID_ONBOARDING
        }
    }
    
    fun getExitBannerAdId(): String {
        return if (BuildConfig.DEBUG){
            TestAdIds.BANNER_AD_ID
        }else{
            RealAdIds.BANNER_AD_ID_HOME
        }
    }
    
    fun getNativeAdId(): String {
        return if (BuildConfig.DEBUG){
            TestAdIds.NATIVE_AD_ID
        }else{
            RealAdIds.NATIVE_AD_ID
        }
    }
    
    fun getNativeAdIdOnboarding(): String {
        return if (BuildConfig.DEBUG){
            TestAdIds.NATIVE_AD_ID_ONBOARDING
        }else{
            RealAdIds.NATIVE_AD_ID_ONBOARDING
        }
    }
    
    fun getNativeAdIdLanguage(): String {
        return if (BuildConfig.DEBUG){
            TestAdIds.NATIVE_AD_ID
        }else{
            RealAdIds.NATIVE_AD_ID_LANGUAGE
        }
    }
    
    fun getOpenAdId(): String {
        return if (BuildConfig.DEBUG){
            TestAdIds.OPEN_APP_AD_ID
        }else{
            RealAdIds.OPEN_APP_AD_ID
        }
    }
    
    fun getResumeAdId(): String {
        return if (BuildConfig.DEBUG){
            TestAdIds.RESUME_AD_ID
        }else{
            RealAdIds.RESUME_AD_ID
        }
    }

    
    fun getInterstitialAdId(): String {
        return if (BuildConfig.DEBUG){
            TestAdIds.INTERSTITIAL_AD_ID
        }else{
            RealAdIds.INTERSTITIAL_AD_ID
        }
    }
    
    fun getWelcomeInterstitialAdId(): String {
        return if (BuildConfig.DEBUG){
            TestAdIds.INTERSTITIAL_AD_ID
        }else{
            RealAdIds.INTERSTITIAL_AD_ID_WELCOME
        }
    }
    
    fun getRewardedAdId(): String {
        return if (BuildConfig.DEBUG){
            RealAdIds.REWARDED_AD_ID
        }else{
            RealAdIds.REWARDED_AD_ID
        }
    }
    
    fun getRewardedInterstitialAdId(): String {
        return if (BuildConfig.DEBUG){
            RealAdIds.REWARDED_INTERSTITIAL_AD_ID
        }else{
            RealAdIds.REWARDED_INTERSTITIAL_AD_ID
        }
    }
}