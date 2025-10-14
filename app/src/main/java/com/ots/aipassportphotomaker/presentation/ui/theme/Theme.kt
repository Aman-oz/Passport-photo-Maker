package com.ots.aipassportphotomaker.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Standard Material3 color schemes
private val DarkColorScheme = darkColorScheme(
    primary = AppColors.DarkPrimary,
    onPrimary = AppColors.DarkOnPrimary,
    primaryContainer = AppColors.DarkPrimaryContainer,
    onPrimaryContainer = AppColors.DarkOnPrimaryContainer,
    secondary = AppColors.DarkSecondary,
    onSecondary = AppColors.DarkOnSecondary,
    secondaryContainer = AppColors.DarkSecondaryContainer,
    onSecondaryContainer = AppColors.DarkOnSecondaryContainer,
    tertiary = AppColors.DarkTertiary,
    onTertiary = AppColors.DarkOnTertiary,
    tertiaryContainer = AppColors.DarkTertiaryContainer,
    onTertiaryContainer = AppColors.DarkOnTertiaryContainer,
    background = AppColors.DarkBackground,
    onBackground = AppColors.DarkOnBackground,
    surface = AppColors.DarkSurface,
    onSurface = AppColors.DarkOnSurface,
    surfaceVariant = AppColors.DarkSurfaceVariant,
    onSurfaceVariant = AppColors.DarkOnSurfaceVariant,
    outline = AppColors.DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.LightPrimary,
    onPrimary = AppColors.LightOnPrimary,
    primaryContainer = AppColors.LightPrimaryContainer,
    onPrimaryContainer = AppColors.LightOnPrimaryContainer,
    secondary = AppColors.LightSecondary,
    onSecondary = AppColors.LightOnSecondary,
    secondaryContainer = AppColors.LightSecondaryContainer,
    onSecondaryContainer = AppColors.LightOnSecondaryContainer,
    tertiary = AppColors.LightTertiary,
    onTertiary = AppColors.LightOnTertiary,
    tertiaryContainer = AppColors.LightTertiaryContainer,
    onTertiaryContainer = AppColors.LightOnTertiaryContainer,
    background = AppColors.LightBackground,
    onBackground = AppColors.LightOnBackground,
    surface = AppColors.LightSurface,
    onSurface = AppColors.LightOnSurface,
    surfaceVariant = AppColors.LightSurfaceVariant,
    onSurfaceVariant = AppColors.LightOnSurfaceVariant,
    outline = AppColors.LightOutline
)

// Extend ColorScheme to include custom colors

val ColorScheme.custom100: Color
    get() = if (this == darkColors) AppColors.Dark100 else AppColors.Light100

val ColorScheme.onCustom100: Color
    get() = if (this == darkColors) AppColors.DarkOn100 else AppColors.LightOn100

val ColorScheme.custom200: Color
    get() = if (this == darkColors) AppColors.Dark200 else AppColors.Light200

val ColorScheme.onCustom200: Color
    get() = if (this == darkColors) AppColors.DarkOn200 else AppColors.LightOn200

val ColorScheme.custom300: Color
    get() = if (this == darkColors) AppColors.Dark300 else AppColors.Light300

val ColorScheme.onCustom300: Color
    get() = if (this == darkColors) AppColors.DarkOn300 else AppColors.LightOn300

val ColorScheme.custom400: Color
    get() = if (this == darkColors) AppColors.Dark400 else AppColors.Light400

val ColorScheme.onCustom400: Color
    get() = if (this == darkColors) AppColors.DarkOn400 else AppColors.LightOn400

val ColorScheme.customSuccess: Color
    get() = if (this == darkColors) AppColors.DarkSuccess else AppColors.LightSuccess

val ColorScheme.onCustomSuccess: Color
    get() = if (this == darkColors) AppColors.DarkOnSuccess else AppColors.LightOnSuccess

val ColorScheme.customSuccessContainer: Color
    get() = if (this == darkColors) AppColors.DarkSuccessContainer else AppColors.LightSuccessContainer

val ColorScheme.onCustomSuccessContainer: Color
    get() = if (this == darkColors) AppColors.DarkOnSuccessContainer else AppColors.LightOnSuccessContainer

val ColorScheme.customError: Color
    get() = if (this == darkColors) AppColors.DarkError else AppColors.LightError

val ColorScheme.onCustomError: Color
    get() = if (this == darkColors) AppColors.DarkOnError else AppColors.LightOnError

val ColorScheme.customErrorContainer: Color
    get() = if (this == darkColors) AppColors.DarkErrorContainer else AppColors.LightErrorContainer

val ColorScheme.onCustomErrorContainer: Color
    get() = if (this == darkColors) AppColors.DarkOnErrorContainer else AppColors.LightOnErrorContainer

val ColorScheme.customWarning: Color
    get() = if (this == darkColors) AppColors.DarkWarning else AppColors.LightWarning

val ColorScheme.onCustomWarning: Color
    get() = if (this == darkColors) AppColors.DarkOnWarning else AppColors.LightOnWarning

val ColorScheme.customWarningContainer: Color
    get() = if (this == darkColors) AppColors.DarkWarningContainer else AppColors.LightWarningContainer

val ColorScheme.onCustomWarningContainer: Color
    get() = if (this == darkColors) AppColors.DarkOnWarningContainer else AppColors.LightOnWarningContainer

val ColorScheme.nativeAdButtonColor: Color
    get() = if (this == darkColors) AppColors.DarkNativeCTAColor else AppColors.LightNativeCTAColor

// Define dark and light color schemes with custom colors
val darkColors = DarkColorScheme.copy(
    primary = AppColors.DarkPrimary,
    onPrimary = AppColors.DarkOnPrimary,
    primaryContainer = AppColors.DarkPrimaryContainer,
    onPrimaryContainer = AppColors.DarkOnPrimaryContainer,
    secondaryContainer = AppColors.DarkSecondaryContainer,
    onSecondaryContainer = AppColors.DarkOnSecondaryContainer,
    background = AppColors.DarkBackground,
    onBackground = AppColors.DarkOnBackground,
    surface = AppColors.DarkSurface,
    onSurface = AppColors.DarkOnSurface,
    surfaceVariant = AppColors.DarkSurfaceVariant,
    onSurfaceVariant = AppColors.DarkOnSurfaceVariant
)

val lightColors = LightColorScheme.copy(
    primary = AppColors.LightPrimary,
    onPrimary = AppColors.LightOnPrimary,
    primaryContainer = AppColors.LightPrimaryContainer,
    onPrimaryContainer = AppColors.LightOnPrimaryContainer,
    secondaryContainer = AppColors.LightSecondaryContainer,
    onSecondaryContainer = AppColors.LightOnSecondaryContainer,
    background = AppColors.LightBackground,
    onBackground = AppColors.LightOnBackground,
    surface = AppColors.LightSurface,
    onSurface = AppColors.LightOnSurface,
    surfaceVariant = AppColors.LightSurfaceVariant
)

lateinit var colors: ColorScheme

@Composable
fun AIPassportPhotoMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    /*val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }*/

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColors
        else -> lightColors
    }

    colors = colorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}