package com.ots.aipassportphotomaker.presentation.ui.editimage

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aman.downloader.OziDownloader
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.utils.ColorUtils.parseColorFromString
import com.ots.aipassportphotomaker.common.utils.FileUtils
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.data.model.RemoverApiResponse
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.ProcessingStage
import com.ots.aipassportphotomaker.domain.model.SuitsEntity
import com.ots.aipassportphotomaker.domain.repository.ColorFactory
import com.ots.aipassportphotomaker.domain.repository.RemoveBackgroundRepository
import com.ots.aipassportphotomaker.domain.usecase.photoid.GetDocumentDetails
import com.ots.aipassportphotomaker.domain.util.DispatchersProvider
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.domain.util.Result
import com.ots.aipassportphotomaker.domain.util.getDocumentWidthAndHeight
import com.ots.aipassportphotomaker.domain.util.onError
import com.ots.aipassportphotomaker.domain.util.onSuccess
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import com.ots.aipassportphotomaker.presentation.ui.usecase.suits.GetSuitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@HiltViewModel
class EditImageScreenViewModel @Inject constructor(
    private val getDocumentDetails: GetDocumentDetails,
    editImageScreenBundle: EditImageScreenBundle,
    private val dispatcher: DispatchersProvider,
    private val networkMonitor: NetworkMonitor,
    private val removeBackgroundRepository: RemoveBackgroundRepository,
    private val oziDownloader: OziDownloader,
    val colorFactory: ColorFactory,
    private val getSuitsUseCase: GetSuitsUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    val suits: Flow<PagingData<SuitsEntity>> = getSuitsUseCase.suits(
        pageSize = 20
    ).cachedIn(viewModelScope)

    private val _uiState: MutableStateFlow<EditImageScreenUiState> =
        MutableStateFlow(EditImageScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<EditImageScreenNavigationState> =
        singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    private val _shouldRemoveBackground = MutableStateFlow(false)
    val shouldRemoveBackground = _shouldRemoveBackground.asStateFlow()

    private val _processingStage = MutableStateFlow(ProcessingStage.NONE)
    val processingStage = _processingStage.asStateFlow()

    val documentId: Int = editImageScreenBundle.documentId
    var imageUrl: String? = editImageScreenBundle.imageUrl
    val documentName: String = editImageScreenBundle.documentName
    val documentSize: String = editImageScreenBundle.documentSize
    val documentUnit: String = editImageScreenBundle.documentUnit
    val documentPixels: String = editImageScreenBundle.documentPixels
    val selectedColor: String? = editImageScreenBundle.selectedColor
    val sourceScreen: String = editImageScreenBundle.sourceScreen
    val editPosition: Int = editImageScreenBundle.editPosition
    val selectedDpi: String = editImageScreenBundle.selectedDpi

    private var mParsedColor = Color.Unspecified

    private val _localSelectedColor: MutableSharedFlow<Color> = singleSharedFlow()
    val localSelectedColor = _localSelectedColor.asSharedFlow()

    private var lastRemovedBgUrl: String? = null


    init {
        Logger.i(
            "EditImageScreenViewModel",
            " initialized with documentId: $documentId, imagePath: $imageUrl, selectedColor: $selectedColor, sourceScreen: $sourceScreen, editPosition: $editPosition, selectedDpi: $selectedDpi")
        if (sourceScreen == "HomeScreen") {
            loadState(false)
        } else {
            loadState(true)
        }

        onInitialState()
        observeSuitsLoadingState()
    }

    fun updateImageUrl(newImageUrl: String) {
        launch {
            imageUrl = newImageUrl
            _uiState.value = _uiState.value.copy(imageUrl = newImageUrl)
            Logger.i("EditImageScreenViewModel", "Updated image URL: $newImageUrl")
        }
    }

    private fun observeSuitsLoadingState() {
        launch {
            suits.collect { pagingData ->
                Logger.i("EditImageScreenViewModel", "Suits paging data updated: $pagingData")
                _uiState.update { currentState ->
                    currentState.copy(showLoading = false)
                }
            }
        }
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

        // custom size
        if (sourceScreen == "ImageProcessingScreen" && documentId == 0) {
            _shouldRemoveBackground.value = false
            loadState(isLoading = false)
            val parsedColor = parseColorFromString(selectedColor) ?: Color.Unspecified
            mParsedColor = parsedColor
            when (parsedColor) {
                Color.Transparent -> {
                    selectColor(parsedColor, ColorFactory.ColorType.TRANSPARENT)
                    Logger.i("EditImageScreenViewModel", "Parsed color is Transparent")
                }

                Color.Unspecified -> {
                    selectColor(parsedColor, ColorFactory.ColorType.TRANSPARENT)
                    Logger.i("EditImageScreenViewModel", "Parsed color is Unspecified")
                }

                Color.White -> {
                    selectColor(parsedColor, ColorFactory.ColorType.WHITE)
                    Logger.i("EditImageScreenViewModel", "Parsed color is White")
                }

                Color.Green -> {
                    selectColor(parsedColor, ColorFactory.ColorType.GREEN)
                    Logger.i("EditImageScreenViewModel", "Parsed color is Green")
                }

                Color.Blue -> {
                    selectColor(parsedColor, ColorFactory.ColorType.BLUE)
                    Logger.i("EditImageScreenViewModel", "Parsed color is Blue")
                }

                Color.Red -> {
                    selectColor(parsedColor, ColorFactory.ColorType.RED)
                    Logger.i("EditImageScreenViewModel", "Parsed color is Red")
                }

                else -> {
                    selectColor(parsedColor, ColorFactory.ColorType.CUSTOM)
                    Logger.i("EditImageScreenViewModel", "Parsed custom color: $parsedColor")
                }
            }
            _uiState.value = EditImageScreenUiState(
                showLoading = false,
                documentName = documentName,
                documentSize = documentSize,
                documentUnit = documentUnit,
                documentPixels = documentPixels,
                documentResolution = selectedDpi,
                documentImage = "",
                documentType = "custom",
                documentCompleted = "",
                selectedColor = parsedColor,
                imageUrl = imageUrl,
                editPosition = editPosition
            )

            val size = getDocumentWidthAndHeight(documentSize)
            _uiState.value = _uiState.value.copy(ratio = size.width / size.height)


            if (imageUrl.isNullOrEmpty()) {
                Logger.e("EditImageScreenViewModel", "No image path provided")
                _error.value = "No image was selected"
                return@launch
            }

            try {

                val width = size.width ?: 0f
                val height = size.height ?: 0f
                val unit = documentUnit.ifEmpty { "mm" }

                Logger.i(
                    "EditImageScreenViewModel",
                    "Starting crop with size: $size width=$width, height=$height, unit=$unit"
                )

            } catch (e: Exception) {
                Logger.e("EditImageScreenViewModel", "Error processing image: ${e.message}", e)
                _error.value = "Failed to process the selected image: ${e.message}"
            }

            return@launch
        }

        if (sourceScreen == "HomeScreen") {
            _shouldRemoveBackground.value = true
            if (imageUrl.isNullOrEmpty()) {
                Logger.e("EditImageScreenViewModel", "No image path provided from HomeScreen")
                _error.value = "No image was selected"
                loadState(isLoading = false)
                return@launch
            }

            val uri = Uri.parse(imageUrl)
            Logger.d("EditImageScreenViewModel", "Processing URI: $uri")

            val imageFile = FileUtils.uriToFile(context, uri)

            if (!imageFile.exists() || imageFile.length() == 0L) {
                Logger.e("EditImageScreenViewModel", "Image file is empty or does not exist")
                _error.value = "The selected image could not be processed"
                return@launch
            }

            Logger.i("EditImageScreenViewModel", "Image file exists: ${imageFile.path}, size: ${imageFile.length()} bytes")
            // Get image dimensions using BitmapFactory
            val options = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            android.graphics.BitmapFactory.decodeFile(imageFile.path, options)

            val imageWidth = options.outWidth
            val imageHeight = options.outHeight
            val aspectRatio = imageWidth.toFloat() / imageHeight.toFloat()

            Logger.i("EditImageScreenViewModel", "Image file exists: ${imageFile.path}, size: ${imageFile.length()} bytes")
            Logger.i("EditImageScreenViewModel", "Image dimensions: ${imageWidth}x${imageHeight}, ratio: $aspectRatio")

            _uiState.value = EditImageScreenUiState(
                showLoading = false,
                documentName = "Custom Image",
                documentSize = "${imageWidth} x ${imageHeight}",
                documentUnit = "px",
                documentPixels = "${imageWidth}x${imageHeight} px",
                documentResolution = selectedDpi ?: "72 dpi", // Default DPI
                documentImage = "",
                documentType = "custom",
                documentCompleted = "",
                selectedColor = Color.Unspecified,
                imageUrl = imageFile.path.toString(),
                ratio = imageWidth/imageHeight.toFloat(),
                sourceScreen = sourceScreen,
                editPosition = editPosition
            )

            return@launch
        }

        _shouldRemoveBackground.value = false
        getDocumentById(documentId).onSuccess { document ->
            Logger.i("EditImageScreenViewModel", "Fetched document details: $document")
            loadState(isLoading = false)
            val parsedColor = parseColorFromString(selectedColor) ?: Color.Unspecified
            mParsedColor = parsedColor
            when (parsedColor) {
                Color.Transparent -> {
                    selectColor(parsedColor, ColorFactory.ColorType.TRANSPARENT)
                    Logger.i("EditImageScreenViewModel", "Parsed color is Transparent")
                }

                Color.Unspecified -> {
                    selectColor(parsedColor, ColorFactory.ColorType.TRANSPARENT)
                    Logger.i("EditImageScreenViewModel", "Parsed color is Unspecified")
                }

                Color.White -> {
                    selectColor(parsedColor, ColorFactory.ColorType.WHITE)
                    Logger.i("EditImageScreenViewModel", "Parsed color is White")
                }

                Color.Green -> {
                    selectColor(parsedColor, ColorFactory.ColorType.GREEN)
                    Logger.i("EditImageScreenViewModel", "Parsed color is Green")
                }

                Color.Blue -> {
                    selectColor(parsedColor, ColorFactory.ColorType.BLUE)
                    Logger.i("EditImageScreenViewModel", "Parsed color is Blue")
                }

                Color.Red -> {
                    selectColor(parsedColor, ColorFactory.ColorType.RED)
                    Logger.i("EditImageScreenViewModel", "Parsed color is Red")
                }

                else -> {
                    selectColor(parsedColor, ColorFactory.ColorType.CUSTOM)
                    Logger.i("EditImageScreenViewModel", "Parsed custom color: $parsedColor")
                }
            }
            _uiState.value = EditImageScreenUiState(
                showLoading = false,
                documentName = document.name,
                documentSize = document.size,
                documentUnit = document.unit,
                documentPixels = document.pixels,
                documentResolution = selectedDpi ?: document.resolution,
                documentImage = document.image,
                documentType = document.type,
                documentCompleted = document.completed,
                selectedColor = parsedColor,
                imageUrl = imageUrl,
                editPosition = editPosition
            )

            val size = getDocumentWidthAndHeight(document.size)
            _uiState.value = _uiState.value.copy(ratio = size.width / size.height)


            if (imageUrl.isNullOrEmpty()) {
                Logger.e("EditImageScreenViewModel", "No image path provided")
                _error.value = "No image was selected"
                return@onSuccess
            }

            try {

                val width = size.width ?: 0f
                val height = size.height ?: 0f
                val unit = document.unit.ifEmpty { "mm" }

                Logger.i(
                    "EditImageScreenViewModel",
                    "Starting crop with size: $size width=$width, height=$height, unit=$unit"
                )

            } catch (e: Exception) {
                Logger.e("EditImageScreenViewModel", "Error processing image: ${e.message}", e)
                _error.value = "Failed to process the selected image: ${e.message}"
            }
        }.onError { error ->
            Logger.e(
                "EditImageScreenViewModel",
                "Failed to fetch document: ${error.message}",
                error
            )
            _error.value = "Failed to load document details"
        }
    }

    fun removeBackground(file: File) {

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
            var apiResponse: RemoverApiResponse? = null

            while (attempts < maxAttempts && !success) {
                try {
                    delay(1500L)
                    _processingStage.value = ProcessingStage.PROCESSING

                    apiResponse = removeBackgroundRepository.removeBackground(file).getOrThrow()

                    delay(2000)
                    _processingStage.value = ProcessingStage.BACKGROUND_REMOVAL

                    if (apiResponse.imageUrl == null) {
                        Logger.e("EditImageScreenViewModel","remove background: Server returned null filename")
                        _error.value = "Server returned null filename"
                        break
                    }
                    if (apiResponse.imageUrl == lastRemovedBgUrl && attempts < maxAttempts - 1) {
                        Logger.w("EditImageScreenViewModel","remove background: Received same URL as last attempt, retrying... (attempt ${attempts + 1})")
                        attempts++
                        delay(500)
                    } else {
                        delay(1500)
                        _processingStage.value = ProcessingStage.SAVING_IMAGE

                        val localImagePath = saveImageToLocalStorage(apiResponse.imageUrl)
                        if (localImagePath != null) {
                            lastRemovedBgUrl = localImagePath
                            _uiState.value = _uiState.value.copy(
                                isBgRemoved = true,
                                imageUrl = localImagePath,
                                showLoading = false
                            )
                            _shouldRemoveBackground.value = false
                            Logger.i("EditImageScreenViewModel","Background removed successfully: ${apiResponse.imageUrl}")
                            success = true

                            delay(500)
                            _processingStage.value = ProcessingStage.COMPLETED
                            delay(100)
                            _processingStage.value = ProcessingStage.NONE
                        } else {
                            Logger.e("EditImageScreenViewModel","remove background: Failed to save image locally")
                            _error.value = "Failed to save image locally"
                            _processingStage.value = ProcessingStage.ERROR
                        }
                    }
                } catch (error: retrofit2.HttpException) {
                    val errorCode = error.code()
                    val errorBody = error.response()?.errorBody()?.string() ?: "No details"
                    Logger.e("EditImageScreenViewModel", "cropImage: Error during cropping: HTTP $errorCode - $errorBody", error)
                    _error.value = "HTTP $errorCode: $errorBody"
                    _processingStage.value = ProcessingStage.ERROR

                    break
                } catch (error: Throwable) {
                    Logger.e("EditImageScreenViewModel", "cropImage: Error during cropping: ${error.message}", error)
                    Logger.i("EditImageScreenViewModel","user image: ${file.absolutePath}")
                    _error.value = error.message
                    _processingStage.value = ProcessingStage.ERROR

                    break
                }
            }

            if (!success) {
                Logger.e("EditImageScreenViewModel","cropImage: Failed to get new cropped image after $maxAttempts attempts")
                _processingStage.value = ProcessingStage.ERROR
                _error.value = "Failed to get new cropped image after $maxAttempts attempts"
            }
            _loading.value = false
        }
    }

    fun selectColor(color: Color, colorType: ColorFactory.ColorType) {
        _uiState.value = _uiState.value.copy(selectedColor = color)
        colorFactory.selectColor(colorType)
        _localSelectedColor.tryEmit(color)

        if (sourceScreen == "HomeScreen" &&
            !_uiState.value.isBgRemoved &&
            _shouldRemoveBackground.value) {

            // Get the file from the current image URL
            val imageFile = imageUrl?.let { File(it) }
            if (imageFile != null && imageFile.exists()) {
                removeBackground(imageFile)
                // Set flag to false immediately after initiating removal
                // to prevent multiple removals
                _shouldRemoveBackground.value = false
            }
        }
    }

    fun requestBackgroundRemoval() {
        _shouldRemoveBackground.value = true
    }

    fun setCustomColor(color: Color) {
        _uiState.value = _uiState.value.copy(selectedColor = color)
        colorFactory.setCustomColor(color)
        _localSelectedColor.tryEmit(colorFactory.selectedColor)
    }

    fun onCutoutClicked() {
        _navigationState.tryEmit(
            EditImageScreenNavigationState.CutOutScreen(
                documentId = documentId,
                imageUrl = imageUrl,
                selectedBackgroundColor = mParsedColor,
                sourceScreen = "EditImageScreen"
            )
        )
    }

    fun onImageSaved(imagePath: String) {

        Logger.i("EditImageScreenViewModel", "Image saved at path: $imagePath")
        _uiState.value = uiState.value.copy(imagePath = imagePath)
        _navigationState.tryEmit(
            EditImageScreenNavigationState.SavedImageScreen(
                documentId = documentId,
                imagePath = imagePath,
                selectedDpi = selectedDpi

            )
        )
    }

    private fun loadState(isLoading: Boolean) {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = isLoading, errorMessage = null)
        }
    }


    private suspend fun getDocumentById(documentId: Int): Result<DocumentEntity> =
        getDocumentDetails(documentId)

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
            val fileName = FileUtils.TEMP_FILE_NAME

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

}