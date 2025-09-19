package com.ots.aipassportphotomaker.data.repository

import androidx.paging.PagingSource
import com.ots.aipassportphotomaker.data.exception.DataNotAvailableException
import com.ots.aipassportphotomaker.data.db.documents.DocumentDao
import com.ots.aipassportphotomaker.data.db.documents.DocumentRemoteKeyDao
import com.ots.aipassportphotomaker.data.model.DocumentData
import com.ots.aipassportphotomaker.data.model.DocumentDbData
import com.ots.aipassportphotomaker.data.model.DocumentRemoteKeyDbData
import com.ots.aipassportphotomaker.data.model.mapper.CreatedImageData
import com.ots.aipassportphotomaker.data.model.toDbData
import com.ots.aipassportphotomaker.data.model.toDomain
import com.ots.aipassportphotomaker.data.util.source.DocumentDataSource
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.dbmodels.CreatedImageEntity
import com.ots.aipassportphotomaker.domain.util.Result

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class DocumentLocalDataSource(
    private val documentDao: DocumentDao,
    private val remoteKeyDao: DocumentRemoteKeyDao,
) : DocumentDataSource.Local {

    override fun documents(): PagingSource<Int, DocumentDbData> = documentDao.documents()
    override suspend fun getDocuments(): Result<List<DocumentEntity>> {
        val movies = documentDao.getDocuments()
        return if (movies.isNotEmpty()) {
            Result.Success(movies.map { it.toDomain() })
        } else {
            Result.Error(DataNotAvailableException())
        }
    }

    override suspend fun getDocument(movieId: Int): Result<DocumentEntity> {
        return documentDao.getMovie(movieId)?.let {
            Result.Success(it.toDomain())
        } ?: Result.Error(DataNotAvailableException())
    }

    override suspend fun saveDocuments(movies: List<DocumentData>) {
        documentDao.saveDocuments(movies.map { it.toDbData() })
    }

    override suspend fun getLastRemoteKey(): DocumentRemoteKeyDbData? {
        return remoteKeyDao.getLastRemoteKey()
    }

    override suspend fun saveRemoteKey(key: DocumentRemoteKeyDbData) {
        remoteKeyDao.saveRemoteKey(key)
    }

    override suspend fun clearDocuments() {
        documentDao.clearDocumentsExceptFavorites()
    }

    override suspend fun clearRemoteKeys() {
        remoteKeyDao.clearRemoteKeys()
    }

    // New: Save created image
    override suspend fun saveCreatedImage(createdImage: CreatedImageData) {
        documentDao.saveCreatedImage(createdImage.toDbData())
    }

    // New: Get created images by type
    override suspend fun getCreatedImagesByType(type: String): Result<List<CreatedImageEntity>> {
        val images = documentDao.getCreatedImagesByType(type)
        return if (images.isNotEmpty()) {
            Result.Success(images.map { it.toDomain() })
        } else {
            Result.Error(DataNotAvailableException())
        }
    }

    override suspend fun getAllCreatedImages() : Result<List<CreatedImageEntity>> {
        val images = documentDao.getAllCreatedImages()
        return if (images.isNotEmpty()) {
            Result.Success(images.map { it.toDomain() })
        } else {
            Result.Error(DataNotAvailableException())
        }
    }
}