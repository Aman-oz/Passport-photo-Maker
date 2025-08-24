package com.ots.aipassportphotomaker.presentation.ui.documentinfo

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.ext.ImageSize
import com.ots.aipassportphotomaker.common.ext.collectAsEffect
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.bottom_nav.NavigationBarSharedViewModel
import com.ots.aipassportphotomaker.presentation.ui.components.CommonTopBar
import com.ots.aipassportphotomaker.presentation.ui.components.LoaderFullScreen
import com.ots.aipassportphotomaker.presentation.ui.main.MainRouter
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.custom100
import com.ots.aipassportphotomaker.presentation.ui.theme.custom300
import com.ots.aipassportphotomaker.presentation.ui.theme.custom400
import com.ots.aipassportphotomaker.presentation.ui.theme.customSuccess
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom300
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400

@Composable
fun DocumentInfoPage(
    mainRouter: MainRouter,
    viewModel: DocumentInfoScreenViewModel = hiltViewModel(),
    sharedViewModel: NavigationBarSharedViewModel,
) {
    val TAG = "DocumentInfoPage"

    val uiState by viewModel.uiState.collectAsState()

    Logger.d(TAG, "DocumentInfoPage: UI State: $uiState")
    Logger.i(TAG, "DocumentInfoPage: Document name: ${uiState.documentName}")

    viewModel.navigationState.collectAsEffect { navigationState ->

        Log.d(TAG, "DocumentInfoPage: Navigation State: $navigationState")
        when (navigationState) {
            is DocumentInfoScreenNavigationState.TakePhotoScreen -> {
//                mainRouter.navigateToPhotoIDDetailScreen(navigationState.type)
            }

            is DocumentInfoScreenNavigationState.ProcessingScreen -> {
//                mainRouter.navigateToPhotoIDDetailScreen(navigationState.type)
            }

            is DocumentInfoScreenNavigationState.SelectPhotoScreen -> {
                Log.d(TAG, "DocumentInfoPage: Navigate to Select Photo Screen")
                mainRouter.navigateToSelectPhotoScreen(
                    documentId = navigationState.documentId
                )
            }
        }
    }


    DocumentInfoScreen(
        uiState = uiState,
        onOpenGalleryClick = {
            viewModel.onSelectPhotoClicked()
        },
        onTakePhotoClick = {
            viewModel.onOpenCameraClicked()
        },
        onCreatePhotoClick = { type ->
            viewModel.onCreatePhotoClicked()
        },
        onBackClick = {
            mainRouter.goBack()
        },
    )
}

