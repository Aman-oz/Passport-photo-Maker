package com.ots.aipassportphotomaker.adsmanager.revenue

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.utils.Logger
import java.util.Currency
import java.util.Locale

// Created by amanullah on 03/10/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

fun logAdRevenue(analyticsManager: AnalyticsManager,adType: String, adValue: Double) {
    val price = adValue / 1000000
    val currency = Currency.getInstance(Locale.US)

    val adRevenueParameters = Bundle()
    adRevenueParameters.putDouble(FirebaseAnalytics.Param.VALUE, price)
    adRevenueParameters.putString(FirebaseAnalytics.Param.CURRENCY, currency.currencyCode)
    adRevenueParameters.putString("ad_format", adType)
    adRevenueParameters.putString("ad_network", "admob")
    analyticsManager.sendEvent("ad_revenue_sdk", adRevenueParameters)

    Logger.d("AdRevenueTAG", "logAdRevenue adType : $adType")
    Logger.d("AdRevenueTAG", "logAdRevenue Currency : $currency")
    Logger.d("AdRevenueTAG", "logAdRevenue Price : $adValue")
    Logger.d("AdRevenueTAG", "logAdRevenue Price divide by 1m : $price")
}