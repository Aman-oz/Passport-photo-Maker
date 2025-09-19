package com.ots.aipassportphotomaker.common.iab.interfaces

import com.las.collage.maker.iab.ProductItem

interface PurchaseResponse
{
    fun isAlreadyOwned()
    fun userCancelled()
    fun ok(productItem: ProductItem)
    fun error(error: String)
}