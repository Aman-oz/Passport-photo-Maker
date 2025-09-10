package com.ots.aipassportphotomaker.presentation.ui.processimage

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aman.downloader.OziDownloader
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.utils.ColorUtils.parseColorFromString
import com.ots.aipassportphotomaker.common.utils.FileUtils
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.data.model.ApiResponse
import com.ots.aipassportphotomaker.data.model.RemoverApiResponse
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.ProcessingStage
import com.ots.aipassportphotomaker.domain.repository.CropImageRepository
import com.ots.aipassportphotomaker.domain.repository.RemoveBackgroundRepository
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

// Created by amanullah on 27/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@HiltViewModel
class ImageProcessingScreenViewModel @Inject constructor(
    private val getDocumentDetails: GetDocumentDetails,
    imageProcessingBundle: ImageProcessingBundle,
    private val repository: CropImageRepository,
    private val removeBackgroundRepository: RemoveBackgroundRepository,
    private val dispatcher: DispatchersProvider,
    private val networkMonitor: NetworkMonitor,
    private val oziDownloader: OziDownloader,
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

    private val _removerResult = MutableStateFlow<RemoverApiResponse?>(null)
    val removerResult = _removerResult.asStateFlow()

    private val _uploadResult = MutableLiveData<kotlin.Result<RemoverApiResponse>>()
    val uploadResult: LiveData<kotlin.Result<RemoverApiResponse>> get() = _uploadResult

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: MutableStateFlow<Boolean> get() = _isLoading

    val documentId: Int = imageProcessingBundle.documentId
    val imagePath: String? = imageProcessingBundle.imagePath
    val filePath: String? = imageProcessingBundle.filePath
    val selectedDpi: String = imageProcessingBundle.selectedDpi
    val selectedColor: String? = imageProcessingBundle.selectedColor


    private var lastCroppedUrl: String? = null
    var isPortrait: Boolean = true

    init {
        Logger.i("ImageProcessingScreenViewModel"," initialized with documentId: $documentId, imagePath: $imagePath, filePath: $filePath selectedDpi: $selectedDpi, selectedColor: $selectedColor")
        onInitialState()
        loadState(true)
        if (imagePath != null) {
            updateCurrentImagePath(imagePath)
        } else {
            _error.value = "No image path provided"
        }
    }

    private fun updateCurrentImagePath(newPath: String) {
        viewModelScope.launch(dispatcher.io) {
            _uiState.value = _uiState.value.copy(currentImagePath = newPath)
        }
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

//                uploadFile(imageFile)
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
                    _processingStage.value = ProcessingStage.CROPPING_IMAGE

                    if (apiResponse.filename == null) {
                        Logger.e("ImageProcessingScreenViewModel","cropImage: Server returned null filename")
                        _error.value = "Server returned null filename"
                        break
                    }
                    if (apiResponse.filename == lastCroppedUrl && attempts < maxAttempts - 1) {
                        Logger.w("ImageProcessingScreenViewModel","cropImage: Received same URL as last attempt, retrying... (attempt ${attempts + 1})")
                        attempts++
                        delay(500)
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
                    _processingStage.value = ProcessingStage.ERROR
                    lastCroppedUrl = imagePath
                    _uiState.value = _uiState.value.copy(
                        finalImageUrl = imagePath,
                        showLoading = false
                    )
                    /*lastCroppedUrl?.let { path ->

                        uploadFile(File(path))
                    }*/
                    onImageCropped()
                    break
                } catch (error: Throwable) {
                    Logger.e("ImageProcessingScreenViewModel", "cropImage: Error during cropping: ${error.message}", error)
                    Logger.i("ImageProcessingScreenViewModel","user image: ${file.absolutePath}")
                    _error.value = error.message
                    _processingStage.value = ProcessingStage.ERROR
                    /*lastCroppedUrl = imagePath
                    _uiState.value = _uiState.value.copy(
                        finalImageUrl = imagePath,
                        showLoading = false
                    )
                    onImageCropped()*/
                    break
                }
            }

            if (!success) {
                Logger.e("ImageProcessingScreenViewModel","cropImage: Failed to get new cropped image after $maxAttempts attempts")
                _processingStage.value = ProcessingStage.ERROR
                _error.value = "Failed to get new cropped image after $maxAttempts attempts"
            }
            _loading.value = false
        }
    }

    fun removeBackground(file: File) {

        viewModelScope.launch(dispatcher.io) {
            _loading.value = true
            _error.value = null
            _processingStage.value = ProcessingStage.BACKGROUND_REMOVAL

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
            var apiResponse: RemoverApiResponse? = null

            while (attempts < maxAttempts && !success) {
                try {
                    delay(1500L)
                    _processingStage.value = ProcessingStage.PROCESSING

                    apiResponse = removeBackgroundRepository.removeBackground(file).getOrThrow()

                    delay(2000)
                    _processingStage.value = ProcessingStage.BACKGROUND_REMOVAL

                    if (apiResponse.imageUrl == null) {
                        Logger.e("ImageProcessingScreenViewModel","remove background: Server returned null filename")
                        _error.value = "Server returned null filename"
                        break
                    }
                    if (apiResponse.imageUrl == lastCroppedUrl && attempts < maxAttempts - 1) {
                        Logger.w("ImageProcessingScreenViewModel","remove background: Received same URL as last attempt, retrying... (attempt ${attempts + 1})")
                        attempts++
                        delay(500)
                    } else {
                        delay(1500)
                        _processingStage.value = ProcessingStage.COMPLETED

                        lastCroppedUrl = apiResponse.imageUrl
                        _uiState.value = _uiState.value.copy(
                            finalImageUrl = apiResponse.imageUrl,
                            showLoading = false
                        )
                        Logger.i("ImageProcessingScreenViewModel","Background removed successfully: ${apiResponse.imageUrl}")
                        success = true

                        delay(1000)

                        onBackgroundRemoved()
                    }
                } catch (error: retrofit2.HttpException) {
                    val errorCode = error.code()
                    val errorBody = error.response()?.errorBody()?.string() ?: "No details"
                    Logger.e("ImageProcessingScreenViewModel", "cropImage: Error during cropping: HTTP $errorCode - $errorBody", error)
                    _error.value = "HTTP $errorCode: $errorBody"
                    _processingStage.value = ProcessingStage.ERROR
                    /*lastCroppedUrl = imagePath
                    _uiState.value = _uiState.value.copy(
                        finalImageUrl = imagePath,
                        showLoading = false
                    )

                    onImageCropped()*/
                    break
                } catch (error: Throwable) {
                    Logger.e("ImageProcessingScreenViewModel", "cropImage: Error during cropping: ${error.message}", error)
                    Logger.i("ImageProcessingScreenViewModel","user image: ${file.absolutePath}")
                    _error.value = error.message
                    _processingStage.value = ProcessingStage.ERROR
                    /*lastCroppedUrl = imagePath
                    _uiState.value = _uiState.value.copy(
                        finalImageUrl = imagePath,
                        showLoading = false
                    )
                    onImageCropped()*/
                    break
                }
            }

            if (!success) {
                Logger.e("ImageProcessingScreenViewModel","cropImage: Failed to get new cropped image after $maxAttempts attempts")
                _processingStage.value = ProcessingStage.ERROR
                _error.value = "Failed to get new cropped image after $maxAttempts attempts"
            }
            _loading.value = false
        }
    }

    fun onImageCropped() {
        Logger.i("DocumentInfoScreenViewModel", "onImageCropped: final image url = $lastCroppedUrl")
        viewModelScope.launch(dispatcher.io) {
            try {
                _loading.value = true
                _processingStage.value = ProcessingStage.SAVING_IMAGE
                // Save the image to local storage
                val localImagePath = saveImageToLocalStorage(lastCroppedUrl)

                if (localImagePath != null) {
                    Logger.i("ImageProcessingScreenViewModel", "Image saved locally at: $localImagePath")
                    updateCurrentImagePath(localImagePath)
                    removeBackground(File(localImagePath))
                    // Update the navigation with the local file path
                    /*withContext(dispatcher.main) {
                        _navigationState.tryEmit(
                            ImageProcessingScreenNavigationState.EditImageScreen(
                                documentId = documentId,
                                imageUrl = localImagePath.toString(),
                                selectedBackgroundColor = uiState.value.selectedColor
                            )
                        )
                    }*/
                } else {
                    _error.value = "Failed to save image locally"
                }
            } catch (e: Exception) {
                Logger.e("ImageProcessingScreenViewModel", "Error in onImageCropped: ${e.message}", e)
                _error.value = "Failed to process image: ${e.message}"
                _processingStage.value = ProcessingStage.ERROR
            } finally {
                _loading.value = false
                _processingStage.value = ProcessingStage.COMPLETED
            }
        }
        /*_navigationState.tryEmit(
            ImageProcessingScreenNavigationState.EditImageScreen(
                documentId = documentId,
                imageUrl = lastCroppedUrl,
                selectedBackgroundColor = uiState.value.selectedColor

            ))*/
    }

    fun onBackgroundRemoved() {
        Logger.i("DocumentInfoScreenViewModel", "onImageCropped: final image url = $lastCroppedUrl")
        viewModelScope.launch(dispatcher.io) {
            try {
                _loading.value = true
                _processingStage.value = ProcessingStage.COMPLETED
                // Save the image to local storage
                val localImagePath = saveImageToLocalStorage(lastCroppedUrl)

                if (localImagePath != null) {
                    Logger.i("ImageProcessingScreenViewModel", "Image saved locally at: $localImagePath")

                    updateCurrentImagePath(localImagePath)
                    // Update the navigation with the local file path
                    withContext(dispatcher.main) {
                        _navigationState.tryEmit(
                            ImageProcessingScreenNavigationState.EditImageScreen(
                                documentId = documentId,
                                imageUrl = localImagePath.toString(),
                                selectedBackgroundColor = uiState.value.selectedColor
                            )
                        )
                    }
                } else {
                    _error.value = "Failed to save image locally"
                }
            } catch (e: Exception) {
                Logger.e("ImageProcessingScreenViewModel", "Error in onImageCropped: ${e.message}", e)
                _error.value = "Failed to process image: ${e.message}"
                _processingStage.value = ProcessingStage.ERROR
            } finally {
                _loading.value = false
                _processingStage.value = ProcessingStage.COMPLETED
            }
        }
        /*_navigationState.tryEmit(
            ImageProcessingScreenNavigationState.EditImageScreen(
                documentId = documentId,
                imageUrl = lastCroppedUrl,
                selectedBackgroundColor = uiState.value.selectedColor

            ))*/
    }

    /**
     * Downloads an image from a URL and saves it to the app's private storage
     * Returns the local file path if successful
     */
    fun saveImageToLocalStorage(imageUrl: String?): String? {
        if (imageUrl.isNullOrEmpty()) {
            Logger.e("ImageProcessingScreenViewModel", "saveImageToLocalStorage: URL is null or empty")
            return null
        }

        try {
            // Generate a unique filename
            val fileName = "cropped_${System.currentTimeMillis()}.jpg"

            // Create directory if it doesn't exist
            val cacheDir = File(context.filesDir, "images")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            val filePath = File(cacheDir, fileName).absolutePath

            // Build the download request
            val request = oziDownloader.newRequestBuilder(
                imageUrl, cacheDir.absolutePath, fileName
            ).tag("ImageProcessing").build()

            // Synchronously wait for download completion
            var resultPath: String? = null
            var downloadError: String? = null

            val latch = java.util.concurrent.CountDownLatch(1)

            oziDownloader.enqueue(
                request,
                onStart = {
                    Logger.d("ImageProcessingScreenViewModel", "Download started")
                    _processingStage.value = ProcessingStage.DOWNLOADING
                },
                onProgress = { progress ->
                    Logger.d("ImageProcessingScreenViewModel", "Download progress: $progress%")
                },
                onCompleted = {
                    Logger.i("ImageProcessingScreenViewModel", "Download completed: $filePath")
                    resultPath = filePath
                    latch.countDown()
                },
                onError = { error ->
                    Logger.e("ImageProcessingScreenViewModel", "Download error: $error")
                    downloadError = error
                    latch.countDown()
                }
            )

            // Wait for completion with timeout
            if (!latch.await(30, java.util.concurrent.TimeUnit.SECONDS)) {
                Logger.e("ImageProcessingScreenViewModel", "Download timed out")
                return null
            }

            if (downloadError != null) {
                Logger.e("ImageProcessingScreenViewModel", "Download failed: $downloadError")
                return null
            }

            return resultPath
        } catch (e: Exception) {
            Logger.e("ImageProcessingScreenViewModel", "Error saving image: ${e.message}", e)
            return null
        }
    }

    fun downloadImage(imageUrl: String?) {
        if (imageUrl.isNullOrEmpty()) {
            Logger.e("ImageProcessingScreenViewModel", "downloadImage: URL is null or empty")
            _error.value = "Image URL is empty"
            return
        }

        try {
            // Generate a unique filename
            val fileName = "image_${System.currentTimeMillis()}.jpg"

            // Create directory if it doesn't exist
            val imageDir = File(context.filesDir, "images")
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }

            val filePath = File(imageDir, fileName).absolutePath

            // Build the download request
            val request = oziDownloader.newRequestBuilder(
                imageUrl, imageDir.absolutePath, fileName
            ).tag("ImageDownload").build()

            oziDownloader.enqueue(
                request,
                onStart = {
                    Logger.d("ImageProcessingScreenViewModel", "Download started")
                    _loading.value = true
                },
                onProgress = { progress ->
                    Logger.d("ImageProcessingScreenViewModel", "Download progress: $progress%")
                    _processingStage.value = ProcessingStage.DOWNLOADING
                },
                onCompleted = {
                    Logger.i("ImageProcessingScreenViewModel", "Download completed: $filePath")
                    _loading.value = false
                    _processingStage.value = ProcessingStage.COMPLETED
                    // Handle the downloaded file
                    _uiState.value = _uiState.value.copy(finalImageUrl = filePath)
                },
                onError = { error ->
                    Logger.e("ImageProcessingScreenViewModel", "Download error: $error")
                    _loading.value = false
                    _error.value = "Download failed: $error"
                    _processingStage.value = ProcessingStage.ERROR
                }
            )
        } catch (e: Exception) {
            Logger.e("ImageProcessingScreenViewModel", "Error in downloadImage: ${e.message}", e)
            _loading.value = false
            _error.value = "Failed to download image: ${e.message}"
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