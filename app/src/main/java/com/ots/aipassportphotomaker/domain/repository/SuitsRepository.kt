package com.ots.aipassportphotomaker.domain.repository

import androidx.paging.PagingData
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.model.SuitsEntity
import kotlinx.coroutines.flow.Flow

// Created by amanullah on 09/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
interface SuitsRepository {
    fun suits(pageSize: Int): Flow<PagingData<SuitsEntity>>
}