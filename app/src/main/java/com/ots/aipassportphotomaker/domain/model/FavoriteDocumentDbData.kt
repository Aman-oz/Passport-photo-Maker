package com.ots.aipassportphotomaker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Entity(tableName = "favorite_documents")
data class FavoriteDocumentDbData(
    @PrimaryKey val documentId: Int
)
