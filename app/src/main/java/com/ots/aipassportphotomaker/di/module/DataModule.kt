package com.ots.aipassportphotomaker.di.module

import android.content.Context
import com.aman.downloader.DownloaderConfig
import com.aman.downloader.OziDownloader
import com.ots.aipassportphotomaker.data.db.documents.DocumentDao
import com.ots.aipassportphotomaker.data.db.documents.DocumentRemoteKeyDao
import com.ots.aipassportphotomaker.data.db.favoritedocuments.FavoriteDocumentDao
import com.ots.aipassportphotomaker.data.remote.api.DocumentApi
import com.ots.aipassportphotomaker.data.remote.api.RemoveBackgroundApi
import com.ots.aipassportphotomaker.data.repository.DocumentAssetDataSource
import com.ots.aipassportphotomaker.data.repository.DocumentLocalDataSource
import com.ots.aipassportphotomaker.data.repository.DocumentRemoteDataSource
import com.ots.aipassportphotomaker.data.repository.DocumentRepositoryImpl
import com.ots.aipassportphotomaker.data.repository.RemoveBackgroundRepositoryImpl
import com.ots.aipassportphotomaker.data.repository.SuitsDataSource
import com.ots.aipassportphotomaker.data.repository.favorite.FavoriteDocumentsDataSource
import com.ots.aipassportphotomaker.data.repository.favorite.FavoriteDocumentsLocalDataSource
import com.ots.aipassportphotomaker.data.util.DocumentRemoteMediator
import com.ots.aipassportphotomaker.data.util.NetworkMonitorImpl
import com.ots.aipassportphotomaker.data.util.source.DocumentDataSource
import com.ots.aipassportphotomaker.domain.repository.DocumentRepository
import com.ots.aipassportphotomaker.domain.repository.RemoveBackgroundRepository
import com.ots.aipassportphotomaker.domain.usecase.photoid.AddDocumentToFavorite
import com.ots.aipassportphotomaker.domain.usecase.photoid.CheckFavoriteStatus
import com.ots.aipassportphotomaker.domain.usecase.photoid.GetDocumentDetails
import com.ots.aipassportphotomaker.domain.usecase.photoid.GetFavoriteDocuments
import com.ots.aipassportphotomaker.domain.usecase.photoid.RemoveDocumentFromFavorite
import com.ots.aipassportphotomaker.domain.usecase.photoid.SearchDocuments
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Created by amanullah on 25/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor = NetworkMonitorImpl(context)

    @Provides
    fun provideDocumentAssetDataSource(@ApplicationContext context: Context): DocumentAssetDataSource {
        return DocumentAssetDataSource(context)
    }

    @Provides
    @Singleton
    fun provideDownloaderConfig(): DownloaderConfig {
        return DownloaderConfig()
    }

    @Provides
    @Singleton
    fun provideOziDownloader(
        @ApplicationContext context: Context,
        config: DownloaderConfig
    ): OziDownloader {
        return OziDownloader.create(context, config)
    }

    @Provides
    @Singleton
    fun provideDocumentRepository(
        documentRemote: DocumentDataSource.Remote,
        documentLocal: DocumentDataSource.Local,
        assetDataSource: DocumentAssetDataSource,
        movieRemoteMediator: DocumentRemoteMediator,
        favoriteLocal: FavoriteDocumentsDataSource.Local,
    ): DocumentRepository {
        return DocumentRepositoryImpl(documentRemote, documentLocal, assetDataSource,movieRemoteMediator, favoriteLocal)
    }

    @Provides
    @Singleton
    fun provideDocumentRemoveDataSource(movieApi: DocumentApi): DocumentDataSource.Remote {
        return DocumentRemoteDataSource(movieApi)
    }

    @Provides
    @Singleton
    fun provideDocumentLocalDataSource(
        documentDao: DocumentDao,
        documentRemoteKeyDao: DocumentRemoteKeyDao,
    ): DocumentDataSource.Local {
        return DocumentLocalDataSource(documentDao, documentRemoteKeyDao)
    }

    @Provides
    @Singleton
    fun provideSuitsDataSource(@ApplicationContext context: Context): SuitsDataSource {
        return SuitsDataSource(context)
    }

    @Provides
    @Singleton
    fun provideDocumentMediator(
        movieLocalDataSource: DocumentDataSource.Local,
        movieRemoteDataSource: DocumentDataSource.Remote
    ): DocumentRemoteMediator {
        return DocumentRemoteMediator(movieLocalDataSource, movieRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideFavoriteDocumentLocalDataSource(
        favoriteMovieDao: FavoriteDocumentDao
    ): FavoriteDocumentsDataSource.Local {
        return FavoriteDocumentsLocalDataSource(favoriteMovieDao)
    }

    @Provides
    fun provideSearchDocumentsUseCase(movieRepository: DocumentRepository): SearchDocuments {
        return SearchDocuments(movieRepository)
    }

    @Provides
    fun provideGetDocumentDetailsUseCase(movieRepository: DocumentRepository): GetDocumentDetails {
        return GetDocumentDetails(movieRepository)
    }

    @Provides
    fun provideGetFavoriteDocumentsUseCase(movieRepository: DocumentRepository): GetFavoriteDocuments {
        return GetFavoriteDocuments(movieRepository)
    }

    @Provides
    fun provideCheckFavoriteStatusUseCase(movieRepository: DocumentRepository): CheckFavoriteStatus {
        return CheckFavoriteStatus(movieRepository)
    }

    @Provides
    fun provideAddDocumentToFavoriteUseCase(movieRepository: DocumentRepository): AddDocumentToFavorite {
        return AddDocumentToFavorite(movieRepository)
    }

    @Provides
    fun provideRemoveDocumentFromFavoriteUseCase(movieRepository: DocumentRepository): RemoveDocumentFromFavorite {
        return RemoveDocumentFromFavorite(movieRepository)
    }
}