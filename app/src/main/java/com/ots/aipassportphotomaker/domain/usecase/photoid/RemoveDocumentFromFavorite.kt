package com.ots.aipassportphotomaker.domain.usecase.photoid

import com.ots.aipassportphotomaker.domain.repository.DocumentRepository

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class RemoveDocumentFromFavorite(
    private val documentRepository: DocumentRepository
) {
    suspend operator fun invoke(documentId: Int) = documentRepository.removeDocumentFromFavorite(documentId)
}