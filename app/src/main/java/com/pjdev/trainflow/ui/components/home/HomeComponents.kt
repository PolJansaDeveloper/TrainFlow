package com.pjdev.trainflow.ui.components.home

import com.pjdev.trainflow.ui.components.common.GradientBorderCard
import com.pjdev.trainflow.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.domain.model.WorkoutBlock
import com.pjdev.trainflow.domain.model.toReadableDuration
import com.pjdev.trainflow.ui.theme.RestDayBorderEnd
import com.pjdev.trainflow.ui.theme.RestDayBorderStart
import androidx.compose.material.icons.filled.FitnessCenter



@Composable
fun HomeHeader(
    isRestDay: Boolean,
    onHistory: () -> Unit,
    onSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopActionIcon(
                icon = Icons.Default.History,
                contentDescription = "History",
                onClick = onHistory,
                isRestDay = isRestDay
            )

            TopActionIcon(
                icon = Icons.Default.Settings,
                contentDescription = "Settings",
                onClick = onSettings,
                isRestDay = isRestDay
            )
        }
    }
}





@Composable
private fun TopActionIcon(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    isRestDay : Boolean
) {
    val bgColor = MaterialTheme.colorScheme.surface


    val innerColor = MaterialTheme.colorScheme.surfaceVariant


    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)


    Surface(
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        color = bgColor,
        tonalElevation = 2.dp,
        shadowElevation = 8.dp,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp)
                .border(
                    width = 1.5.dp,
                    color = borderColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                innerColor,
                                innerColor.copy(alpha = 0.6f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                // 🔥 mini acento diagonal estilo "marca"
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            rotationZ = -20f
                        }
                        .background(if (isRestDay) RestDayBorderStart.copy(alpha = 0.3f) else
                             MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        )
                )

                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun TodayWorkoutCard(
    day: DayWorkout,
    currentDayOfWeek: Int,
    onOpenWorkout: (Int, String) -> Unit
) {
    if (day.isRestDay || day.workouts.isEmpty()) {
        RestDayCard()
    } else {
        ActiveWorkoutCard(
            day = day,
            currentDayOfWeek = currentDayOfWeek,
            onOpenWorkout = onOpenWorkout
        )
    }
}

@Composable
private fun ActiveWorkoutCard(
    day: DayWorkout,
    currentDayOfWeek: Int,
    onOpenWorkout: (Int, String) -> Unit
) {
    var showWorkoutPicker by remember { mutableStateOf(false) }

    val workoutCount = day.workouts.size
    val primaryWorkout = day.workouts.firstOrNull()

    val headline = when {
        workoutCount <= 1 -> primaryWorkout?.name?.ifBlank { "Workout" } ?: "Workout"
        else -> "$workoutCount workouts planned"
    }

    val supportingText = when {
        workoutCount <= 1 -> "Today's workout"
        else -> day.workouts.joinToString(" • ") { it.name.ifBlank { "Workout" } }
    }

    val buttonText = if (workoutCount <= 1) "Start workout" else "Choose workout"

    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.primary
        )
    )
    val onGradient = MaterialTheme.colorScheme.onPrimary

    if (showWorkoutPicker) {
        WorkoutPickerDialog(
            workouts = day.workouts,
            onDismiss = { },
            onSelect = { workout ->
                onOpenWorkout(currentDayOfWeek, workout.id)
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 0.dp,
        shadowElevation = 10.dp,
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .background(gradient)
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = onGradient
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = if (workoutCount <= 1) "Today's workout" else "Today's workouts",
                        style = MaterialTheme.typography.titleSmall,
                        color = onGradient.copy(alpha = 0.92f)
                    )

                    Text(
                        text = headline,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = onGradient
                    )

                    if (workoutCount > 1) {
                        Text(
                            text = supportingText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = onGradient.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            GradientBorderCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = {
                        if (workoutCount == 1 && primaryWorkout != null) {
                            onOpenWorkout(currentDayOfWeek, primaryWorkout.id)
                        }
                    },
                    shape = RoundedCornerShape(21.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutPickerDialog(
    workouts: List<WorkoutBlock>,
    onDismiss: () -> Unit,
    onSelect: (WorkoutBlock) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Choose workout")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                workouts.forEach { workout ->
                    FilledTonalButton(
                        onClick = { onSelect(workout) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = workout.name.ifBlank { "Workout" },
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun RestDayCard() {
    val recoveryBackground = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f),
            MaterialTheme.colorScheme.surface
        )
    )

    GradientBorderCard(
        modifier = Modifier.fillMaxWidth(),
        isRestDay = true
    ) {
        Column(
            modifier = Modifier
                .background(recoveryBackground)
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            RestDayBorderEnd
                        )
                        .border(
                            width = 2.dp,
                            color = RestDayBorderStart.copy(alpha = 0.6f),
                            shape = CircleShape
                ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        modifier = Modifier.size(22.dp),
                        contentDescription = null,
                        tint = RestDayBorderStart
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Recovery day",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Reset and recharge",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Text(
                text = "Recovery also builds progress.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickStatsRow(day: DayWorkout) {
    val exercises = if (day.isRestDay) 0 else day.totalExercises()
    val sets = if (day.isRestDay) 0 else day.totalSets()

    val duration = if (day.isRestDay) {
        "Rest day"
    } else {
        day.totalWorkoutSeconds().toReadableDuration()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = exercises.toString(),
                label = "Exercises",
                isRestDay = day.isRestDay
            )

            StatCard(
                modifier = Modifier.weight(1f),
                value = sets.toString(),
                label = "Sets",
                isRestDay = day.isRestDay
            )
        }

        StatCard(
            modifier = Modifier.fillMaxWidth(),
            value = duration,
            label = "Workout duration",
            isRestDay = day.isRestDay
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    isRestDay : Boolean
) {
    val color = if (isRestDay) RestDayBorderStart else MaterialTheme.colorScheme.secondary
    GradientBorderCard(
        modifier = modifier,
        isRestDay = isRestDay

    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 22.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
                )
        }
    }
}

@Composable
fun PlannerButton(
    onWeekPlanner: () -> Unit,
    isRestDay: Boolean
) {
    val color1 = if (isRestDay) RestDayBorderStart else MaterialTheme.colorScheme.primary
    val color2 = if (isRestDay) RestDayBorderEnd else MaterialTheme.colorScheme.tertiary

    GradientBorderCard(
        modifier = Modifier.fillMaxWidth(),
        isRestDay = isRestDay
    ) {
        FilledTonalButton(
            onClick = onWeekPlanner,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(21.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        val brush = Brush.linearGradient(
                            colors = listOf(color2, color1)
                        )

                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                brush = brush,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    },
                tint = Color.White
            )

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = "Open weekly planner",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}