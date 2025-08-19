package com.ots.aipassportphotomaker.data.db.favoritedocuments

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ots.aipassportphotomaker.data.model.DocumentDbData
import com.ots.aipassportphotomaker.domain.model.FavoriteDocumentDbData

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Dao
interface FavoriteDocumentDao {

    @Query("SELECT * FROM documents where id in (SELECT documentId FROM favorite_documents)")
    fun favoriteDocuments(): PagingSource<Int, DocumentDbData>

    @Query("SELECT * FROM favorite_documents")
    suspend fun getAll(): List<FavoriteDocumentDbData>

    @Query("SELECT * FROM favorite_documents where documentId=:movieId")
    suspend fun get(movieId: Int): FavoriteDocumentDbData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(favoriteMovieDbData: FavoriteDocumentDbData)

    @Query("DELETE FROM favorite_documents WHERE documentId=:movieId")
    suspend fun remove(movieId: Int)
}