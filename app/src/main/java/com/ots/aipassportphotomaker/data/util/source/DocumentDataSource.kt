package com.ots.aipassportphotomaker.data.util.source

import androidx.paging.PagingSource
import com.ots.aipassportphotomaker.data.model.DocumentData
import com.ots.aipassportphotomaker.data.model.DocumentDbData
import com.ots.aipassportphotomaker.data.model.DocumentRemoteKeyDbData
import com.ots.aipassportphotomaker.data.model.mapper.CreatedImageData
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.dbmodels.CreatedImageEntity
import com.ots.aipassportphotomaker.domain.util.Result

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
interface DocumentDataSource {

    interface Remote {
        suspend fun getDocuments(page: Int, limit: Int): Result<List<DocumentData>>
        suspend fun getDocuments(movieIds: List<Int>): Result<List<DocumentData>>
        suspend fun getDocument(movieId: Int): Result<DocumentData>
        suspend fun search(query: String, page: Int, limit: Int): Result<List<DocumentData>>
    }

    interface Local {
        fun documents(): PagingSource<Int, DocumentDbData>
        suspend fun getDocuments(): Result<List<DocumentEntity>>
        suspend fun getDocument(movieId: Int): Result<DocumentEntity>
        /*suspend fun getDocumentsFromJson() : Result<List<DocumentData>>
        suspend fun getDocumentFromJson(movieId: Int): Result<DocumentData>*/
        suspend fun saveCreatedImage(createdImage: CreatedImageData)
        suspend fun getCreatedImagesByType(type: String): Result<List<CreatedImageEntity>>
        suspend fun saveDocuments(movies: List<DocumentData>)
        suspend fun getLastRemoteKey(): DocumentRemoteKeyDbData?
        suspend fun saveRemoteKey(key: DocumentRemoteKeyDbData)
        suspend fun clearDocuments()
        suspend fun clearRemoteKeys()
    }
}