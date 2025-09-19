package com.ots.aipassportphotomaker.common.iab.interfaces

import com.las.collage.maker.iab.ProductItem

interface QueryResponse<in T : ProductItem>
{
    fun error(responseCode: Int)
    fun ok(skuItems: List<T>)
}