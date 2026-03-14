package com.pjdev.trainflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pjdev.trainflow.domain.model.Exercise
import com.pjdev.trainflow.domain.model.TrackingType
import com.pjdev.trainflow.ui.components.common.HomeBackground
import com.pjdev.trainflow.ui.components.common.ScreenHeader

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExerciseEditorScreen(
    exercise: Exercise?,
    onSave: (String, Int, String, Int, Int, TrackingType) -> Unit,
    onBack: () -> Unit
) {

    var name by remember(exercise) { mutableStateOf(exercise?.name ?: "") }
    var sets by remember(exercise) { mutableStateOf((exercise?.sets ?: 3).toString()) }
    var repsTarget by remember(exercise) { mutableStateOf(exercise?.repsTarget ?: "10") }

    var work by remember(exercise) { mutableStateOf((exercise?.workSeconds ?: 40).toString()) }
    var rest by remember(exercise) { mutableStateOf((exercise?.restSeconds ?: 60).toString()) }

    var tracking by remember(exercise) { mutableStateOf(exercise?.trackingType ?: TrackingType.RepsOnly) }

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
                        title = if (exercise == null) "New exercise" else "Edit exercise",
                        subtitle = "Define how this movement will be performed and tracked.",
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
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                label = { Text("Name") },
                                placeholder = { Text("Push Ups") },
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                OutlinedTextField(
                                    value = sets,
                                    onValueChange = { sets = it.filter(Char::isDigit) },
                                    modifier = Modifier.weight(1f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                    label = { Text("Sets") },
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = work,
                                    onValueChange = { work = it.filter(Char::isDigit) },
                                    modifier = Modifier.weight(1f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                    label = { Text("Work (sec)") },
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = rest,
                                    onValueChange = { rest = it.filter(Char::isDigit) },
                                    modifier = Modifier.weight(1f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                    label = { Text("Rest (sec)") },
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = repsTarget,
                                onValueChange = { repsTarget = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                label = { Text("Target") },
                                placeholder = { Text("10 reps / 30 sec / note...") },
                                singleLine = true
                            )
                        }
                    }
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
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Text(
                                "Tracking type",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                "Choose how results will be recorded after the workout.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                TrackingType.entries.forEach { type ->

                                    FilterChip(
                                        selected = tracking == type,
                                        onClick = { tracking = type },
                                        label = { Text(type.key) }
                                    )
                                }
                            }
                        }
                    }
                }

                item {

                    Button(
                        onClick = {

                            onSave(
                                name,
                                sets.toIntOrNull() ?: 1,
                                repsTarget,
                                work.toIntOrNull() ?: 40,
                                rest.toIntOrNull() ?: 60,
                                tracking
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
                    ) {

                        Text(
                            "Save exercise",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}