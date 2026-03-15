package com.pjdev.trainflow.ui.components.planner

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.ui.components.common.dayLabels
import com.pjdev.trainflow.ui.components.common.infoGradient

@Composable
private fun restAccentColor() = MaterialTheme.colorScheme.outline

@Composable
private fun workoutAccentColor() = MaterialTheme.colorScheme.tertiary

@Composable
private fun restChipBackground() = MaterialTheme.colorScheme.surfaceVariant

@Composable
private fun workoutChipBackground() =
    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f)

@Composable
private fun restChipContent() = MaterialTheme.colorScheme.onSurfaceVariant

@Composable
private fun workoutChipContent() = MaterialTheme.colorScheme.onTertiaryContainer

@Composable
fun PlannerHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    BackHandler(onBack = onBack)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.size(30.dp),
            onClick = onBack,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
            tonalElevation = 2.dp,
            shadowElevation = 6.dp

        ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )

        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WeeklySummaryCard(days: List<DayWorkout>) {
    val trainingDays = days.count { !it.isRestDay }
    val restDays = days.count { it.isRestDay }
    val totalExercises = days.filterNot { it.isRestDay }.sumOf { it.exercises.size }
    val gradient = infoGradient()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        shadowElevation = 8.dp,
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .background(gradient)
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Your weekly overview",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                Alignment.CenterVertically
            ) {
                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    value = trainingDays.toString(),
                    label = "Training days"
                )
                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    value = restDays.toString(),
                    label = "Recovery days"
                )
                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    value = totalExercises.toString(),
                    label = "Training hours"
                )
            }
        }
    }
}

@Composable
private fun SummaryMiniCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(onPrimary.copy(alpha = 0.14f))
            .padding(vertical = 14.dp, horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = onPrimary,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = onPrimary.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PlannerDayCard(
    day: DayWorkout,
    onEdit: () -> Unit,
    onOpenWorkout: () -> Unit
) {
    val accentColor = if (day.isRestDay) {
        restAccentColor()
    } else {
        workoutAccentColor()
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 6.dp, height = 56.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(accentColor)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = dayLabels[day.dayOfWeek - 1],
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (day.isRestDay) "Rest day" else day.workoutName.ifBlank { "Workout" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (!day.isRestDay) {
                        Text(
                            text = "${day.exercises.size} exercises planned",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                DayStatusChip(isRestDay = day.isRestDay)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!day.isRestDay) {
                    Button(
                        onClick = onOpenWorkout,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Open")
                    }
                }

                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("Edit")
                }
            }
        }
    }
}

@Composable
private fun DayStatusChip(isRestDay: Boolean) {
    val bg = if (isRestDay) restChipBackground() else workoutChipBackground()
    val content = if (isRestDay) restChipContent() else workoutChipContent()

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = if (isRestDay) "Recovery" else "Workout",
            style = MaterialTheme.typography.labelLarge,
            color = content
        )
    }
}