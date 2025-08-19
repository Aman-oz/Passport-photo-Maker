package com.ots.aipassportphotomaker.di.module

import android.content.Context
import androidx.room.Room
import com.ots.aipassportphotomaker.data.db.documents.DocumentDao
import com.ots.aipassportphotomaker.data.db.documents.DocumentDatabase
import com.ots.aipassportphotomaker.data.db.documents.DocumentRemoteKeyDao
import com.ots.aipassportphotomaker.data.db.favoritedocuments.FavoriteDocumentDao
import com.ots.aipassportphotomaker.data.util.DiskExecutor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Created by amanullah on 24/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideMovieDatabase(
        @ApplicationContext context: Context,
        diskExecutor: DiskExecutor
    ): DocumentDatabase {
        return Room
            .databaseBuilder(context, DocumentDatabase::class.java, "document.db")
            .setQueryExecutor(diskExecutor)
            .setTransactionExecutor(diskExecutor)
            .build()
    }

    @Provides
    fun provideMovieDao(movieDatabase: DocumentDatabase): DocumentDao {
        return movieDatabase.documentDao()
    }

    @Provides
    fun provideMovieRemoteKeyDao(movieDatabase: DocumentDatabase): DocumentRemoteKeyDao {
        return movieDatabase.documentRemoteKeysDao()
    }

    @Provides
    fun provideFavoriteMovieDao(movieDatabase: DocumentDatabase): FavoriteDocumentDao {
        return movieDatabase.favoriteDocumentDao()
    }
}