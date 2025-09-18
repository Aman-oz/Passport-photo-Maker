package com.ots.aipassportphotomaker.domain.usecase.photoid

import com.ots.aipassportphotomaker.domain.model.dbmodels.CreatedImageEntity
import com.ots.aipassportphotomaker.domain.repository.DocumentRepository
import javax.inject.Inject

class SaveCreatedImageUseCase @Inject constructor(
    private val documentRepository: DocumentRepository
) {
    suspend operator fun invoke(createdImage: CreatedImageEntity) {
        documentRepository.saveCreatedImage(createdImage)
    }
}