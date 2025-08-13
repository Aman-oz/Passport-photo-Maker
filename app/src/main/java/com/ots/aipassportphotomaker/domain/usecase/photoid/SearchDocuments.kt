package com.ots.aipassportphotomaker.domain.usecase.photoid

import androidx.paging.PagingData
import com.ots.aipassportphotomaker.domain.model.DocumentEntity
import com.ots.aipassportphotomaker.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozu Publishing. All rights reserved.
class SearchDocuments(
    private val documentRepository: DocumentRepository
) {
    operator fun invoke(query: String, pageSize: Int): Flow<PagingData<DocumentEntity>> = documentRepository.search(query, pageSize)
}