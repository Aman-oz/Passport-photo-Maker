package com.ots.aipassportphotomaker.common.iab.interfaces

import com.ots.aipassportphotomaker.common.iab.subscription.SubscriptionItem

interface ConnectResponse
{
    fun disconnected()
    fun billingUnavailable()
    fun developerError()
    fun error()
    fun featureNotSupported()
    fun itemUnavailable()
    //fun ok(oneTimePurchaseItems: List<OneTimePurchaseItem>, subscriptionItems: List<SubscriptionItem>)
    fun ok(subscriptionItems: List<SubscriptionItem>)
    fun serviceDisconnected()
    fun serviceUnavailable()
}