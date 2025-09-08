package com.ots.aipassportphotomaker.domain.model

// Created by amanullah on 04/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
enum class ProcessingStage {
    NONE,
    UPLOADING,
    PROCESSING,
    CROPPING_IMAGE,
    COMPLETED,
    DOWNLOADING,
    SAVING_IMAGE,
    BACKGROUND_REMOVAL,
    ERROR,
    NO_NETWORK_AVAILABLE
}
