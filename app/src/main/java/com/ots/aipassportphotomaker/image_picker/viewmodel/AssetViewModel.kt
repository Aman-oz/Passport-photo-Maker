package com.ots.aipassportphotomaker.image_picker.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.image_picker.AssetRoute
import com.ots.aipassportphotomaker.image_picker.model.AssetDirectory
import com.ots.aipassportphotomaker.image_picker.model.AssetInfo
import com.ots.aipassportphotomaker.image_picker.model.RequestType
import com.ots.aipassportphotomaker.image_picker.provider.AssetLoader
import com.ots.aipassportphotomaker.image_picker.provider.AssetPickerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.text.clear
import kotlin.toString

//const val init_directory = "Photos/Videos"
const val init_directory = "Camera"

internal class AssetViewModel(
    private val assetPickerRepository: AssetPickerRepository,
    private val navController: NavController,
) : ViewModel() {

//    private val assets = mutableStateListOf<AssetInfo>()
    private val _assets = mutableStateListOf<AssetInfo>()
    val assets: List<AssetInfo> get() = _assets

    private val _directoryGroup = mutableStateListOf<AssetDirectory>()
    val directoryGroup: List<AssetDirectory> get() = _directoryGroup

    val selectedList = mutableStateListOf<AssetInfo>()
    var directory by mutableStateOf(init_directory)

    private val _isAssetsLoading = MutableStateFlow(false)
    val isAssetsLoading: StateFlow<Boolean> = _isAssetsLoading.asStateFlow()

    private var currentOffset = 0
    private val batchSize = 50
    var isLoading = false
    private val _isInitialLoadComplete = mutableStateOf(false)
    private val directoryCounts = mutableMapOf<String, Int>()

    init {
        viewModelScope.launch {
            initDirectories()
            _isInitialLoadComplete.value = true
        }
    }

    suspend fun initDirectories() {
        currentOffset = 0
        _assets.clear()
        _directoryGroup.clear()
        loadInitialData()
        updateDirectoryGroupsWithCounts()
    }

    private suspend fun loadInitialData() {
        if (isLoading) return
        isLoading = true
        try {
            val newAssets = assetPickerRepository.getAssets(RequestType.IMAGE, batchSize, currentOffset)
            if (newAssets.isNotEmpty()) {
                _assets.addAll(newAssets)
                currentOffset += newAssets.size
                Logger.d("AssetViewModel", "Loaded initial ${newAssets.size} assets, total: ${_assets.size}")
            } else {
                Logger.d("AssetViewModel", "No assets loaded at offset $currentOffset")
            }
            // Load directory counts once during init
            directoryCounts.putAll(assetPickerRepository.getDirectoryCounts())
        } catch (e: Exception) {
            Logger.e("AssetViewModel", "Error loading initial data: ${e.message}", e)
        } finally {
            _isAssetsLoading.value = false
            isLoading = false
        }
    }

    private suspend fun loadBatch() {
        if (isLoading) return
        isLoading = true
        _isAssetsLoading.value = true
        try {
            val newAssets = assetPickerRepository.getAssets(RequestType.IMAGE, batchSize, currentOffset)
            if (newAssets.isNotEmpty()) {
                _assets.addAll(newAssets)
                currentOffset += newAssets.size
                Logger.d("AssetViewModel", "Loaded ${newAssets.size} assets, total: ${_assets.size}")
            } else {
                Logger.d("AssetViewModel", "No more assets to load at offset $currentOffset")
            }
        } catch (e: Exception) {
            Logger.e("AssetViewModel", "Error loading batch: ${e.message}", e)
        } finally {
            isLoading = false
            _isAssetsLoading.value = false
        }
    }

    fun loadMoreAssets() {
        if (!isLoading && _isInitialLoadComplete.value) {
            viewModelScope.launch {
                loadBatch()
                if (_assets.isNotEmpty()) {
                    updateDirectoryGroupsWithCounts()
                }
            }
        }
    }

    fun onImageCaptured(uri: Uri) {
        viewModelScope.launch {
//            delay(500)
            initDirectories()
            val newImage = assets.firstOrNull {
                it.uriString == uri.toString() ||
                        it.uriString.endsWith(uri.lastPathSegment ?: "")
            }
            Logger.d("AssetViewModel", "Captured URI: $uri, Found: $newImage")
            newImage?.let {
                selectedList.clear() // Clear previous selections for single auto-select; remove if multi-select desired
                selectedList.add(newImage)
                toggleSelect(true, it) // Or directly: selectedList.add(it)
                Logger.i("AssetViewModel", "Auto-selected captured image: ${it.filename}")
            } ?: run {
                Logger.w("AssetViewModel", "Captured image not found in assets after refresh")
            }
            /*if (newImage != null) {
                selectedList.clear()
                selectedList.add(newImage)
            }*/
        }
    }

    private fun updateDirectoryGroupsWithCounts() {
        val directoryList = _assets.groupBy { it.directory }.map { entry ->
            AssetDirectory(
                directory = entry.key,
                assets = entry.value,
                counts = directoryCounts[entry.key] ?: entry.value.size // Use total count if available, else current size
            )
        }
        _directoryGroup.clear()
        _directoryGroup.add(AssetDirectory(directory = init_directory, assets = _assets, counts = directoryCounts[init_directory] ?: _assets.size))
        _directoryGroup.addAll(directoryList)
        Logger.d("AssetViewModel", "Updated directory groups, total groups: ${_directoryGroup.size}")
    }

    suspend fun initDirectories1() {
        currentOffset = 0
        _assets.clear()
        _directoryGroup.clear()
        loadNextBatch1()
        val directoryList = assets.groupBy {
            it.directory
        }.map {
            AssetDirectory(directory = it.key, assets = it.value)
        }
        _directoryGroup.add(AssetDirectory(directory = init_directory, assets = assets))
        _directoryGroup.addAll(directoryList)
    }

    private suspend fun loadNextBatch1() {
        val newAssets = assetPickerRepository.getAssets(RequestType.IMAGE, batchSize, currentOffset)
        _assets.addAll(newAssets)
        currentOffset += newAssets.size
        if (newAssets.size == batchSize) {
            loadNextBatch1()
        }
    }

    fun clear() {
        selectedList.clear()
    }

    fun toggleSelect(selected: Boolean, assetInfo: AssetInfo) {
        if (selected) {
            selectedList += assetInfo
        } else {
            selectedList -= assetInfo
        }
    }

    fun getGroupedAssets(requestType: RequestType): Map<String, List<AssetInfo>> {
        return _assets.sortedByDescending { it.date }
            .groupBy { it.dateString }
    }

    fun isAllSelected(assets: List<AssetInfo>): Boolean {
        val selectedIds = selectedList.map { it.id }
        val ids = assets.map { it.id }
        return selectedIds.containsAll(ids)
    }

    fun hasSelected(assets: List<AssetInfo>): Boolean {
        val selectedIds = selectedList.map { it.id }
        val ids = assets.map { it.id }

        return selectedIds.any { ids.contains(it) }
    }

    fun navigateToPreview(index: Int, dateString: String, requestType: RequestType) {
        navController.navigate(AssetRoute.preview(index, dateString, requestType))
    }

    fun deleteImage(cameraUri: Uri?) {
        viewModelScope.launch {
            assetPickerRepository.deleteByUri(cameraUri)
            _assets.removeAll { it.uriString == cameraUri.toString() } // Update local list
        }
    }

    fun getUri(): Uri? {
        return assetPickerRepository.insertImage()
    }

    fun unSelectAll(resources: List<AssetInfo>) {
        selectedList -= resources.toSet()
    }

    fun selectAll(resources: List<AssetInfo>, maxAssets: Int): Boolean {
        val selectedIds = selectedList.map { it.id }
        val newSelectedList = resources.filterNot { selectedIds.contains(it.id) }

        selectedList += newSelectedList.subList(0, minOf(maxAssets - selectedIds.size, newSelectedList.size))
        return maxAssets - selectedIds.size < newSelectedList.size
    }

    /*private suspend fun initAssets(requestType: RequestType) {
        assets.clear()
        assets.addAll(assetPickerRepository.getAssets(requestType))
    }*/
}