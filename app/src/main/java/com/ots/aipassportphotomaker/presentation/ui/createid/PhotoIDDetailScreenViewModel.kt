package com.ots.aipassportphotomaker.presentation.ui.createid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.domain.usecase.photoid.SearchDocuments
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import com.ots.aipassportphotomaker.presentation.ui.createid.PhotoIDScreen2ViewModel.Companion.KEY_SEARCH_QUERY
import com.ots.aipassportphotomaker.presentation.ui.mapper.toDocumentListItem
import com.ots.aipassportphotomaker.presentation.ui.usecase.photoid.GetDocumentsByType
import com.ots.aipassportphotomaker.presentation.ui.usecase.photoid.GetDocumentsWithSeparators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Created by amanullah on 21/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@HiltViewModel
class PhotoIDDetailScreenViewModel @Inject constructor(
    val networkMonitor: NetworkMonitor,
    private val searchDocuments: SearchDocuments,
    private val savedStateHandle: SavedStateHandle,
    getDocumentsByType: GetDocumentsByType,
    photoIDDetailBundle: PhotoIDDetailBundle,
) : BaseViewModel() {

    private val type = photoIDDetailBundle.type
    private val imagePath = photoIDDetailBundle.imagePath
    var cachedImagePath: String? = imagePath

    val documents: Flow<PagingData<DocumentListItem>> = getDocumentsByType.documents(
        type = type,//should get by the selected type
        pageSize = 40
    ).cachedIn(viewModelScope)

    private val _uiState: MutableStateFlow<PhotoIDDetailScreenUiState> = MutableStateFlow(PhotoIDDetailScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<PhotoIDDetailScreenNavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _refreshListState: MutableSharedFlow<Unit> = singleSharedFlow()
    val refreshListState = _refreshListState.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    var searchedDocuments: Flow<PagingData<DocumentListItem>> = savedStateHandle.getStateFlow(
        PhotoIDScreenViewModel.KEY_SEARCH_QUERY, "")
        .debounce(if (_uiState.value.showDefaultState) 0 else 500)
        .onEach { query ->
            _uiState.value = if (query.isNotEmpty()) {
                PhotoIDDetailScreenUiState(showDefaultState = false, showLoading = false)
            } else {
                PhotoIDDetailScreenUiState(showDefaultState = true, showLoading = false)
            }
        }
        .filter { it.isNotEmpty() }
        .flatMapLatest { query ->
            searchDocuments(query, 30).map { pagingData ->
                pagingData.map { documentEntity -> documentEntity.toDocumentListItem() }
            }
        }.cachedIn(viewModelScope)

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
        cachedImagePath?.let {
            Logger.i("PhotoIDDetailScreenViewModel","PhotoIDDetailScreenViewModel: Image Path: $it")
            if (it.isNotEmpty()) {
                _uiState.update { currentState ->
                    currentState.copy(imagePath = it)

                }
            }
        }

        _uiState.value = _uiState.value.copy(
            type = type, //should get by the selected type
            showLoading = true // Start with loading until data is available
        )

        documents.firstOrNull()?.let { firstPage ->
            _uiState.update { currentState ->
                currentState.copy(showLoading = false)
            }
        }
    }

    fun onDocumentClicked(documentId: Int) {

        _navigationState.tryEmit(PhotoIDDetailScreenNavigationState.DocumentInfoScreen(
            documentId = documentId,
            imagePath = cachedImagePath,
        ))
    }

    fun onBackClicked() {
        //_navigationState.tryEmit(PhotoIDDetailScreenNavigationState.NavigateBack)
    }

    fun onGetProClicked() {
        // Navigate to premium screen or show premium dialog
    }

    fun onSearch(query: String) {
        savedStateHandle[KEY_SEARCH_QUERY] = query
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


    companion object {
        const val KEY_SEARCH_QUERY = "search_query"
    }
}