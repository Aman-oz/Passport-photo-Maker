package com.ots.aipassportphotomaker.domain.repository

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.ots.aipassportphotomaker.presentation.ui.theme.AppColors
import javax.inject.Inject
import javax.inject.Singleton

// Created by amanullah on 26/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Singleton
class ColorFactory @Inject constructor() {

    // Available color options
    enum class ColorType {
        CUSTOM,
        TRANSPARENT,
        WHITE,
        GREEN,
        BLUE,
        RED
    }

    // State holders
    private val _selectedColorType = mutableStateOf(ColorType.TRANSPARENT)
    private val _customColor = mutableStateOf(Color.Transparent)
    private val _selectedColor = mutableStateOf(Color.Transparent)

    // Public getters
    val selectedColorType get() = _selectedColorType.value
    val customColor get() = _customColor.value
    val selectedColor get() = _selectedColor.value

    // Predefined colors map
    private val predefinedColors = mapOf(
        ColorType.TRANSPARENT to Color.Transparent,
        ColorType.WHITE to Color.White,
        ColorType.GREEN to Color.Green,
        ColorType.BLUE to AppColors.LightPrimary,
        ColorType.RED to Color.Red
    )

    // Get color by type
    fun getColorByType(type: ColorType): Color {
        return when (type) {
            ColorType.CUSTOM -> _customColor.value
            else -> predefinedColors[type] ?: Color.White
        }
    }

    // Set custom color from color picker
    fun setCustomColor(color: Color) {
        _customColor.value = color
        _selectedColorType.value = ColorType.CUSTOM
        _selectedColor.value = color
    }

    // Select predefined color
    fun selectColor(type: ColorType) {
        _selectedColorType.value = type
        _selectedColor.value = getColorByType(type)
    }

    // Get all available colors with their types
    fun getAllColors(): List<Pair<ColorType, Color>> {
        return listOf(
            ColorType.CUSTOM to _customColor.value,
            ColorType.TRANSPARENT to Color.Transparent,
            ColorType.WHITE to Color.White,
            ColorType.GREEN to Color.Green,
            ColorType.BLUE to AppColors.LightPrimary,
            ColorType.RED to Color.Red
        )
    }

    // Reset to default
    fun resetToDefault() {
        _selectedColorType.value = ColorType.TRANSPARENT
        _selectedColor.value = Color.Transparent
        _customColor.value = Color.Transparent
    }

    // Check if custom color is selected and different from default colors
    fun isCustomColorSelected(): Boolean {
        return _selectedColorType.value == ColorType.CUSTOM &&
                !predefinedColors.values.contains(_customColor.value)
    }
}