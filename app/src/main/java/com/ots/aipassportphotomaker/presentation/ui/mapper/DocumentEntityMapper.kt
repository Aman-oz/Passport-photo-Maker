package com.ots.aipassportphotomaker.presentation.ui.mapper

import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.DocumentListItem

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

fun DocumentEntity.toPresentation() = DocumentListItem.Document(
    id = id,
    name = name,
    size = size,
    unit = unit,
    pixels = pixels,
    resolution = resolution,
    image = image,
    type = type,
    completed = completed
)

fun DocumentEntity.toDocumentListItem(): DocumentListItem = this.toPresentation()