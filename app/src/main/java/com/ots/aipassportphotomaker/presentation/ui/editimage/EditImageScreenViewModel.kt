package com.ots.aipassportphotomaker.presentation.ui.editimage

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.utils.ColorUtils.parseColorFromString
import com.ots.aipassportphotomaker.common.utils.FileUtils
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.SuitsEntity
import com.ots.aipassportphotomaker.domain.repository.ColorFactory
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@HiltViewModel
class EditImageScreenViewModel @Inject constructor(
    private val getDocumentDetails: GetDocumentDetails,
    editImageScreenBundle: EditImageScreenBundle,
    private val dispatcher: DispatchersProvider,
    private val networkMonitor: NetworkMonitor,
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

    val documentId: Int = editImageScreenBundle.documentId
    var imageUrl: String? = editImageScreenBundle.imageUrl
    val selectedColor: String? = editImageScreenBundle.selectedColor
    val sourceScreen: String = editImageScreenBundle.sourceScreen
    val editPosition: Int = editImageScreenBundle.editPosition
    val selectedDpi: String = editImageScreenBundle.selectedDpi

    private var mParsedColor = Color.Unspecified

    private val _localSelectedColor: MutableSharedFlow<Color> = singleSharedFlow()
    val localSelectedColor = _localSelectedColor.asSharedFlow()

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

        if (sourceScreen == "HomeScreen") {
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

    fun selectColor(color: Color, colorType: ColorFactory.ColorType) {
        _uiState.value = _uiState.value.copy(selectedColor = color)
        colorFactory.selectColor(colorType)
        _localSelectedColor.tryEmit(color)
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

}