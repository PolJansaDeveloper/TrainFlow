package com.pjdev.trainflow.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.domain.session.ActiveWorkoutState
import com.pjdev.trainflow.domain.session.WorkoutPhase
import com.pjdev.trainflow.ui.components.common.HomeBackground
import com.pjdev.trainflow.ui.components.common.StartWorkoutDialog
import com.pjdev.trainflow.ui.components.common.formatPhaseTime
import com.pjdev.trainflow.ui.components.common.formatWorkoutElapsed

@Composable
fun WorkoutRunningScreen(
    day: DayWorkout,
    uiState: ActiveWorkoutState,
    onStartWorkout: () -> Unit,
    onFinishWorkout: () -> Unit,
    onBack: () -> Unit
) {
    var showStartDialog by rememberSaveable { mutableStateOf(true) }

    BackHandler(enabled = !showStartDialog) {
        onBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeBackground()

        if (showStartDialog) {
            StartWorkoutDialog(
                day = day,
                onDismiss = onBack,
                onStart = {
                    showStartDialog = false
                    onStartWorkout()
                }
            )
        }

        Scaffold(
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WorkoutSessionTopBar(
                    title = day.workoutName.ifBlank { "Workout" },
                    elapsed = formatWorkoutElapsed(uiState.totalWorkoutElapsedSeconds),
                    onBack = onBack
                )

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    WorkoutTimerHero(
                        uiState = uiState
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                WorkoutSessionFooter(
                    uiState = uiState
                )

                if (uiState.phase == WorkoutPhase.COMPLETED) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onFinishWorkout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "Finish session",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutSessionTopBar(
    title: String,
    elapsed: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = onBack,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
            tonalElevation = 2.dp,
            shadowElevation = 6.dp
        ) {
            Box(
                modifier = Modifier.size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = "Elapsed $elapsed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.size(46.dp))
    }
}

@Composable
private fun WorkoutTimerHero(
    uiState: ActiveWorkoutState
) {
    val color1 = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
    val color2 = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val progress = when {
        uiState.phase == WorkoutPhase.COMPLETED -> 1f
        uiState.phaseDurationSeconds <= 0 -> 0f
        else -> {
            val done = uiState.phaseDurationSeconds - uiState.phaseRemainingSeconds
            (done.toFloat() / uiState.phaseDurationSeconds.toFloat()).coerceIn(0f, 1f)
        }
    }

    val phaseBrush = when (uiState.phase) {
        WorkoutPhase.WORK -> Brush.sweepGradient(
            listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.primary
            )
        )
        WorkoutPhase.REST -> Brush.sweepGradient(
            listOf(
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.secondary
            )
        )
        WorkoutPhase.COMPLETED -> Brush.sweepGradient(
            listOf(
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.tertiary
            )
        )
        WorkoutPhase.READY -> Brush.sweepGradient(
            listOf(
                MaterialTheme.colorScheme.outline,
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.outline
            )
        )
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(300.dp)
        ) {
            val stroke = 22.dp.toPx()
            val inset = stroke / 2f
            val arcSize = size.width - stroke

            drawCircle(
                color = color1 ,
                radius = size.minDimension / 2f
            )

            drawArc(
                color = color2,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(arcSize, arcSize),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            drawArc(
                brush = phaseBrush,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(arcSize, arcSize),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(
            modifier = Modifier
                .size(235.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f)
                        )
                    )
                )
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PhasePill(uiState.phase)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (uiState.phase == WorkoutPhase.COMPLETED) {
                    formatWorkoutElapsed(uiState.totalWorkoutElapsedSeconds)
                } else {
                    formatPhaseTime(uiState.phaseRemainingSeconds)
                },
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = when (uiState.phase) {
                    WorkoutPhase.COMPLETED -> "Workout finished"
                    else -> uiState.currentExerciseName
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when (uiState.phase) {
                    WorkoutPhase.WORK ->
                        "Set ${uiState.currentSetIndex + 1} of ${uiState.currentExerciseSets}"
                    WorkoutPhase.REST ->
                        "Recover and get ready"
                    WorkoutPhase.COMPLETED ->
                        "Nice work"
                    WorkoutPhase.READY ->
                        "Ready to begin"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PhasePill(
    phase: WorkoutPhase
) {
    val (bg, fg, text) = when (phase) {
        WorkoutPhase.WORK -> Triple(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
            MaterialTheme.colorScheme.primary,
            "WORK"
        )
        WorkoutPhase.REST -> Triple(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f),
            MaterialTheme.colorScheme.secondary,
            "REST"
        )
        WorkoutPhase.COMPLETED -> Triple(
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f),
            MaterialTheme.colorScheme.tertiary,
            "DONE"
        )
        WorkoutPhase.READY -> Triple(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
            MaterialTheme.colorScheme.onSurfaceVariant,
            "READY"
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            color = fg
        )
    }
}

@Composable
private fun WorkoutSessionFooter(
    uiState: ActiveWorkoutState
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MinimalMetric(
                modifier = Modifier.weight(1f),
                value = "${uiState.currentExerciseIndex + 1}/${uiState.totalExercises}",
                label = "Exercise"
            )
            MinimalMetric(
                modifier = Modifier.weight(1f),
                value = "${uiState.currentSetIndex + 1}/${uiState.currentExerciseSets}",
                label = "Set"
            )
            MinimalMetric(
                modifier = Modifier.weight(1f),
                value = uiState.currentExerciseRepsTarget,
                label = "Target"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MinimalMetric(
                modifier = Modifier.weight(1f),
                value = "${uiState.currentExerciseWorkSeconds}s",
                label = "Work"
            )
            MinimalMetric(
                modifier = Modifier.weight(1f),
                value = "${uiState.currentExerciseRestSeconds}s",
                label = "Rest"
            )
        }

        if (uiState.phase != WorkoutPhase.COMPLETED && uiState.nextExerciseName != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = uiState.nextExerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun MinimalMetric(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.68f)
            )
            .padding(vertical = 14.dp, horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}