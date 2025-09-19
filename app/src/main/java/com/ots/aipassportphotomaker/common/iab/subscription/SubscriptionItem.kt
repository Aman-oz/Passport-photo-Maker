package com.ots.aipassportphotomaker.common.iab.subscription

import com.android.billingclient.api.ProductDetails
import com.las.collage.maker.iab.ProductItem

class SubscriptionItem(productDetails: ProductDetails) : ProductItem(productDetails) {
    var subscribedItem: SubscribedItem? = null

    var offerToken = getSubscriptionOfferDetails()?.offerToken

    // get the price of base plan
    var pricingPhase: ProductDetails.PricingPhase? =
        getSubscriptionOfferDetails()?.pricingPhases?.pricingPhaseList?.last()

    //check trial
    var isTrial: Boolean? = getSubscriptionOfferDetails()?.pricingPhases?.pricingPhaseList?.size!! > 1

    // get first of offers if have
    private fun getSubscriptionOfferDetails(): ProductDetails.SubscriptionOfferDetails? {
        return productDetails.subscriptionOfferDetails?.first()
    }
}