package com.ots.aipassportphotomaker.data.repository.favorite

import androidx.paging.PagingSource
import com.ots.aipassportphotomaker.data.model.DocumentDbData
import com.ots.aipassportphotomaker.domain.util.Result

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
interface FavoriteDocumentsDataSource {

    interface Local {
        fun favoriteDocuments(): PagingSource<Int, DocumentDbData>
        suspend fun getFavoriteDocumentIds(): Result<List<Int>>
        suspend fun addDocumentToFavorite(documentId: Int)
        suspend fun removeDocumentFromFavorite(documentId: Int)
        suspend fun checkFavoriteStatus(documentId: Int): Result<Boolean>
    }
}