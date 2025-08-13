package com.ots.aipassportphotomaker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Entity(tableName = "documents_remote_keys")
data class DocumentRemoteKeyDbData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prevPage: Int?,
    val nextPage: Int?
)
