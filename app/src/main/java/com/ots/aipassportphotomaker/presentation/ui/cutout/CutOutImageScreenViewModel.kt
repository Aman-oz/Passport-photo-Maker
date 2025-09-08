package com.ots.aipassportphotomaker.presentation.ui.cutout

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.utils.ColorUtils.parseColorFromString
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.usecase.photoid.GetDocumentDetails
import com.ots.aipassportphotomaker.domain.util.DispatchersProvider
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.domain.util.Result
import com.ots.aipassportphotomaker.domain.util.getDocumentWidthAndHeight
import com.ots.aipassportphotomaker.domain.util.onError
import com.ots.aipassportphotomaker.domain.util.onSuccess
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.text.ifEmpty

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@HiltViewModel
class CutOutImageScreenViewModel @Inject constructor(
    private val getDocumentDetails: GetDocumentDetails,
    editImageScreenBundle: CutOutImageScreenBundle,
    private val dispatcher: DispatchersProvider,
    private val networkMonitor: NetworkMonitor,
    @ApplicationContext private val context: Context
): BaseViewModel() {

    private val _uiState: MutableStateFlow<CutOutImageScreenUiState> =
        MutableStateFlow(CutOutImageScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<CutOutImageScreenNavigationState> =
        singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val documentId: Int = editImageScreenBundle.documentId
    val imageUrl: String? = editImageScreenBundle.imageUrl
    val selectedColor: String? = editImageScreenBundle.selectedColor

    init {
        Logger.i("CutOutImageScreenViewModel"," initialized with documentId: $documentId, imagePath: $imageUrl, selectedColor: $selectedColor")
        onInitialState()
        loadState(true)
    }

    private fun onInitialState() = launch {
        getDocumentById(documentId).onSuccess { document ->
            Logger.i("CutOutImageScreenViewModel", "Fetched document details: $document")
            loadState(isLoading = false)
            val parsedColor = parseColorFromString(selectedColor) ?: Color.Unspecified
            _uiState.value = CutOutImageScreenUiState(
                showLoading = false,
                documentName = document.name,
                documentSize = document.size,
                documentUnit = document.unit,
                documentPixels = document.pixels,
                documentResolution = document.resolution,
                documentImage = document.image,
                documentType = document.type,
                documentCompleted = document.completed,
                selectedColor = parsedColor,
                imageUrl = imageUrl
            )

            val size = getDocumentWidthAndHeight(document.size)

            if (imageUrl.isNullOrEmpty()) {
                Logger.e("CutOutImageScreenViewModel", "No image path provided")
                _error.value = "No image was selected"
                return@onSuccess
            }

            try {

                val width = size.width ?: 0f
                val height = size.height ?: 0f
                val unit = document.unit.ifEmpty { "mm" }

                Logger.i("CutOutImageScreenViewModel", "Starting crop with size: $size width=$width, height=$height, unit=$unit")

            } catch (e: Exception) {
                Logger.e("CutOutImageScreenViewModel", "Error processing image: ${e.message}", e)
                _error.value = "Failed to process the selected image: ${e.message}"
            }
        }.onError { error ->
            Logger.e("CutOutImageScreenViewModel", "Failed to fetch document: ${error.message}", error)
            _error.value = "Failed to load document details"
        }
    }

    fun saveEditedImage(editedBitmap: ImageBitmap) {
        launch {
            val editedUri = saveBitmapToMediaStore(context, editedBitmap.asAndroidBitmap(), "edited_${System.currentTimeMillis()}.png")
            if (editedUri != null) {
                _uiState.value = _uiState.value.copy(imageUrl = editedUri.toString())
                Logger.i("CutOutImageScreenViewModel", "Saved edited image to $editedUri")
            } else {
                _error.value = "Failed to save edited image"
            }
        }
    }

    private fun saveBitmapToMediaStore(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                true
            } ?: run { context.contentResolver.delete(uri, null, null); null }
            uri
        }
    }

    private fun loadState(isLoading: Boolean) {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = isLoading, errorMessage = null)
        }
    }


    private suspend fun getDocumentById(documentId: Int): Result<DocumentEntity> = getDocumentDetails(documentId)

}