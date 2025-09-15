package com.ots.aipassportphotomaker.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

// Created by amanullah on 15/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Parcelize
@Serializable
data class CustomDocumentData(
    val documentName: String,
    val documentSize: String,
    val documentUnit: String,
    val documentPixels: String,
    val documentResolution: String,
    val documentImage: String?,
    val documentType: String,
    val documentCompleted: String?
) : Parcelable
