package com.pjdev.trainflow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TrainFlowLightColors = lightColorScheme(
    primary = Color(0xFF2563EB),          // azul potente
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE8FF),
    onPrimaryContainer = Color(0xFF0B1F3A),

    secondary = Color(0xFFFF7A59),        // coral energético
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE2D9),
    onSecondaryContainer = Color(0xFF3A1208),

    tertiary = Color(0xFF14B8A6),         // turquesa fitness
    onTertiary = Color.White,

    background = Color(0xFFF4F7FB),
    onBackground = Color(0xFF111827),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),

    surfaceVariant = Color(0xFFEEF2F7),
    onSurfaceVariant = Color(0xFF4B5563),

    outline = Color(0xFFD1D5DB)
)

@Composable
fun TrainFlowTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TrainFlowLightColors,
        typography = AppTypography,
        content = content
    )
}