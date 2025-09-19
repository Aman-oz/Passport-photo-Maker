package com.ots.aipassportphotomaker.domain.repository

import androidx.paging.PagingData
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.domain.model.dbmodels.CreatedImageEntity
import kotlinx.coroutines.flow.Flow
import com.ots.aipassportphotomaker.domain.util.Result

/**
 * Created by Aman Ullah on 13/08/2025
 */
interface DocumentRepository {
    fun documents(pageSize: Int): Flow<PagingData<DocumentEntity>>
    fun documents(type: String, pageSize: Int): Flow<PagingData<DocumentEntity>>
    fun favoriteDocuments(pageSize: Int): Flow<PagingData<DocumentEntity>>
    fun search(query: String, pageSize: Int): Flow<PagingData<DocumentEntity>>
    suspend fun getDocument(documentId: Int): Result<DocumentEntity>
    suspend fun checkFavoriteStatus(documentId: Int): Result<Boolean>
    suspend fun addDocumentToFavorite(documentId: Int)
    suspend fun removeDocumentFromFavorite(documentId: Int)
    suspend fun sync(): Boolean

    suspend fun saveCreatedImage(createdImage: CreatedImageEntity)
    suspend fun getCreatedImagesByType(type: String): Result<List<CreatedImageEntity>>
    suspend fun getAllCreatedImages(): Result<List<CreatedImageEntity>>
}