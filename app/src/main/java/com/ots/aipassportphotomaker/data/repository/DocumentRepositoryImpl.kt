package com.ots.aipassportphotomaker.data.repository

import com.ots.aipassportphotomaker.data.repository.favorite.FavoriteDocumentsDataSource
import com.ots.aipassportphotomaker.data.util.DocumentRemoteMediator
import com.ots.aipassportphotomaker.data.util.source.DocumentDataSource

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class DocumentRepositoryImpl(
    private val remote: DocumentDataSource.Remote,
    private val local: DocumentDataSource.Local,
    private val remoteMediator: DocumentRemoteMediator,
    private val localFavorite: FavoriteDocumentsDataSource.Local
) {
}