package com.ots.aipassportphotomaker.presentation.ui.documentinfo

import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.usecase.photoid.GetDocumentDetails
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.ots.aipassportphotomaker.domain.util.Result
import com.ots.aipassportphotomaker.domain.util.onSuccess
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDScreenNavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class DocumentInfoScreenViewModel @Inject constructor(
    private val getDocumentDetails: GetDocumentDetails,
    documentDetailsBundle: DocumentDetailsBundle,
) : BaseViewModel() {

    private val _uiState: MutableStateFlow<DocumentInfoScreenUiState> = MutableStateFlow(DocumentInfoScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<DocumentInfoScreenNavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val documentId: Int = documentDetailsBundle.documentId

    init {
        onInitialState()
        loadState(false)
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

    private fun loadState(isLoading: Boolean) {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = isLoading, errorMessage = null)
        }
    }

    fun onSelectPhotoClicked() {
//        _navigationState.tryEmit(PhotoIDScreenNavigationState.SelectPhotoScreen(documentId))
    }
    fun onOpenCameraClicked() {
//        _navigationState.tryEmit(PhotoIDScreenNavigationState.TakePhotoScreen(documentId))
    }
    fun onCreatePhotoClicked() {
//        _navigationState.tryEmit(PhotoIDScreenNavigationState.ProcessingScreen(documentId))
    }


    private suspend fun getDocumentById(documentId: Int): Result<DocumentEntity> = getDocumentDetails(documentId)

}