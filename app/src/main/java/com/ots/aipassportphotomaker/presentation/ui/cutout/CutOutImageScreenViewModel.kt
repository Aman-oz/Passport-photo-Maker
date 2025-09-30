package com.ots.aipassportphotomaker.presentation.ui.cutout

import android.app.Activity
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.aman.downloader.OziDownloader
import com.ots.aipassportphotomaker.adsmanager.admob.MyAdsManager
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.managers.PreferencesHelper
import com.ots.aipassportphotomaker.common.utils.AdsConstants
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.common.utils.ColorUtils.parseColorFromString
import com.ots.aipassportphotomaker.common.utils.FileUtils
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.data.model.RemoverApiResponse
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.ProcessingStage
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
import java.io.File
import javax.inject.Inject
import kotlin.text.ifEmpty

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@HiltViewModel
class CutOutImageScreenViewModel @Inject constructor(
    private val getDocumentDetails: GetDocumentDetails,
    cutOutImageScreenBundle: CutOutImageScreenBundle,
    private val dispatcher: DispatchersProvider,
    private val networkMonitor: NetworkMonitor,
    private val removeBackgroundRepository: RemoveBackgroundRepository,
    private val oziDownloader: OziDownloader,
    @ApplicationContext private val context: Context,
    private val adsManager: MyAdsManager,
    private val analyticsManager: AnalyticsManager,
    private val preferencesHelper: PreferencesHelper
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

    private val _processingStage = MutableStateFlow(ProcessingStage.NONE)
    val processingStage = _processingStage.asStateFlow()

    private var lastRemovedBgUrl: String? = null

    val documentId: Int = cutOutImageScreenBundle.documentId
    val imageUrl: String? = cutOutImageScreenBundle.imageUrl
    val selectedColor: String? = cutOutImageScreenBundle.selectedColor
    val sourceScreen: String = cutOutImageScreenBundle.sourceScreen

    init {
        if (sourceScreen != "HomeScreen") {
            loadState(false)
        }else{
            loadState(true)
        }
        onInitialState()
        initSourceScreen()
    }

    private fun initSourceScreen() {
        Logger.i("CutOutImageScreenViewModel", "Source screen: $sourceScreen")
        _uiState.value = _uiState.value.copy(sourceScreen = sourceScreen)
    }

    private fun onInitialState() = launch {
        analyticsManager.sendAnalytics(AnalyticsConstants.OPENED, "CutOutImageScreen")

        Logger.i("CutOutImageScreenViewModel", "onInitialState called with documentId: $documentId, imageUrl: $imageUrl, selectedColor: $selectedColor, sourceScreen: $sourceScreen")
        if (sourceScreen == "HomeScreen" || documentId == 0) {
            if (imageUrl.isNullOrEmpty()) {
                Logger.e("CutOutImageScreenViewModel", "No image path provided from HomeScreen")
                _error.value = "No image was selected"
                loadState(false)
                return@launch
            }

            _uiState.value = CutOutImageScreenUiState(
                showLoading = false,
                documentName = "",
                documentSize = "",
                documentUnit = "",
                documentPixels = "",
                documentResolution = "",
                documentImage = "",
                documentType = "",
                documentCompleted = "",
                selectedColor = Color.Unspecified,
                imageUrl = imageUrl
            )
            loadState(false)
            return@launch
        }

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
                        Logger.e("CutOutImageScreenViewModel","remove background: Server returned null filename")
                        _error.value = "Server returned null filename"
                        break
                    }
                    if (apiResponse.imageUrl == lastRemovedBgUrl && attempts < maxAttempts - 1) {
                        Logger.w("CutOutImageScreenViewModel","remove background: Received same URL as last attempt, retrying... (attempt ${attempts + 1})")
                        attempts++
                        delay(500)
                    } else {
                        delay(1500)
                        _processingStage.value = ProcessingStage.SAVING_IMAGE

                        val localImagePath = saveImageToLocalStorage(apiResponse.imageUrl)
                        if (localImagePath != null) {
                            lastRemovedBgUrl = localImagePath
                            _uiState.value = _uiState.value.copy(
                                imageUrl = localImagePath,
                                showLoading = false
                            )
                            Logger.i("CutOutImageScreenViewModel","Background removed successfully: ${apiResponse.imageUrl}")
                            success = true

                            delay(500)
                            _processingStage.value = ProcessingStage.COMPLETED
                            delay(100)
                            _processingStage.value = ProcessingStage.NONE
                        } else {
                            Logger.e("CutOutImageScreenViewModel","remove background: Failed to save image locally")
                            _error.value = "Failed to save image locally"
                            _processingStage.value = ProcessingStage.ERROR
                        }
                    }
                } catch (error: retrofit2.HttpException) {
                    val errorCode = error.code()
                    val errorBody = error.response()?.errorBody()?.string() ?: "No details"
                    Logger.e("CutOutImageScreenViewModel", "cropImage: Error during cropping: HTTP $errorCode - $errorBody", error)
                    _error.value = "HTTP $errorCode: $errorBody"
                    _processingStage.value = ProcessingStage.ERROR

                    break
                } catch (error: Throwable) {
                    Logger.e("CutOutImageScreenViewModel", "cropImage: Error during cropping: ${error.message}", error)
                    Logger.i("CutOutImageScreenViewModel","user image: ${file.absolutePath}")
                    _error.value = error.message
                    _processingStage.value = ProcessingStage.ERROR

                    break
                }
            }

            if (!success) {
                Logger.e("CutOutImageScreenViewModel","cropImage: Failed to get new cropped image after $maxAttempts attempts")
                _processingStage.value = ProcessingStage.ERROR
                _error.value = "Failed to get new cropped image after $maxAttempts attempts"
            }
            _loading.value = false
        }
    }

    fun onBackgroundRemovedAi() {
        Logger.i("CutOutImageScreenViewModel", "AI Background removed image at path: $lastRemovedBgUrl")
        _uiState.value = _uiState.value.copy(imageUrl = lastRemovedBgUrl)
    }

    fun onImageSaved(imagePath: String) {

        Logger.i("CutOutImageScreenViewModel", "Image saved at path: $imagePath")
        _uiState.value = _uiState.value.copy(imageUrl = imagePath)

        _navigationState.tryEmit(
            CutOutImageScreenNavigationState.SavedImageScreen(
                documentId = documentId,
                imagePath = imagePath,
                sourceScreen = "CutOutImageScreen"
            ))
    }

    private fun loadState(isLoading: Boolean) {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = isLoading, errorMessage = null)
        }
    }


    private suspend fun getDocumentById(documentId: Int): Result<DocumentEntity> = getDocumentDetails(documentId)

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

    fun showInterstitialAd(activity: Activity, onAdClosed: (Boolean) -> Unit) {
        adsManager.showInterstitial(activity, true) { isAdShown ->
            if (isAdShown == true) {
                onAdClosed.invoke(true)
            } else {
                onAdClosed.invoke(false)
            }
        }
    }

    fun isPremiumUser(): Boolean {
        return preferencesHelper.getBoolean(AdsConstants.IS_NO_ADS_ENABLED, false)
    }

    fun sendEvent(eventName: String, eventValue: String) {
        analyticsManager.sendAnalytics(eventName, eventValue)
    }
}