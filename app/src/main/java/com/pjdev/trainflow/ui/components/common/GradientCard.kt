package com.pjdev.trainflow.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.ui.theme.RestDayBorderEnd
import com.pjdev.trainflow.ui.theme.RestDayBorderStart

@Composable
fun GradientBorderCard(
    modifier: Modifier = Modifier,
    isRestDay: Boolean = false,
    content: @Composable () -> Unit

) {
    val shape = RoundedCornerShape(22.dp)

    val borderBrush =
        if (isRestDay) Brush.linearGradient(
            colors = listOf(
                RestDayBorderStart,
                RestDayBorderEnd
            )
        ) else {
            Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.primary
                )
            )
        }

    val glowBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        )
    )

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(glowBrush)
                .blur(12.dp)
        )

        Box(
            modifier = Modifier
                .background(
                    brush = borderBrush,
                    shape = shape
                )
                .padding(1.5.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(21.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
            ) {
                content()
            }
        }
    }
}
