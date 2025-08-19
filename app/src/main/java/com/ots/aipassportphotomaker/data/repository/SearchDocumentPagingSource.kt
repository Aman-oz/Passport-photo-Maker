package com.ots.aipassportphotomaker.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.ots.aipassportphotomaker.data.model.DocumentData
import com.ots.aipassportphotomaker.data.util.source.DocumentDataSource
import com.ots.aipassportphotomaker.domain.util.Result
import com.ots.aipassportphotomaker.domain.util.Result.Success

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.


private const val STARTING_PAGE_INDEX = 1

class SearchDocumentPagingSource(
    private val query: String,
    private val remote: DocumentDataSource.Remote
) : PagingSource<Int, DocumentData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DocumentData> {
        val page = params.key ?: STARTING_PAGE_INDEX

        return when (val result = remote.search(query, page, params.loadSize)) {
            is Success -> LoadResult.Page(
                data = result.data.distinctBy { movie -> movie.id },
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (result.data.isEmpty()) null else page + 1
            )

            is Result.Error -> LoadResult.Error(result.error)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DocumentData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}