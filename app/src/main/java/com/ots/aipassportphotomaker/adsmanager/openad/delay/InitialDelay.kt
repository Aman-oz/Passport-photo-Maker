package com.ots.aipassportphotomaker.adsmanager.openad.delay

import com.ots.aipassportphotomaker.common.utils.Logger

// Created by amanullah on 03/10/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class InitialDelay(
    private val delayCount: Int = 1,
    internal val delayPeriodType: DelayType = DelayType.DAYS
) {
    init {
        // Zero is fine
        if (delayCount < 0) throw IllegalArgumentException("Delay Count cannot be Negative.")
        // Just a harmless warning message
        if (delayPeriodType == DelayType.DAYS && delayCount > 2) Logger.d("InitialDelay","You sure that the InitialDelay set by you is correct?")
    }

    internal fun getTime(): Int {
        val oneHourInMillis = 3600000
        val twoMinuteInMillis = 120000
        val periodTypeToMillis =
            when (delayPeriodType) {
                DelayType.DAYS -> (oneHourInMillis * 24)
                DelayType.HOUR -> oneHourInMillis
                DelayType.MINUTE_2 -> twoMinuteInMillis
                else -> 0
            }
        return (delayCount * periodTypeToMillis)
    }

    companion object {
        @JvmField
        val NONE = InitialDelay(0, DelayType.NONE)

        @JvmField
        val MINUTE_2 = InitialDelay(1, DelayType.MINUTE_2)
    }
}