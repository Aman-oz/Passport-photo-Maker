package com.ots.aipassportphotomaker.domain.repository

import androidx.paging.PagingData
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import kotlinx.coroutines.flow.Flow
import com.ots.aipassportphotomaker.domain.util.Result

/**
 * Created by Aman Ullah on 13/08/2025
 */
interface DocumentRepository {
    fun documents(pageSize: Int): Flow<PagingData<DocumentEntity>>
    fun favoriteDocuments(pageSize: Int): Flow<PagingData<DocumentEntity>>
    fun search(query: String, pageSize: Int): Flow<PagingData<DocumentEntity>>
    suspend fun getDocument(movieId: Int): Result<DocumentEntity>
    suspend fun checkFavoriteStatus(movieId: Int): Result<Boolean>
    suspend fun addDocumentToFavorite(movieId: Int)
    suspend fun removeDocumentFromFavorite(movieId: Int)
    suspend fun sync(): Boolean
}