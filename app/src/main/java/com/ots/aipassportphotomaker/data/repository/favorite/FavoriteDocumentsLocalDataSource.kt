package com.ots.aipassportphotomaker.data.repository.favorite

import androidx.paging.PagingSource
import com.ots.aipassportphotomaker.data.db.favoritedocuments.FavoriteDocumentDao
import com.ots.aipassportphotomaker.data.exception.DataNotAvailableException
import com.ots.aipassportphotomaker.data.model.DocumentDbData
import com.ots.aipassportphotomaker.domain.model.FavoriteDocumentDbData
import com.ots.aipassportphotomaker.domain.util.Result

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class FavoriteDocumentsLocalDataSource(
    private val favoriteDocumentDao: FavoriteDocumentDao,
) : FavoriteDocumentsDataSource.Local {

    override fun favoriteDocuments(): PagingSource<Int, DocumentDbData> = favoriteDocumentDao.favoriteDocuments()

    override suspend fun addDocumentToFavorite(movieId: Int) {
        favoriteDocumentDao.add(FavoriteDocumentDbData(movieId))
    }

    override suspend fun removeDocumentFromFavorite(movieId: Int) {
        favoriteDocumentDao.remove(movieId)
    }

    override suspend fun checkFavoriteStatus(movieId: Int): Result<Boolean> {
        return Result.Success(favoriteDocumentDao.get(movieId) != null)
    }

    override suspend fun getFavoriteDocumentIds(): Result<List<Int>> {
        val movieIds = favoriteDocumentDao.getAll().map { it.documentId }
        return if (movieIds.isNotEmpty()) {
            Result.Success(movieIds)
        } else {
            Result.Error(DataNotAvailableException())
        }
    }
}