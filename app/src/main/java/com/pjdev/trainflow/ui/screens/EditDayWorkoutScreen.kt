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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.ui.components.common.EmptyStateCard
import com.pjdev.trainflow.ui.components.common.ExerciseListCard
import com.pjdev.trainflow.ui.components.common.HomeBackground
import com.pjdev.trainflow.ui.components.common.ScreenHeader
import com.pjdev.trainflow.ui.components.common.dayLabels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDayWorkoutScreen(
    day: DayWorkout,
    onSave: (DayWorkout) -> Unit,
    onEditExercise: (String?) -> Unit,
    onDeleteExercise: (String) -> Unit,
    onBack: () -> Unit
) {
    var isRest by remember(day) { mutableStateOf(day.isRestDay) }
    var name by remember(day) { mutableStateOf(day.workoutName) }

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
                        title = "Edit ${dayLabels[day.dayOfWeek - 1]}",
                        subtitle = "Adjust your workout setup for this day.",
                        onBack = onBack
                    )
                }

                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
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
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        "Rest day",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        "Enable this if you want a recovery day without exercises.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Switch(
                                    checked = isRest,
                                    onCheckedChange = { isRest = it }
                                )
                            }

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isRest,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                label = { Text("Workout name") },
                                placeholder = { Text("Upper Body Strength") },
                                singleLine = true
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Exercises",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                if (isRest) "Disabled on rest day" else "${day.exercises.size} planned",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        FilledTonalButton(
                            onClick = { onEditExercise(null) },
                            enabled = !isRest,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                        ) {
                            Text("Add exercise")
                        }
                    }
                }

                if (day.exercises.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "No exercises yet",
                            subtitle = "Start building this workout by adding your first exercise."
                        )
                    }
                } else {
                    items(day.exercises, key = { it.id }) { ex ->
                        ExerciseListCard(
                            exercise = ex,
                            onEdit = { onEditExercise(ex.id) },
                            onDelete = { onDeleteExercise(ex.id) },
                            enabled = !isRest
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            onSave(
                                day.copy(
                                    isRestDay = isRest,
                                    workoutName = if (isRest) "" else name
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
                    ) {
                        Text("Save day", style = MaterialTheme.typography.titleMedium)
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}