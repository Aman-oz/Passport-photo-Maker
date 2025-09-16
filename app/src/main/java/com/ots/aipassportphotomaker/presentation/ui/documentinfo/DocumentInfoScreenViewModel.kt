package com.ots.aipassportphotomaker.presentation.ui.documentinfo

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.CustomDocumentData
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.repository.ColorFactory
import com.ots.aipassportphotomaker.domain.usecase.photoid.GetDocumentDetails
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.ots.aipassportphotomaker.domain.util.Result
import com.ots.aipassportphotomaker.domain.util.onSuccess
import com.ots.aipassportphotomaker.image_picker.model.AssetInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class DocumentInfoScreenViewModel @Inject constructor(
    private val getDocumentDetails: GetDocumentDetails,
    documentDetailsBundle: DocumentDetailsBundle,
    val colorFactory: ColorFactory,
) : BaseViewModel() {

    private val _uiState: MutableStateFlow<DocumentInfoScreenUiState> = MutableStateFlow(DocumentInfoScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<DocumentInfoScreenNavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _selectedColor: MutableSharedFlow<Color> = singleSharedFlow()
    val selectedColor = _selectedColor.asSharedFlow()

    val selectedImagesList = mutableStateListOf<AssetInfo>()
    var selectedDpi: String = "300"

    private val documentId: Int = documentDetailsBundle.documentId
    private val imagePath: String? = documentDetailsBundle.imagePath

    private val documentName: String? = documentDetailsBundle.documentName
    private val documentSize: String? = documentDetailsBundle.documentSize
    private val documentUnit: String? = documentDetailsBundle.documentUnit
    private val documentPixels: String? = documentDetailsBundle.documentPixels
    private val documentResolution: String? = documentDetailsBundle.documentResolution
    private val documentImage: String? = documentDetailsBundle.documentImage
    private val documentType: String? = documentDetailsBundle.documentType
    private val documentCompleted: String? = documentDetailsBundle.documentCompleted


    //*********************Permission***************//
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun  dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    //********************************************//


    init {

        Logger.i("DocumentInfoScreenViewModel", "init: documentId=$documentId, imagePath=$imagePath")
        imagePath?.let {
            Logger.i("DocumentInfoScreenViewModel", "onInitialState: imagePath=$it")
            if (it.isNotEmpty()) {
                selectedImagesList.add(AssetInfo(id = 0,uriString = it, filepath = it, filename = "", directory = "", size = 0L, mediaType = 0, mimeType = "", duration = 0L, date = 0L))
            }
        }

        if (documentDetailsBundle.documentId == 0 && documentDetailsBundle.documentName != null) {
            handleCustomDocumentData(documentDetailsBundle)
        } else if (documentDetailsBundle.documentId > 0) {
            onInitialState()
        }
//        onInitialState()
        loadState(false)
        colorFactory.resetToDefault()
    }

    private fun onInitialState() = launch {

        getDocumentById(documentId).onSuccess {
            loadState(isLoading = false)
            _uiState.value = DocumentInfoScreenUiState(
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
        }
    }

    private fun handleCustomDocumentData(customData: DocumentDetailsBundle) {
        _uiState.update { currentState ->
            currentState.copy(
                showLoading = false,
                documentName = customData.documentName!!,
                documentSize = customData.documentSize!!,
                documentUnit = customData.documentUnit!!,
                documentPixels = customData.documentPixels!!,
                documentResolution = customData.documentResolution!!,
                documentImage = customData.documentImage,
                documentType = customData.documentType!!,
                documentCompleted = customData.documentCompleted
            )
        }
    }

    private fun loadState(isLoading: Boolean) {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = isLoading, errorMessage = null)
        }
    }

    fun onUpdateDpi(newDpi: String) {
        selectedDpi = newDpi
        Logger.i("DocumentInfoScreenViewModel", "onUpdateDpi: selectedDpi=$selectedDpi")
        _uiState.update { it.copy(documentResolution = newDpi) }
    }

    fun onOpenCameraClicked() {
//        _navigationState.tryEmit(PhotoIDScreenNavigationState.TakePhotoScreen(documentId))
    }

    fun onCreatePhotoClicked() {
        Logger.i("DocumentInfoScreenViewModel", "onCreatePhotoClicked: selectedImagesList=$selectedImagesList")
        _navigationState.tryEmit(
            DocumentInfoScreenNavigationState.ProcessingScreen(
                documentId = documentId,
                imagePath = selectedImagesList.firstOrNull()?.uriString?.toString(),
                filePath = selectedImagesList.firstOrNull()?.filepath?.toString(),
                documentName = _uiState.value.documentName,
                documentSize = _uiState.value.documentSize,
                documentUnit = _uiState.value.documentUnit,
                documentPixels = _uiState.value.documentPixels,
                selectedDpi = selectedDpi,
                selectedBackgroundColor = selectedColor.replayCache.firstOrNull(),
                sourceScreen = "DocumentInfoScreen"

            ))
    }

    fun onBackgroundOptionChanged(option: BackgroundOption) {
        _uiState.value = _uiState.value.copy(backgroundOption = option)

        // If "Keep Original" is selected, clear the color selection
        if (option == BackgroundOption.KEEP_ORIGINAL) {
            colorFactory.resetToDefault()
            _selectedColor.tryEmit(Color.Unspecified)
        }
    }

    fun selectPredefinedColor(colorType: ColorFactory.ColorType) {
        _uiState.value = _uiState.value.copy(backgroundOption = BackgroundOption.CHANGE_BACKGROUND)
        colorFactory.selectColor(colorType)
        _selectedColor.tryEmit(colorFactory.selectedColor)
    }

    fun setCustomColor(color: Color) {
        _uiState.value = _uiState.value.copy(backgroundOption = BackgroundOption.CHANGE_BACKGROUND)
        colorFactory.setCustomColor(color)
        _selectedColor.tryEmit(colorFactory.selectedColor)
    }

    fun applySelectedColor() {
        val currentBackgroundOption = _uiState.value.backgroundOption

        if (currentBackgroundOption == BackgroundOption.CHANGE_BACKGROUND) {
            _selectedColor.tryEmit(colorFactory.selectedColor)
        } else {
            // Keep original - don't apply any color
            _selectedColor.tryEmit(Color.Unspecified)
        }
    }

    fun resetColorSelection() {
        colorFactory.resetToDefault()
        _selectedColor.tryEmit(colorFactory.selectedColor)
    }

    private suspend fun getDocumentById(documentId: Int): Result<DocumentEntity> = getDocumentDetails(documentId)

}