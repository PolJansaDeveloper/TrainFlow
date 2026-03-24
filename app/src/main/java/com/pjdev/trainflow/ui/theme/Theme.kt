package com.pjdev.trainflow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val RestDayBorderStart = Color(0xFF9CCF9B)   // verde salvia suave
val RestDayBorderEnd = Color(0xFFFFF6D6)     // blanco cálido con toque crema

private val TrainFlowLightColors = lightColorScheme(

    primary = Color(0xFFE53935),            // rojo fuego principal
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),

    secondary = Color(0xFF7F1D1D),          // rojo oscuro (para gradients)
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF8D7DA),
    onSecondaryContainer = Color(0xFF2C0A0A),

    tertiary = Color(0xFFB91C1C),           // rojo intenso
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFCD5D5),
    onTertiaryContainer = Color(0xFF3B0A0A),

    background = Color(0xFFF7F5F3),         // fondo claro elegante
    onBackground = Color(0xFF111111),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111111),

    surfaceVariant = Color(0xFFF1ECE8),
    onSurfaceVariant = Color(0xFF5B5550),

    outline = Color(0xFFD7CEC8)
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