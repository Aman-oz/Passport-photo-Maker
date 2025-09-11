package com.ots.aipassportphotomaker.presentation.ui.savedimage

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.ots.aipassportphotomaker.common.ext.FacebookPackage
import com.ots.aipassportphotomaker.common.ext.InstagramPackage
import com.ots.aipassportphotomaker.common.ext.SharePackage
import com.ots.aipassportphotomaker.common.ext.WhatsappPackage
import com.ots.aipassportphotomaker.common.ext.shareMedia
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.utils.ImageUtils.getFileSizeInfo
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.usecase.photoid.GetDocumentDetails
import com.ots.aipassportphotomaker.domain.util.DispatchersProvider
import com.ots.aipassportphotomaker.domain.util.Result
import com.ots.aipassportphotomaker.domain.util.getDocumentWidthAndHeight
import com.ots.aipassportphotomaker.domain.util.onError
import com.ots.aipassportphotomaker.domain.util.onSuccess
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@HiltViewModel
class SavedImageScreenViewModel @Inject constructor(
    private val getDocumentDetails: GetDocumentDetails,
    savedImageScreenBundle: SavedImageScreenBundle,
    private val dispatcher: DispatchersProvider,
    @ApplicationContext private val context: Context
): BaseViewModel() {

    private val _uiState: MutableStateFlow<SavedImageScreenUiState> =
        MutableStateFlow(SavedImageScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<SavedImageScreenNavigationState> =
        singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val documentId: Int = savedImageScreenBundle.documentId
    val imagePath: String? = savedImageScreenBundle.imagePath

    init {
        Logger.i("EditImageScreenViewModel"," initialized with documentId: $documentId, imagePath: $imagePath")
        onInitialState()
        loadState(true)
    }

    fun onLoadStateUpdate(loadState: CombinedLoadStates) {
        val showLoading = loadState.refresh is LoadState.Loading

        val error = when (val refresh = loadState.refresh) {
            is LoadState.Error -> refresh.error.message
            else -> null
        }

        _uiState.update { it.copy(showLoading = showLoading, errorMessage = error) }
    }

    private fun onInitialState() = launch {
        getDocumentById(documentId).onSuccess { document ->
            Logger.i("EditImageScreenViewModel", "Fetched document details: $document")
            loadState(isLoading = false)
            _uiState.value = SavedImageScreenUiState(
                showLoading = false,
                documentName = document.name,
                documentSize = document.size,
                documentUnit = document.unit,
                documentPixels = document.pixels,
                documentResolution = document.resolution,
                documentImage = document.image,
                documentType = document.type,
                documentCompleted = document.completed,
                imagePath = imagePath
            )

            val fileSize = getFileSizeInfo(context,imagePath)

            _uiState.value = _uiState.value.copy(
                fileSize = fileSize
            )

            val size = getDocumentWidthAndHeight(document.size)
            _uiState.value = _uiState.value.copy(ratio = size.width/size.height)

            if (imagePath.isNullOrEmpty()) {
                Logger.e("EditImageScreenViewModel", "No image path provided")
                _error.value = "No image was selected"
                return@onSuccess
            }

            try {

                val width = size.width ?: 0f
                val height = size.height ?: 0f
                val unit = document.unit.ifEmpty { "mm" }

                Logger.i("EditImageScreenViewModel", "Starting crop with size: $size width=$width, height=$height, unit=$unit")

            } catch (e: Exception) {
                Logger.e("EditImageScreenViewModel", "Error processing image: ${e.message}", e)
                _error.value = "Failed to process the selected image: ${e.message}"
            }
        }.onError { error ->
            Logger.e("EditImageScreenViewModel", "Failed to fetch document: ${error.message}", error)
            _error.value = "Failed to load document details"
        }
    }

    /*fun onCutoutClicked() {
        _navigationState.tryEmit(
            EditImageScreenNavigationState.CutOutScreen(
                documentId = documentId,
                imageUrl = imageUrl,
                selectedBackgroundColor = mParsedColor

            ))
    }*/


    fun shareToWhatsApp(context: Context,imagePath: String) {
        shareMedia(context, imagePath.toUri(), WhatsappPackage)
    }

    fun shareToInstagram(context: Context,imagePath: String) {
        shareMedia(context, imagePath.toUri(), InstagramPackage)
    }

    fun shareToFacebook(context: Context,imagePath: String) {
        shareMedia(context, imagePath.toUri(), FacebookPackage)
    }

    fun shareToOthers(context: Context,imagePath: String) {
        shareMedia(context, imagePath.toUri(), SharePackage)
    }

    fun deleteImage(imagePath: String?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (imagePath.isNullOrEmpty()) {
            onError("No image to delete")
            return
        }

        viewModelScope.launch(dispatcher.io) {
            try {
                val uri = Uri.parse(imagePath)

                val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // For Android 10+
                    val numRowsDeleted = context.contentResolver.delete(uri, null, null)
                    numRowsDeleted > 0
                } else {
                    // For older Android versions
                    val file = File(uri.path ?: "")
                    file.exists() && file.delete()
                }

                withContext(dispatcher.main) {
                    if (result) {
                        onSuccess()
                    } else {
                        onError("Failed to delete image")
                    }
                }
            } catch (e: Exception) {
                Logger.e("SavedImageScreenViewModel", "Error deleting image: ${e.message}", e)
                withContext(dispatcher.main) {
                    onError("Error deleting image: ${e.message}")
                }
            }
        }
    }

    private fun loadState(isLoading: Boolean) {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = isLoading, errorMessage = null)
        }
    }

    private suspend fun getDocumentById(documentId: Int): Result<DocumentEntity> = getDocumentDetails(documentId)

}