package com.ots.aipassportphotomaker.data.repository

import com.ots.aipassportphotomaker.data.model.DocumentData
import com.ots.aipassportphotomaker.data.remote.api.DocumentApi
import com.ots.aipassportphotomaker.data.util.safeApiCall
import com.ots.aipassportphotomaker.data.util.source.DocumentDataSource
import com.ots.aipassportphotomaker.domain.util.Result

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class DocumentRemoteDataSource(
    private val movieApi: DocumentApi
) : DocumentDataSource.Remote {

    override suspend fun getDocuments(page: Int, limit: Int): Result<List<DocumentData>> = safeApiCall {
        movieApi.getDocuments(page, limit)
    }

    override suspend fun getDocuments(movieIds: List<Int>): Result<List<DocumentData>> = safeApiCall {
        movieApi.getDocuments(movieIds)
    }

    override suspend fun getDocument(movieId: Int): Result<DocumentData> = safeApiCall {
        movieApi.getDocument(movieId)
    }

    override suspend fun search(query: String, page: Int, limit: Int): Result<List<DocumentData>> = safeApiCall {
        movieApi.search(query, page, limit)
    }
}
