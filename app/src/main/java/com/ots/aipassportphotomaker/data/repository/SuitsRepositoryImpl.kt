package com.ots.aipassportphotomaker.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.SuitsEntity
import com.ots.aipassportphotomaker.domain.repository.SuitsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Created by amanullah on 09/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class SuitsRepositoryImpl(
    private val suitsDataSource: SuitsDataSource
): SuitsRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun suits(pageSize: Int): Flow<PagingData<SuitsEntity>> {
        return flow {
            val suits = suitsDataSource.getSuits()
            Logger.d("SuitsRepositoryImpl", "suits: ${suits.size} suits loaded from JSON")
            val pagingData = PagingData.from(suits)
            emit(pagingData)
        }
    }
}