package com.ots.aipassportphotomaker.data.util

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.data.model.DocumentDbData
import com.ots.aipassportphotomaker.data.model.DocumentRemoteKeyDbData
import com.ots.aipassportphotomaker.data.util.source.DocumentDataSource
import com.ots.aipassportphotomaker.domain.util.Result

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

private const val MOVIE_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class DocumentRemoteMediator(
    private val local: DocumentDataSource.Local,
    private val remote: DocumentDataSource.Remote
) : RemoteMediator<Int, DocumentDbData>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, DocumentDbData>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> MOVIE_STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> local.getLastRemoteKey()?.nextPage ?: return MediatorResult.Success(endOfPaginationReached = true)
        }

        Logger.d("Document", "MovieRemoteMediator: load() called with: loadType = $loadType, page: $page, stateLastItem = ${state.isEmpty()}")

        // There was a lag in loading the first page; as a result, it jumps to the end of the pagination.
        if (state.isEmpty() && page == 2) return MediatorResult.Success(endOfPaginationReached = false)

        when (val result = remote.getDocuments(page, state.config.pageSize)) {
            is Result.Success -> {
                Logger.d("Document", "MovieRemoteMediator: get movies from remote")
                if (loadType == LoadType.REFRESH) {
                    local.clearDocuments()
                    local.clearRemoteKeys()
                }

                val documents = result.data

                val endOfPaginationReached = documents.isEmpty()

                val prevPage = if (page == MOVIE_STARTING_PAGE_INDEX) null else page - 1
                val nextPage = if (endOfPaginationReached) null else page + 1

                val key = DocumentRemoteKeyDbData(prevPage = prevPage, nextPage = nextPage)

                local.saveDocuments(documents)
                local.saveRemoteKey(key)

                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }

            is Result.Error -> {
                return MediatorResult.Error(result.error)
            }
        }
    }
}