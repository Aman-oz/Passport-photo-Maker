package com.ots.image_picker.view

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.ots.image_picker.AssetPickerRoute
import com.ots.image_picker.model.AssetInfo
import com.ots.image_picker.model.AssetPickerConfig
import com.ots.image_picker.provider.AssetPickerRepository
import com.ots.image_picker.viewmodel.AssetViewModel
import com.ots.image_picker.viewmodel.AssetViewModelFactory

@Composable
fun AssetPicker(
    assetPickerConfig: AssetPickerConfig,
    onPicked: (List<AssetInfo>) -> Unit,
    onClose: (List<AssetInfo>) -> Unit,
    onLoading: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val viewModel: AssetViewModel = viewModel(
        factory = AssetViewModelFactory(
            assetPickerRepository = AssetPickerRepository(context),
            navController = navController
        )
    )
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.initDirectories()
        isLoading.value = false
    }

    when {
        isLoading.value -> {
            onLoading?.invoke() ?: CircularProgressIndicator()
        }
        else -> {
            CompositionLocalProvider(LocalAssetConfig provides assetPickerConfig) {
                AssetPickerRoute(
                    navController = navController,
                    viewModel = viewModel,
                    onPicked = onPicked,
                    onClose = onClose,
                )
            }
        }
    }
}
