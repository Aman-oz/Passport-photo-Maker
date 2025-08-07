package com.ots.aipassportphotomaker.presentation.ui.history

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import com.ots.aipassportphotomaker.presentation.ui.home.HomeScreenNavigationState
import com.ots.aipassportphotomaker.presentation.ui.home.HomeScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HistoryScreenViewModel @Inject constructor(
    val networkMonitor: NetworkMonitor,
) : BaseViewModel() {

    private val _uiState: MutableStateFlow<HistoryScreenUiState> = MutableStateFlow(
        HistoryScreenUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<HistoryScreenNavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _refreshListState: MutableSharedFlow<Unit> = singleSharedFlow()
    val refreshListState = _refreshListState.asSharedFlow()

    init {
        observeNetworkStatus()
        loadData()
    }
    private fun loadData() {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = false, errorMessage = null)
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

    fun onItemClick(name: String) =
        _navigationState.tryEmit(HistoryScreenNavigationState.PhotoIDDetails(name))

    private fun observeNetworkStatus() {
        networkMonitor.networkState
            .onEach { if (it.shouldRefresh) onRefresh() }
            .launchIn(viewModelScope)
    }
    fun onRefresh() = launch {
        _refreshListState.emit(Unit)
    }

}