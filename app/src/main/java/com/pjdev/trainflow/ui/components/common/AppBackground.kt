package com.pjdev.trainflow.ui.components.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
private fun appBackgroundGradient(): Brush {
    val colors = if (MaterialTheme.colorScheme.background.luminance() > 0.5f) {
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            MaterialTheme.colorScheme.background
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.background
        )
    }
    return Brush.verticalGradient(colors)
}

@Composable
private fun topGlowColor(): Color =
    MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)

@Composable
private fun bottomGlowColor(): Color =
    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f)

@Composable
fun HomeBackground(modifier: Modifier = Modifier) {
    val backgroundBrush = appBackgroundGradient()
    val topGlow = topGlowColor()
    val bottomGlow = bottomGlowColor()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(topGlow, Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.12f),
                    radius = size.minDimension * 0.35f
                ),
                radius = size.minDimension * 0.35f,
                center = Offset(size.width * 0.85f, size.height * 0.12f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(bottomGlow, Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.75f),
                    radius = size.minDimension * 0.4f
                ),
                radius = size.minDimension * 0.4f,
                center = Offset(size.width * 0.15f, size.height * 0.75f)
            )
        }
    }
}