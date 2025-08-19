package com.ots.aipassportphotomaker.data.db.documents

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ots.aipassportphotomaker.data.model.DocumentRemoteKeyDbData

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Dao
interface DocumentRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRemoteKey(keys: DocumentRemoteKeyDbData)

    @Query("SELECT * FROM documents_remote_keys WHERE id=:id")
    suspend fun getRemoteKeyByMovieId(id: Int): DocumentRemoteKeyDbData?

    @Query("DELETE FROM documents_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("SELECT * FROM documents_remote_keys WHERE id = (SELECT MAX(id) FROM documents_remote_keys)")
    suspend fun getLastRemoteKey(): DocumentRemoteKeyDbData?
}