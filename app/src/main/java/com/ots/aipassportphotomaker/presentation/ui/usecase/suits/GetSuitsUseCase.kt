package com.ots.aipassportphotomaker.presentation.ui.usecase.suits

import androidx.paging.PagingData
import androidx.paging.map
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.data.model.SuitsJson
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import com.ots.aipassportphotomaker.domain.model.SuitsEntity
import com.ots.aipassportphotomaker.domain.repository.DocumentRepository
import com.ots.aipassportphotomaker.domain.repository.SuitsRepository
import com.ots.aipassportphotomaker.presentation.ui.mapper.toPresentation
import com.ots.aipassportphotomaker.presentation.ui.usecase.photoid.InsertSeparatorIntoPagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.text.insert

// Created by amanullah on 13/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class GetSuitsUseCase @Inject constructor(
    private val suitsRepository: SuitsRepository
) {

    fun suits(pageSize: Int): Flow<PagingData<SuitsEntity>> = suitsRepository.suits(pageSize)

}