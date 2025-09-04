package com.ots.aipassportphotomaker.domain.model

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
enum class ProcessingStage {
    NONE,
    UPLOADING,
    PROCESSING,
    BACKGROUND_REMOVAL,
    COMPLETED,
    NO_NETWORK_AVAILABLE
}
