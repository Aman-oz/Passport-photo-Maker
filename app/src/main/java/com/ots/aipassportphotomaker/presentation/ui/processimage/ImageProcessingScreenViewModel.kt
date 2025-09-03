package com.ots.aipassportphotomaker.presentation.ui.processimage

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.utils.FileUtils
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.data.model.ApiResponse
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.repository.CropImageRepository
import com.ots.aipassportphotomaker.domain.usecase.photoid.GetDocumentDetails
import com.ots.aipassportphotomaker.domain.util.DispatchersProvider
import com.ots.aipassportphotomaker.domain.util.Result
import com.ots.aipassportphotomaker.domain.util.getDocumentWidthAndHeight
import com.ots.aipassportphotomaker.domain.util.onSuccess
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentDetailsBundle
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentInfoScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentInfoScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

// Created by amanullah on 27/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@HiltViewModel
class ImageProcessingScreenViewModel @Inject constructor(
    private val getDocumentDetails: GetDocumentDetails,
    imageProcessingBundle: ImageProcessingBundle,
    private val repository: CropImageRepository,
    private val dispatcher: DispatchersProvider,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private val _uiState: MutableStateFlow<ImageProcessingScreenUiState> =
        MutableStateFlow(ImageProcessingScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<ImageProcessingScreenNavigationState> =
        singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val documentId: Int = imageProcessingBundle.documentId
    val imagePath: String? = imageProcessingBundle.imagePath
    val selectedDpi: String = imageProcessingBundle.selectedDpi
    val selectedColor: String? = imageProcessingBundle.selectedColor

    private var lastCroppedUrl: String? = null


    init {
        Logger.i("ImageProcessingScreenViewModel"," initialized with documentId: $documentId, imagePath: $imagePath, selectedDpi: $selectedDpi, selectedColor: $selectedColor")
        onInitialState()
        loadState(true)
    }

    private fun onInitialState() = launch {
        getDocumentById(documentId).onSuccess {
            Logger.i("ImageProcessingScreenViewModel","Fetched document details: $it")
            loadState(isLoading = false)
            _uiState.value = ImageProcessingScreenUiState(
                showLoading = false,
                documentName = it.name,
                documentSize = it.size,
                documentUnit = it.unit,
                documentPixels = it.pixels,
                documentResolution = it.resolution,
                documentImage = it.image,
                documentType = it.type,
                documentCompleted = it.completed,
            )

            val imageFile = imagePath?.let { path ->
                try {
                    val uri = Uri.parse(path)
                    FileUtils.getFileFromContentUri(context, uri)
                } catch (e: Exception) {
                    Logger.e("ImageProcessingScreenViewModel", "Error parsing URI or creating file: ${e.message}")
                    null
                }
            }
            if (imageFile == null || !imageFile.exists()) {
                Logger.e("ImageProcessingScreenViewModel","Image file not found or cannot be accessed at path: $imagePath")
                _uiState.value = _uiState.value.copy(
                    showLoading = false,
                    errorMessage = "Could not access the selected image"
                )
                return@onSuccess
            }
            Logger.i("ImageProcessingScreenViewModel","Image file located at: ${imageFile.absolutePath}, size: ${imageFile.length()} bytes")

            val size = getDocumentWidthAndHeight(it.size)
            val width = size.width ?: 0f
            val height = size.height ?: 0f
            val unit = it.unit.ifEmpty { "mm" }
            val dpi = selectedDpi.toIntOrNull() ?: 300
            Logger.i("ImageProcessingScreenViewModel","Starting crop with size: $size width=$width, height=$height, unit=$unit, dpi=$dpi")
            cropImage(imageFile, width, height, unit, dpi)
        }
    }

    private fun loadState(isLoading: Boolean) {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = isLoading, errorMessage = null)
        }
    }


    fun cropImage(file: File, width: Float, height: Float, unit: String, dpi: Int) {
        viewModelScope.launch(dispatcher.io) {
            _loading.value = true
            _error.value = null

            var attempts = 0
            val maxAttempts = 3
            var success = false
            var apiResponse: ApiResponse? = null

            while (attempts < maxAttempts && !success) {
                try {
                    apiResponse = repository.cropImage(file, width, height, unit, dpi).getOrThrow()
                    if (apiResponse.filename == null) {
                        Logger.e("ImageProcessingScreenViewModel","cropImage: Server returned null filename")
                        _error.value = "Server returned null filename"
                        break
                    }
                    if (apiResponse.filename == lastCroppedUrl && attempts < maxAttempts - 1) {
                        Logger.w("ImageProcessingScreenViewModel","cropImage: Received same URL as last attempt, retrying... (attempt ${attempts + 1})")
                        attempts++
                        kotlinx.coroutines.delay(500)
                    } else {
                        lastCroppedUrl = apiResponse.filename
                        _uiState.value = _uiState.value.copy(
                            documentImage = apiResponse.filename,
                            showLoading = false
                        )
                        Logger.i("ImageProcessingScreenViewModel","Cropped image successfully: ${apiResponse.filename}")
                        success = true
                    }
                } catch (error: Throwable) {
                    Logger.e("ImageProcessingScreenViewModel","cropImage: Error during cropping: ${error.message}", error)
                    _error.value = error.message
                    break
                }
            }

            if (!success) {
                Logger.e("ImageProcessingScreenViewModel","cropImage: Failed to get new cropped image after $maxAttempts attempts")
                _error.value = "Failed to get new cropped image after $maxAttempts attempts"
            }
            _loading.value = false
        }
    }

    fun clearPreviousResults() {
        viewModelScope.launch(dispatcher.io) {
            _uiState.value = _uiState.value.copy(documentImage = null, errorMessage = null)
            _error.value = null
        }
    }


    private suspend fun getDocumentById(documentId: Int): Result<DocumentEntity> = getDocumentDetails(documentId)
}