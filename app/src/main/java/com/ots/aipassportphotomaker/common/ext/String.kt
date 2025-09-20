package com.ots.aipassportphotomaker.common.ext

// Created by amanullah on 20/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

fun String.replaceSpaceWithUnderscore(): String {
    return this.replace(" ", "_").lowercase()
}