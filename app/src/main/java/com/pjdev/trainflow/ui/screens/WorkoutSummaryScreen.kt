package com.pjdev.trainflow.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.domain.model.ExerciseResult
import com.pjdev.trainflow.domain.model.TrackingType
import com.pjdev.trainflow.ui.components.common.GradientInfoCard
import com.pjdev.trainflow.ui.components.common.HomeBackground
import com.pjdev.trainflow.ui.components.common.ScreenHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSummaryScreen(
    day: DayWorkout,
    onSave: (List<ExerciseResult>) -> Unit,
    onBack: () -> Unit
) {
    val workout = day.workouts.firstOrNull()
    val exercises = workout?.exercises.orEmpty()

    val valueMap = remember { mutableStateMapOf<String, String>() }
    val secondaryMap = remember { mutableStateMapOf<String, String>() }
    val notes = remember { mutableStateMapOf<String, String>() }

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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    ScreenHeader(
                        title = "Workout Summary",
                        subtitle = "Record your results and keep progress moving.",
                        onBack = onBack
                    )
                }

                item {
                    GradientInfoCard(
                        title = workout?.name?.ifBlank { "Workout" } ?: "Workout",
                        subtitle = "${exercises.size} exercises completed",
                        value = "Save your session"
                    )
                }

                items(exercises, key = { it.id }) { ex ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = ex.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Tracking: ${trackingTypeLabel(ex.trackingType)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            when (ex.trackingType) {
                                TrackingType.WeightReps -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = valueMap[ex.id] ?: "",
                                            onValueChange = { valueMap[ex.id] = it },
                                            modifier = Modifier.weight(1f),
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                            label = { Text("Weight") },
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = secondaryMap[ex.id] ?: "",
                                            onValueChange = { secondaryMap[ex.id] = it },
                                            modifier = Modifier.weight(1f),
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                            label = { Text("Reps") },
                                            singleLine = true
                                        )
                                    }
                                }

                                TrackingType.RepsOnly -> {
                                    OutlinedTextField(
                                        value = valueMap[ex.id] ?: "",
                                        onValueChange = { valueMap[ex.id] = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                        label = { Text("Reps") },
                                        singleLine = true
                                    )
                                }

                                TrackingType.TimeOnly -> {
                                    OutlinedTextField(
                                        value = valueMap[ex.id] ?: "",
                                        onValueChange = { valueMap[ex.id] = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                        label = { Text("Time") },
                                        placeholder = { Text("e.g. 45 sec") },
                                        singleLine = true
                                    )
                                }

                                TrackingType.NoteOnly -> {
                                    OutlinedTextField(
                                        value = notes[ex.id] ?: "",
                                        onValueChange = { notes[ex.id] = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                        label = { Text("Note") }
                                    )
                                }
                            }

                            if (ex.trackingType != TrackingType.NoteOnly) {
                                OutlinedTextField(
                                    value = notes[ex.id] ?: "",
                                    onValueChange = { notes[ex.id] = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                    label = { Text("Optional note") }
                                )
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            onSave(
                                exercises.map { ex ->
                                    ExerciseResult(
                                        exerciseName = ex.name,
                                        trackingType = ex.trackingType,
                                        valuePrimary = valueMap[ex.id],
                                        valueSecondary = secondaryMap[ex.id],
                                        note = notes[ex.id]
                                    )
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "Save workout",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

private fun trackingTypeLabel(type: TrackingType): String {
    return when (type) {
        TrackingType.WeightReps -> "Weight + reps"
        TrackingType.RepsOnly -> "Reps only"
        TrackingType.TimeOnly -> "Time only"
        TrackingType.NoteOnly -> "Notes only"
    }
}