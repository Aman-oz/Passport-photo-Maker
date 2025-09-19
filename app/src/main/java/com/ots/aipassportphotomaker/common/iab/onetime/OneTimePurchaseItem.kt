package com.ots.aipassportphotomaker.common.iab.onetime

import com.android.billingclient.api.ProductDetails
import com.las.collage.maker.iab.ProductItem

class OneTimePurchaseItem(productDetails: ProductDetails) : ProductItem(productDetails) {
    var purchasedItem: PurchasedItem? = null
    var price = productDetails.oneTimePurchaseOfferDetails!!.formattedPrice
}