@Composable
private fun DocumentInfoScreen(
    uiState: DocumentInfoScreenUiState,
    onOpenGalleryClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onCreatePhotoClick: (type: String) -> Unit,
    onBackClick: () -> Unit,
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {

        val context = LocalContext.current
        val isLoading = uiState.showLoading
        val errorMessage = uiState.errorMessage

        if (errorMessage != null) Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

        if (isLoading) {
            LoaderFullScreen()
        } else {
            val imageSize = ImageSize.getImageFixedSize()
            val isImageSelected by remember { mutableStateOf(false) }

            var scale0 by remember { mutableFloatStateOf(1f) }
            val imageAnimatedScale by animateFloatAsState(
                targetValue = scale0,
                label = "FloatAnimation"
            )
            var scale1 by remember { mutableFloatStateOf(1f) }
            val textAnimatedScale by animateFloatAsState(
                targetValue = scale1,
                label = "FloatAnimation"
            )
            var scale2 by remember { mutableFloatStateOf(1f) }
            val buttonAnimatedScale by animateFloatAsState(
                targetValue = scale2,
                label = "FloatAnimation"
            )

            Box(
                modifier = Modifier
                    .background(colors.background)
                    .fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .background(colors.background)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    CommonTopBar(
                        title = "Document Info",
                        showGetProButton = false,
                        onBackClick = onBackClick,
                        onGetProClick = {})

                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .background(colors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sample_image_square), // Replace with your drawable resource ID
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            scale0 = 0.90f
                                            tryAwaitRelease()
                                            scale0 = 1f
                                        },
                                        onTap = {
                                            // onDocumentClick(document.id)
                                        }
                                    )
                                }.scale(imageAnimatedScale)
                            
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Checklist Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.custom400)
                            .border(1.dp, colors.custom100, RoundedCornerShape(16.dp)),
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            // Title with Icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.passport_united_state), // Replace with passport icon
                                    contentDescription = "Passport Icon",
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = uiState.documentName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = colors.onCustom400,
                                        modifier = Modifier
                                    )
                                    Text(
                                        text = uiState.documentType,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.onSurfaceVariant,
                                        modifier = Modifier
                                    )
                                }


                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(32.dp))
                                        .background(colors.custom300)
                                        .border(
                                            BorderStroke(1.dp, colors.custom100),
                                            RoundedCornerShape(32.dp)
                                        )
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = {
                                                    Log.d(
                                                        "DocumentInfoScreen",
                                                        "Settings icon tapped"
                                                    )
                                                }
                                            )
                                        },
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_circle),
                                        tint = colors.onCustom300,
                                        contentDescription = "Reselect Document",
                                        modifier = Modifier
                                            .padding(6.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Checklist Items
                            ChecklistItem(uiState, text = "Image Selection", isChecked = false, onChangeBackground = {})
                            ChecklistItem(uiState, text = "Document Size", isChecked = true, onChangeBackground = {})
                            ChecklistItem(uiState, text = "Unit", isChecked = true, onChangeBackground = {})
                            ChecklistItem(uiState, text = "Pixel", isChecked = false, onChangeBackground = {})
                            ChecklistItem(uiState, text = "Resolution", isChecked = true, onChangeBackground = {})
                            ChecklistItem(uiState, text = "Background", isChecked = true, onChangeBackground = {})

                        }
                    }
                    // Add spacer to push buttons to the bottom if needed
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Bottom Button Layout

                if (isImageSelected) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            scale1 = 0.90f
                                            tryAwaitRelease()
                                            scale1 = 1f
                                        },
                                        onTap = {
                                            onCreatePhotoClick(uiState.documentType)
                                        }
                                    )
                                }
                                .scale(textAnimatedScale),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_circle),
                                contentDescription = null,
                                tint = colors.onBackground,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )
                            Text(
                                text = "Retake image",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onBackground,
                                modifier = Modifier
                                    .padding(start = 10.dp) // Adjusted padding for better spacing
                            )
                        }

                        Button(
                            onClick = {
                                onCreatePhotoClick(uiState.documentType)
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .height(48.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        scale2 = 0.90f
                                        tryAwaitRelease()
                                        scale2 = 1f
                                    },
                                    onTap = {
                                        onCreatePhotoClick(uiState.documentType)
                                    }
                                )
                            }
                                .scale(buttonAnimatedScale)
                        ) {
                            Text(
                                text = "Create Photo",
                                color = colors.onPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }

                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = onOpenGalleryClick,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary), // Blue color from image
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp, bottom = 8.dp)
                                .height(48.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.gallery_icon), // Replace with gallery icon
                                    contentDescription = "Open Gallery",
                                    tint = colors.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Open Gallery",
                                    color = colors.onPrimary,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Button(
                            onClick = onTakePhotoClick,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(2.dp, colors.primary),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp, bottom = 8.dp)
                                .height(48.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.camera_icon_outline), // Replace with camera icon
                                    contentDescription = "Take Photo",
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Take Photo",
                                    color = colors.primary,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun ChecklistItem(
    uiState: DocumentInfoScreenUiState,
    text: String,
    isChecked: Boolean,
    onChangeBackground: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
            contentDescription = if (isChecked) "Checked" else "Unchecked",
            tint = if (isChecked) colors.customSuccess else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        when (text) {
            "Image Selection" -> {
                Icon(
                    imageVector = if (isChecked) Icons.Default.Check else Icons.Default.Close, // Replace with your drawable resource ID
                    contentDescription = null,
                    tint = colors.onCustom300,
                    modifier = Modifier
                        .size(16.dp)
                )

            }

            "Background" -> {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .aspectRatio(1.4f)
                        .border(1.dp, colors.outline, shape = RoundedCornerShape(6.dp))
                        .background(
                            Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { showDialog = true }
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                            .background(
                                colors.primary,
                                shape = RoundedCornerShape(6.dp)
                            )
                    )

                }

            }

            else -> {
                Box(
                    modifier = Modifier
                        .background(colors.custom300, shape = RoundedCornerShape(6.dp))
                        .border(1.dp, colors.custom100, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (text) {
                            "Document Size" -> uiState.documentSize ?: "2.0 x 2.0"
                            "Unit" -> uiState.documentUnit ?: "inch"
                            "Pixel" -> uiState.documentPixels ?: "600 x 600 px"
                            "Resolution" -> uiState.documentResolution ?: "300 DPI"
                            else -> ""
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onCustom300
                    )
                }
            }
        }

    }

    // Background Color Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Change color",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    Text(
                        text = "Keep original background or add custom color.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = true, // Default to "Do not change"
                            onClick = { /* Handle "Do not change" selection */ }
                        )
                        Text(
                            text = "Do not change",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = false, // Default to unselected
                            onClick = { /* Handle "Change background color" selection */ }
                        )
                        Text(
                            text = "Change background color",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.White, RoundedCornerShape(4.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.LightGray, RoundedCornerShape(4.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.Blue, RoundedCornerShape(4.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.Red, RoundedCornerShape(4.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview("Light", device = "id:pixel_5", showSystemUi = true)
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DocumentInfoScreenPreview() {
    PreviewContainer {
        DocumentInfoScreen(
            uiState = DocumentInfoScreenUiState(
                showLoading = false,
                errorMessage = null,
            ),
            onOpenGalleryClick = {},
            onTakePhotoClick = {},
            onCreatePhotoClick = {},
            onBackClick = {}
        )
    }
}