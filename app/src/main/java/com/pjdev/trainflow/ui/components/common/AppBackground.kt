package com.pjdev.trainflow.ui.components.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pjdev.trainflow.R
import com.pjdev.trainflow.ui.theme.RestDayBorderEnd
import com.pjdev.trainflow.ui.theme.RestDayBorderStart

@Composable
fun HomeBackground(
    modifier: Modifier = Modifier,
    isRestDay: Boolean = false
) {
    val backgroundBrush = if (isRestDay) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF8F4F1),
                Color(0xFFF1E8E2),
                Color(0xFFEAE0D8)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF7F4F2),
                Color(0xFFEEE7E1),
                Color(0xFFE5DBD3)
            )
        )
    }

    val accentStrong = if (isRestDay) {
        RestDayBorderStart.copy(alpha = 0.18f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
    }

    val accentSoft = if (isRestDay) {
        RestDayBorderEnd.copy(alpha = 0.16f)
    } else {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
    }

    val stripeColorTop = if (isRestDay) {
        RestDayBorderStart.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val minDim = size.minDimension

            // Glow superior derecho
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accentStrong, Color.Transparent),
                    center = Offset(w * 0.88f, h * 0.10f),
                    radius = minDim * 0.34f
                ),
                radius = minDim * 0.34f,
                center = Offset(w * 0.88f, h * 0.10f)
            )

            // Glow suave inferior izquierdo para dar profundidad
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accentSoft, Color.Transparent),
                    center = Offset(w * 0.10f, h * 0.88f),
                    radius = minDim * 0.42f
                ),
                radius = minDim * 0.42f,
                center = Offset(w * 0.10f, h * 0.88f)
            )

            // Banda diagonal superior completa de lado a lado
            rotate(
                degrees = -18f,
                pivot = Offset(w * 0.5f, h * 0.18f)
            ) {
                drawRoundRect(
                    color = stripeColorTop,
                    topLeft = Offset(-w * 0.22f, h * 0.055f),
                    size = Size(
                        width = w * 1.45f,
                        height = h * 0.19f
                    ),
                    cornerRadius = CornerRadius(
                        x = minDim * 0.11f,
                        y = minDim * 0.11f
                    )
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.background_vikingo),
            contentDescription = "Viking",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(140.dp)
                .offset(
                    x = (-12).dp,
                    y = 28.dp
                )
                .alpha(0.7f)
        )
    }
}