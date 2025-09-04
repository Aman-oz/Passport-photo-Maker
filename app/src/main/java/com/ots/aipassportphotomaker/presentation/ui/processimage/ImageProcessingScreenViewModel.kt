package com.ots.aipassportphotomaker.presentation.ui.processimage

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.utils.ColorUtils.parseColorFromString
import com.ots.aipassportphotomaker.common.utils.FileUtils
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.data.model.ApiResponse
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.ProcessingStage
import com.ots.aipassportphotomaker.domain.repository.CropImageRepository
import com.ots.aipassportphotomaker.domain.usecase.photoid.GetDocumentDetails
import com.ots.aipassportphotomaker.domain.util.DispatchersProvider
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.domain.util.Result
import com.ots.aipassportphotomaker.domain.util.getDocumentWidthAndHeight
import com.ots.aipassportphotomaker.domain.util.onError
import com.ots.aipassportphotomaker.domain.util.onSuccess
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.NoInternetConnectionBanner
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentDetailsBundle
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentInfoScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.documentinfo.DocumentInfoScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.sequences.ifEmpty

// Created by amanullah on 27/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@HiltViewModel
class ImageProcessingScreenViewModel @Inject constructor(
    private val getDocumentDetails: GetDocumentDetails,
    imageProcessingBundle: ImageProcessingBundle,
    private val repository: CropImageRepository,
    private val dispatcher: DispatchersProvider,
    private val networkMonitor: NetworkMonitor,
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

    private val _processingStage = MutableStateFlow(ProcessingStage.NONE)
    val processingStage = _processingStage.asStateFlow()

    val documentId: Int = imageProcessingBundle.documentId
    val imagePath: String? = imageProcessingBundle.imagePath
    val selectedDpi: String = imageProcessingBundle.selectedDpi
    val selectedColor: String? = imageProcessingBundle.selectedColor


    private var lastCroppedUrl: String? = null
    var isPortrait: Boolean = true

    init {
        Logger.i("ImageProcessingScreenViewModel"," initialized with documentId: $documentId, imagePath: $imagePath, selectedDpi: $selectedDpi, selectedColor: $selectedColor")
        onInitialState()
        loadState(true)
    }

    private fun onInitialState() = launch {
        getDocumentById(documentId).onSuccess { document ->
            Logger.i("ImageProcessingScreenViewModel", "Fetched document details: $document")
            loadState(isLoading = false)
            val parsedColor = parseColorFromString(selectedColor) ?: Color.Unspecified
            _uiState.value = ImageProcessingScreenUiState(
                showLoading = false,
                documentName = document.name,
                documentSize = document.size,
                documentUnit = document.unit,
                documentPixels = document.pixels,
                documentResolution = document.resolution,
                documentImage = document.image,
                documentType = document.type,
                documentCompleted = document.completed,
                selectedColor = parsedColor
            )

            val size = getDocumentWidthAndHeight(document.size)
            isPortrait = size.height >= size.width

            if (imagePath.isNullOrEmpty()) {
                Logger.e("ImageProcessingScreenViewModel", "No image path provided")
                _error.value = "No image was selected"
                return@onSuccess
            }

            try {
                val uri = Uri.parse(imagePath)
                Logger.d("ImageProcessingScreenViewModel", "Processing URI: $uri")

                val imageFile = FileUtils.uriToFile(context, uri)

                if (!imageFile.exists() || imageFile.length() == 0L) {
                    Logger.e("ImageProcessingScreenViewModel", "Image file is empty or does not exist")
                    _error.value = "The selected image could not be processed"
                    return@onSuccess
                }

                Logger.i("ImageProcessingScreenViewModel", "Image file prepared: ${imageFile.absolutePath}, size: ${imageFile.length()} bytes")

                val width = size.width ?: 0f
                val height = size.height ?: 0f
                val unit = document.unit.ifEmpty { "mm" }
                val dpi = selectedDpi.toIntOrNull() ?: 300

                Logger.i("ImageProcessingScreenViewModel", "Starting crop with size: $size width=$width, height=$height, unit=$unit, dpi=$dpi")
                cropImage(imageFile, width, height, unit, dpi)
            } catch (e: Exception) {
                Logger.e("ImageProcessingScreenViewModel", "Error processing image: ${e.message}", e)
                _error.value = "Failed to process the selected image: ${e.message}"
            }
        }.onError { error ->
            Logger.e("ImageProcessingScreenViewModel", "Failed to fetch document: ${error.message}", error)
            _error.value = "Failed to load document details"
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
            _processingStage.value = ProcessingStage.UPLOADING

            val networkState = networkMonitor.networkState.first()

            if (networkState.isOnline.not()) {
                _error.value = "No internet connection. Please check your network settings."
                _loading.value = false
                _processingStage.value = ProcessingStage.NO_NETWORK_AVAILABLE
                return@launch
            }

            var attempts = 0
            val maxAttempts = 3
            var success = false
            var apiResponse: ApiResponse? = null

            while (attempts < maxAttempts && !success) {
                try {
                    delay(1500L)
                    _processingStage.value = ProcessingStage.PROCESSING

                    apiResponse = repository.cropImage(file, width, height, unit, dpi).getOrThrow()

                    delay(2000)
                    _processingStage.value = ProcessingStage.BACKGROUND_REMOVAL

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
                        delay(1500)
                        _processingStage.value = ProcessingStage.COMPLETED

                        lastCroppedUrl = apiResponse.filename
                        _uiState.value = _uiState.value.copy(
                            finalImageUrl = apiResponse.filename,
                            showLoading = false
                        )
                        Logger.i("ImageProcessingScreenViewModel","Cropped image successfully: ${apiResponse.filename}")
                        success = true

                        delay(1000)

                        onImageCropped()
                    }
                } catch (error: retrofit2.HttpException) {
                    val errorCode = error.code()
                    val errorBody = error.response()?.errorBody()?.string() ?: "No details"
                    Logger.e("ImageProcessingScreenViewModel", "cropImage: Error during cropping: HTTP $errorCode - $errorBody", error)
                    _error.value = "HTTP $errorCode: $errorBody"
                    _processingStage.value = ProcessingStage.NONE
                    break
                } catch (error: Throwable) {
                    Logger.e("ImageProcessingScreenViewModel", "cropImage: Error during cropping: ${error.message}", error)
                    _error.value = error.message
                    _processingStage.value = ProcessingStage.NONE
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

    fun onImageCropped() {
        Logger.i("DocumentInfoScreenViewModel", "onCreatePhotoClicked: final image url = $lastCroppedUrl")
        _navigationState.tryEmit(
            ImageProcessingScreenNavigationState.EditImageScreen(
                documentId = documentId,
                imageUrl = lastCroppedUrl,
                selectedBackgroundColor = uiState.value.selectedColor

            ))
    }

    fun clearPreviousResults() {
        viewModelScope.launch(dispatcher.io) {
            _uiState.value = _uiState.value.copy(documentImage = null, errorMessage = null)
            _error.value = null
        }
    }


    private suspend fun getDocumentById(documentId: Int): Result<DocumentEntity> = getDocumentDetails(documentId)
}