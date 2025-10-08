package com.ots.aipassportphotomaker.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.ads.AdSize
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.adsmanager.admob.AdMobBanner
import com.ots.aipassportphotomaker.adsmanager.admob.adids.AdIdsFactory
import com.ots.aipassportphotomaker.common.preview.PreviewContainer
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.presentation.ui.theme.colors
import com.ots.aipassportphotomaker.presentation.ui.theme.onCustom400

// Created by amanullah on 16/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Composable
fun ExitDialog(
    modifier: Modifier = Modifier,
    isPremium: Boolean = false,
    onCancelClick: () -> Unit,
    onExitClick: () -> Unit,
) {

    var adLoadState by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.exit_app),
            color = colors.onCustom400,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.Center)
        )

        if (!isPremium) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center
                ) {
                    if (!adLoadState) {
                        Text(
                            text = stringResource(R.string.advertisement),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(align = Alignment.Center)
                        )
                    }

                    AdMobBanner(
                        adUnit = AdIdsFactory.getExitBannerAdId(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .align(Alignment.Center),
                        adSize = AdSize.MEDIUM_RECTANGLE, // or adaptive size if needed
                        onAdLoaded = { isLoaded ->
                            adLoadState = true
                            Logger.d("ExitDialog", "OnboardingScreen: Ad Loaded: $isLoaded")
                        }
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Button(
                onClick = {
                    if (!adLoadState && !isPremium) return@Button

                    onCancelClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary.copy(alpha = 0.5f))
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = colors.onPrimary,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (!adLoadState && !isPremium) return@Button

                    onExitClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (!adLoadState && !isPremium) colors.primaryContainer else colors.primary)
            ) {
                Text(
                    text = stringResource(R.string.exit),
                    color = if (!adLoadState && !isPremium) colors.onPrimaryContainer else colors.onPrimary,
                    fontSize = 16.sp
                )
            }

        }


    }

}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ExitDialogPreview() {
    PreviewContainer {
        ExitDialog(
            onCancelClick = {},
            onExitClick = {}
        )
    }
}