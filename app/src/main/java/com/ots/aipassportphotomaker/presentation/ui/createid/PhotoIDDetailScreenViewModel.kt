package com.ots.aipassportphotomaker.presentation.ui.createid

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import com.ots.aipassportphotomaker.presentation.ui.usecase.photoid.GetDocumentsByType
import com.ots.aipassportphotomaker.presentation.ui.usecase.photoid.GetDocumentsWithSeparators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Created by amanullah on 21/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@HiltViewModel
class PhotoIDDetailScreenViewModel @Inject constructor(
    val networkMonitor: NetworkMonitor,
    getDocumentsByType: GetDocumentsByType,
    photoIDDetailBundle: PhotoIDDetailBundle,
) : BaseViewModel() {

    private val type = photoIDDetailBundle.type

    val documents: Flow<PagingData<DocumentListItem>> = getDocumentsByType.documents(
        type = type,//should get by the selected type
        pageSize = 90
    ).cachedIn(viewModelScope)

    private val _uiState: MutableStateFlow<PhotoIDDetailScreenUiState> = MutableStateFlow(PhotoIDDetailScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<PhotoIDDetailScreenNavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _refreshListState: MutableSharedFlow<Unit> = singleSharedFlow()
    val refreshListState = _refreshListState.asSharedFlow()

    init {
        observeNetworkStatus()
        observeLoadState()
        onInitialState()
    }

    private fun loadData() {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = false, errorMessage = null)
        }
    }

    private fun observeNetworkStatus() {
        networkMonitor.networkState
            .onEach { if (it.shouldRefresh) onRefresh() }
            .launchIn(viewModelScope)
    }

    private fun observeLoadState() {
        launch {
            documents.collect {
                // When we receive data, update loading state
                _uiState.update { currentState ->
                    currentState.copy(showLoading = false)
                }
            }
        }
    }

    private fun onInitialState() = launch {

        _uiState.value = _uiState.value.copy(
            type = type, //should get by the selected type
            showLoading = true // Start with loading until data is available
        )

        documents.firstOrNull()?.let { firstPage ->
            _uiState.update { currentState ->
                currentState.copy(showLoading = false)
            }
        }

        /*getDocumentsByType(type).onSuccess {
            _uiState.value = PhotoIDDetailScreenUiState(
                type = it.type
            )
        }*/
    }

    fun onDocumentClicked(documentId: Int) =
        _navigationState.tryEmit(PhotoIDDetailScreenNavigationState.SelectPhotoScreen(documentId))

    fun onBackClicked() {
        //_navigationState.tryEmit(PhotoIDDetailScreenNavigationState.NavigateBack)
    }

    fun onGetProClicked() {
        // Navigate to premium screen or show premium dialog
    }

    fun onLoadStateUpdate(loadState: CombinedLoadStates) {
        val showLoading = loadState.refresh is LoadState.Loading

        val error = when (val refresh = loadState.refresh) {
            is LoadState.Error -> refresh.error.message
            else -> null
        }

        _uiState.update { it.copy(showLoading = showLoading, errorMessage = error) }
    }
    fun onRefresh() = launch {
        _refreshListState.emit(Unit)
    }


    private suspend fun getDocumentsByType(type: String): Result<DocumentEntity> = getDocumentsByType(type)

}