package com.pjdev.trainflow.ui.screens

import GradientBorderCard
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.domain.model.Exercise
import com.pjdev.trainflow.domain.model.WorkoutBlock
import com.pjdev.trainflow.domain.model.secondsToHoursMinutes
import com.pjdev.trainflow.ui.components.common.EmptyStateCard
import com.pjdev.trainflow.ui.components.common.ExerciseListCard
import com.pjdev.trainflow.ui.components.common.HomeBackground
import com.pjdev.trainflow.ui.components.common.dayLabels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDayWorkoutScreen(
    day: DayWorkout,
    onSave: (DayWorkout) -> Unit,
    onAddWorkout: () -> Unit,
    onUpdateWorkoutName: (String, String) -> Unit,
    onDeleteWorkout: (String) -> Unit,
    onEditExercise: (String, String?) -> Unit,
    onDeleteExercise: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var isRest by remember(day) { mutableStateOf(day.isRestDay) }

    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(day.workouts) {
        day.workouts.forEach { workout ->
            if (expandedStates[workout.id] == null) {
                expandedStates[workout.id] = true
            }
        }
        val validIds = day.workouts.map { it.id }.toSet()
        val iterator = expandedStates.keys.iterator()
        val toRemove = mutableListOf<String>()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if (key !in validIds) toRemove += key
        }
        toRemove.forEach { expandedStates.remove(it) }
    }

    val visibleWorkouts = if (isRest) emptyList() else day.workouts
    val totalExercises = visibleWorkouts.sumOf { it.exercises.size }
    val totalDuration = visibleWorkouts.sumOf { it.totalWorkoutSeconds() }.secondsToHoursMinutes()

    Box(modifier = Modifier.fillMaxSize()) {
        HomeBackground()

        Scaffold(
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    EditDayHeader(
                        title = "Edit ${dayLabels[day.dayOfWeek - 1]}",
                        subtitle = "Build one or more workouts for this day.",
                        onBack = onBack
                    )
                }

                item {
                    DaySetupCard(
                        isRest = isRest,
                        workoutCount = visibleWorkouts.size,
                        exerciseCount = totalExercises,
                        totalDuration = totalDuration,
                        onRestChanged = { isRest = it },
                        onAddWorkout = onAddWorkout,
                        addEnabled = !isRest
                    )
                }

                if (isRest) {
                    item {
                        RestModeCard()
                    }
                } else if (day.workouts.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "No workouts yet",
                            subtitle = "Add your first workout block to start planning this day."
                        )
                    }
                } else {
                    items(day.workouts, key = { it.id }) { workout ->
                        WorkoutBlockEditorCard(
                            workout = workout,
                            expanded = expandedStates[workout.id] ?: true,
                            onToggleExpand = {
                                expandedStates[workout.id] = !(expandedStates[workout.id] ?: true)
                            },
                            onUpdateWorkoutName = { newName ->
                                onUpdateWorkoutName(workout.id, newName)
                            },
                            onDeleteWorkout = { onDeleteWorkout(workout.id) },
                            onAddExercise = { onEditExercise(workout.id, null) },
                            onEditExercise = { exerciseId -> onEditExercise(workout.id, exerciseId) },
                            onDeleteExercise = { exerciseId -> onDeleteExercise(workout.id, exerciseId) }
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            onSave(
                                day.copy(
                                    isRestDay = isRest,
                                    workouts = if (isRest) emptyList() else day.workouts
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Save day", style = MaterialTheme.typography.titleMedium)
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun EditDayHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = onBack,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
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
                .padding(start = 14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DaySetupCard(
    isRest: Boolean,
    workoutCount: Int,
    exerciseCount: Int,
    totalDuration: String,
    onRestChanged: (Boolean) -> Unit,
    onAddWorkout: () -> Unit,
    addEnabled: Boolean
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isRest) {
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f)
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        }
                    ) {
                        Box(
                            modifier = Modifier.size(42.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isRest) Icons.Default.Hotel else Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = if (isRest) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Rest day",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Turn this on if you want a recovery day without workouts.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = isRest,
                    onCheckedChange = onRestChanged
                )
            }

            if (!isRest) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryPill(
                        modifier = Modifier.weight(1f),
                        value = workoutCount.toString(),
                        label = "Workouts"
                    )
                    SummaryPill(
                        modifier = Modifier.weight(1f),
                        value = exerciseCount.toString(),
                        label = "Exercises"
                    )
                    SummaryPill(
                        modifier = Modifier.weight(1f),
                        value = totalDuration,
                        label = "Planned"
                    )
                }

                FilledTonalButton(
                    onClick = onAddWorkout,
                    enabled = addEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add workout")
                }
            }
        }
    }
}

@Composable
private fun SummaryPill(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f))
            .padding(vertical = 12.dp, horizontal = 10.dp),
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

@Composable
private fun RestModeCard() {
    val recoveryBackground = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f),
            MaterialTheme.colorScheme.surface
        )
    )

    GradientBorderCard(
        modifier = Modifier.fillMaxWidth()
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
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f)
                ) {
                    Box(
                        modifier = Modifier.size(46.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hotel,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Recovery mode enabled",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "No workouts planned for this day",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Text(
                text = "Use this day to recover, stretch or stay light before your next session.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutBlockEditorCard(
    workout: WorkoutBlock,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onUpdateWorkoutName: (String) -> Unit,
    onDeleteWorkout: () -> Unit,
    onAddExercise: () -> Unit,
    onEditExercise: (String) -> Unit,
    onDeleteExercise: (String) -> Unit
) {
    var localName by rememberSaveable(workout.id) { mutableStateOf(workout.name) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Box(
                        modifier = Modifier.size(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = localName.ifBlank { "Workout" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${workout.exercises.size} exercises • ${workout.totalWorkoutSeconds().secondsToHoursMinutes()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }

            if (expanded) {
                OutlinedTextField(
                    value = localName,
                    onValueChange = {
                        localName = it
                        onUpdateWorkoutName(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    label = { Text("Workout name") },
                    placeholder = { Text("Upper Body Strength") },
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryPill(
                        modifier = Modifier.weight(1f),
                        value = workout.exercises.size.toString(),
                        label = "Exercises"
                    )
                    SummaryPill(
                        modifier = Modifier.weight(1f),
                        value = workout.totalSets().toString(),
                        label = "Sets"
                    )
                    SummaryPill(
                        modifier = Modifier.weight(1f),
                        value = workout.totalWorkoutSeconds().secondsToHoursMinutes(),
                        label = "Time"
                    )
                }

                FilledTonalButton(
                    onClick = onAddExercise,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add exercise")
                }

                if (workout.exercises.isEmpty()) {
                    EmptyStateCard(
                        title = "No exercises yet",
                        subtitle = "Add your first exercise to start building this workout."
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        workout.exercises.forEach { exercise ->
                            ExerciseListCard(
                                exercise = exercise,
                                onEdit = { onEditExercise(exercise.id) },
                                onDelete = { onDeleteExercise(exercise.id) },
                                enabled = true
                            )
                        }
                    }
                }

                Button(
                    onClick = onDeleteWorkout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete workout")
                }
            }
        }
    }
}