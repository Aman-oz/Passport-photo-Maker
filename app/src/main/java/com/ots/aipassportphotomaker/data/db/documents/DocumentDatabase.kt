package com.ots.aipassportphotomaker.data.db.documents

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ots.aipassportphotomaker.data.db.favoritedocuments.FavoriteDocumentDao
import com.ots.aipassportphotomaker.data.model.DocumentDbData
import com.ots.aipassportphotomaker.data.model.DocumentRemoteKeyDbData
import com.ots.aipassportphotomaker.domain.model.FavoriteDocumentDbData

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Database(
    entities = [DocumentDbData::class, FavoriteDocumentDbData::class, DocumentRemoteKeyDbData::class],
    version = 1,
    exportSchema = false
)
abstract class DocumentDatabase: RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun documentRemoteKeysDao(): DocumentRemoteKeyDao
    abstract fun favoriteDocumentDao(): FavoriteDocumentDao
}