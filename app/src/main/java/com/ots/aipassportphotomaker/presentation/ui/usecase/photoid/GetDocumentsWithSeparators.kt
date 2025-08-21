package com.ots.aipassportphotomaker.presentation.ui.usecase.photoid

import androidx.paging.PagingData
import androidx.paging.map
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.domain.repository.DocumentRepository
import com.ots.aipassportphotomaker.presentation.ui.mapper.toPresentation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.text.insert

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class GetDocumentsWithSeparators @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val insertSeparatorIntoPagingData: InsertSeparatorIntoPagingData
) {

    fun documents(pageSize: Int): Flow<PagingData<DocumentListItem>> = documentRepository.documents(pageSize).map {
        val pagingData: PagingData<DocumentListItem.Document> = it.map { document ->
            Logger.d("GetDocumentsWithSeparators", "Mapping document: ${document.name}")
            document.toPresentation()
        }
        insertSeparatorIntoPagingData.insert(pagingData)
    }
}