package com.ots.aipassportphotomaker.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.data.model.toDomain
import com.ots.aipassportphotomaker.data.repository.favorite.FavoriteDocumentsDataSource
import com.ots.aipassportphotomaker.data.util.DocumentRemoteMediator
import com.ots.aipassportphotomaker.data.util.source.DocumentDataSource
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.repository.DocumentRepository
import com.ots.aipassportphotomaker.domain.util.Result
import com.ots.aipassportphotomaker.domain.util.Result.Success
import com.ots.aipassportphotomaker.domain.util.Result.Error
import com.ots.aipassportphotomaker.domain.util.map
import com.ots.aipassportphotomaker.domain.util.onError
import com.ots.aipassportphotomaker.domain.util.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class DocumentRepositoryImpl(
    private val remote: DocumentDataSource.Remote,
    private val local: DocumentDataSource.Local,
    private val json: DocumentAssetDataSource,
    private val remoteMediator: DocumentRemoteMediator,
    private val localFavorite: FavoriteDocumentsDataSource.Local
) : DocumentRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun documents(pageSize: Int): Flow<PagingData<DocumentEntity>> {
        return flow {
            val documents = json.getDocuments()
            Logger.d("DocumentRepositoryImpl", "documents: ${documents.size} documents loaded from JSON")
            val pagingData = PagingData.from(documents)
            emit(pagingData)
        }
    } /*= Pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false
        ),
        remoteMediator = remoteMediator,
        pagingSourceFactory = {
            local.documents()
        }
    ).flow.map { pagingData ->
        pagingData.map { it.toDomain() }
    }*/

    override fun favoriteDocuments(pageSize: Int): Flow<PagingData<DocumentEntity>> = Pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { localFavorite.favoriteDocuments() }
    ).flow.map { pagingData ->
        pagingData.map { it.toDomain() }
    }

    override fun search(query: String, pageSize: Int): Flow<PagingData<DocumentEntity>> {
        return flow {
            val documents = json.getDocuments()
                .filter { it.name.contains(query, ignoreCase = true) }
            emit(PagingData.from(documents))
        }
    } /*= Pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { SearchDocumentPagingSource(query, remote) }
    ).flow.map { pagingData ->
        pagingData.map { it.toDomain() }
    }*/

    override suspend fun getDocument(movieId: Int): Result<DocumentEntity> {
        return when (val localResult = local.getDocument(movieId)) {
            is Success -> localResult
            is Error -> remote.getDocument(movieId).map { it.toDomain() }
        }
    }

    override suspend fun checkFavoriteStatus(movieId: Int): Result<Boolean> = localFavorite.checkFavoriteStatus(movieId)


    override suspend fun addDocumentToFavorite(movieId: Int) {
        local.getDocument(movieId)
            .onSuccess {
                localFavorite.addDocumentToFavorite(movieId)
            }
            .onError {
                remote.getDocument(movieId).onSuccess { movie ->
                    local.saveDocuments(listOf(movie))
                    localFavorite.addDocumentToFavorite(movieId)
                }
            }
    }

    override suspend fun removeDocumentFromFavorite(movieId: Int) = localFavorite.removeDocumentFromFavorite(movieId)

    override suspend fun sync(): Boolean {
        return when (val result = local.getDocuments()) {
            is Error -> false
            is Success -> {
                val movieIds = result.data.map { it.id }
                return updateLocalWithRemoteMovies(movieIds)
            }
        }
    }

    private suspend fun updateLocalWithRemoteMovies(movieIds: List<Int>): Boolean {
        return when (val remoteResult = remote.getDocuments(movieIds)) {
            is Error -> false
            is Success -> {
                local.saveDocuments(remoteResult.data)
                true
            }
        }
    }
}