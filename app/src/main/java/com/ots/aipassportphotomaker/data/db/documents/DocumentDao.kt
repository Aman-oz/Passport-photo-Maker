package com.ots.aipassportphotomaker.data.db.documents

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ots.aipassportphotomaker.data.model.DocumentDbData
import com.ots.aipassportphotomaker.domain.model.dbmodels.CreatedImageDbData

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents ORDER BY name")
    fun documents(): PagingSource<Int, DocumentDbData>

    /**
     * Get all Documents from the Documents table.
     *
     * @return all Documents.
     */
    @Query("SELECT * FROM documents ORDER BY name")
    fun getDocuments(): List<DocumentDbData>

    /**
     * Get Document by id.
     * **/
    @Query("SELECT * FROM documents WHERE id = :movieId")
    suspend fun getMovie(movieId: Int): DocumentDbData?

    /**
     * Insert all Documents.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDocuments(movies: List<DocumentDbData>)

    /**
     * Delete all Documents except favorites.
     */
    @Query("DELETE FROM documents WHERE id NOT IN (SELECT documentId FROM favorite_documents)")
    suspend fun clearDocumentsExceptFavorites()

//    ********************
// Save a single created image (similar to saveDocuments but for single)
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun saveCreatedImage(createdImage: CreatedImageDbData)

    // Get created images by type (for history screen, filtered by type like "passport")
    @Query("SELECT * FROM created_images WHERE type = :type ORDER BY id DESC") // DESC for newest first
    suspend fun getCreatedImagesByType(type: String): List<CreatedImageDbData>

    // Optional: Get all for general history
    @Query("SELECT * FROM created_images ORDER BY id DESC")
    suspend fun getAllCreatedImages(): List<CreatedImageDbData>

    // Optional: Get by ID if needed
    @Query("SELECT * FROM created_images WHERE id = :id")
    suspend fun getCreatedImageById(id: Int): CreatedImageDbData?
}