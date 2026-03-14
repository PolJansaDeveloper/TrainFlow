package com.pjdev.trainflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.domain.model.ExerciseResult
import com.pjdev.trainflow.ui.components.common.GradientInfoCard
import com.pjdev.trainflow.ui.components.common.HomeBackground
import com.pjdev.trainflow.ui.components.common.ScreenHeader

@Composable
fun WorkoutPreviewScreen(
    day: DayWorkout,
    latestResults: Map<String, ExerciseResult>,
    onSummary: () -> Unit,
    onBack: () -> Unit
) {
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
                        title = day.workoutName.ifBlank { "Workout Preview" },
                        subtitle = "Review today’s session flow before saving the results.",
                        onBack = onBack
                    )
                }

                item {
                    GradientInfoCard(
                        title = "Session flow preview",
                        subtitle = "${day.exercises.size} exercises ready",
                        value = "${day.exercises.sumOf { it.sets }} total sets"
                    )
                }

                items(day.exercises, key = { it.id }) { ex ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                ex.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                "${ex.sets} sets • target ${ex.repsTarget} • rest ${ex.restSeconds}s",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            latestResults[ex.name]?.let { last ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = buildString {
                                            append("Last result: ")
                                            append(last.valuePrimary ?: "-")
                                            if (!last.valueSecondary.isNullOrBlank()) append(" · ${last.valueSecondary}")
                                            if (!last.note.isNullOrBlank()) append(" · ${last.note}")
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = onSummary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Finish session", style = MaterialTheme.typography.titleMedium)
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}