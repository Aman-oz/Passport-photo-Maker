package com.ots.aipassportphotomaker.image_picker.provider

import android.content.Context
import android.net.Uri
import com.ots.aipassportphotomaker.image_picker.model.AssetInfo
import com.ots.aipassportphotomaker.image_picker.model.RequestType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AssetPickerRepository(
    private val context: Context
) {
    suspend fun getAssets(requestType: RequestType, limit: Int = 100, offset: Int = 0): List<AssetInfo> = withContext(Dispatchers.IO) {
        if (requestType == RequestType.IMAGE) {
            AssetLoader.load(context, requestType, limit, offset)
        } else {
            emptyList()
        }
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

    suspend fun getDirectoryCounts(): Map<String, Int> = withContext(Dispatchers.IO) {
        AssetLoader.getDirectoryCounts(context)
    }
}