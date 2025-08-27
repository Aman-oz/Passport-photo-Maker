package com.ots.image_picker.provider

import android.content.Context
import android.net.Uri
import com.ots.image_picker.model.AssetInfo
import com.ots.image_picker.model.RequestType

internal class AssetPickerRepository(
    private val context: Context
) {
    suspend fun getAssets(requestType: RequestType): List<AssetInfo> {
        return AssetLoader.load(context, requestType)
    }

    fun insertImage(): Uri? {
        return AssetLoader.insertImage(context)
    }

    fun findByUri(uri: Uri?): AssetInfo? {
        return uri?.let { AssetLoader.findByUri(context, it) }
    }

    fun deleteByUri(uri: Uri?) {
        uri?.let { AssetLoader.deleteByUri(context, it) }
    }
}