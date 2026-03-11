package com.pjdev.trainflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.domain.model.Exercise
import com.pjdev.trainflow.domain.model.ExerciseResult
import com.pjdev.trainflow.domain.model.Settings
import com.pjdev.trainflow.domain.model.TrackingType
import com.pjdev.trainflow.domain.model.WorkoutSession

private val dayLabels = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

@Composable
fun HomeScreen(day: DayWorkout, currentDayOfWeek: Int, onWeekPlanner: () -> Unit, onOpenWorkout: (Int) -> Unit, onHistory: () -> Unit, onSettings: () -> Unit) {
    Scaffold { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("TrainFlow", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Today: ${dayLabels[currentDayOfWeek - 1]}")
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (day.isRestDay) {
                        Text("Rest day", style = MaterialTheme.typography.titleLarge)
                        Text("Choose another workout from your week plan.")
                    } else {
                        Text(day.workoutName.ifBlank { "Workout" }, style = MaterialTheme.typography.titleLarge)
                        Text("${day.exercises.size} exercises")
                        Button(onClick = { onOpenWorkout(currentDayOfWeek) }) { Text("Open workout") }
                    }
                }
            }
            Button(onClick = onWeekPlanner) { Text("Open weekly planner") }
            TextButton(onClick = onHistory) { Text("History") }
            TextButton(onClick = onSettings) { Text("Settings") }
        }
    }
}

@Composable
fun WeekPlannerScreen(days: List<DayWorkout>, onEdit: (Int) -> Unit, onOpenWorkout: (Int) -> Unit, onBack: () -> Unit) {
    Scaffold { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text("Week Planner", style = MaterialTheme.typography.headlineSmall) }
            items(days) { day ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(dayLabels[day.dayOfWeek - 1], fontWeight = FontWeight.Bold)
                            Text(if (day.isRestDay) "Rest day" else day.workoutName)
                        }
                        Row {
                            if (!day.isRestDay) TextButton(onClick = { onOpenWorkout(day.dayOfWeek) }) { Text("Open") }
                            TextButton(onClick = { onEdit(day.dayOfWeek) }) { Text("Edit") }
                        }
                    }
                }
            }
            item { TextButton(onClick = onBack) { Text("Back") } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDayWorkoutScreen(day: DayWorkout, onSave: (DayWorkout) -> Unit, onEditExercise: (String?) -> Unit, onDeleteExercise: (String) -> Unit, onBack: () -> Unit) {
    var isRest by remember(day) { mutableStateOf(day.isRestDay) }
    var name by remember(day) { mutableStateOf(day.workoutName) }
    Scaffold { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Edit ${dayLabels[day.dayOfWeek - 1]}", style = MaterialTheme.typography.headlineSmall)
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Switch(checked = isRest, onCheckedChange = { isRest = it })
                Spacer(Modifier.width(8.dp))
                Text("Rest day")
            }
            OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), enabled = !isRest, label = { Text("Workout name") })
            Text("Exercises", fontWeight = FontWeight.SemiBold)
            day.exercises.forEach { ex ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${ex.name} • ${ex.sets}x${ex.repsTarget}")
                    Row {
                        TextButton(onClick = { onEditExercise(ex.id) }) { Text("Edit") }
                        TextButton(onClick = { onDeleteExercise(ex.id) }) { Text("Delete") }
                    }
                }
            }
            Button(onClick = { onEditExercise(null) }, enabled = !isRest) { Text("Add exercise") }
            Button(onClick = { onSave(day.copy(isRestDay = isRest, workoutName = if (isRest) "" else name)) }) { Text("Save day") }
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}

@Composable
fun ExerciseEditorScreen(exercise: Exercise?, onSave: (String, Int, String, Int, TrackingType) -> Unit, onBack: () -> Unit) {
    var name by remember(exercise) { mutableStateOf(exercise?.name ?: "") }
    var sets by remember(exercise) { mutableStateOf((exercise?.sets ?: 3).toString()) }
    var repsTarget by remember(exercise) { mutableStateOf(exercise?.repsTarget ?: "10") }
    var rest by remember(exercise) { mutableStateOf((exercise?.restSeconds ?: 60).toString()) }
    var tracking by remember(exercise) { mutableStateOf(exercise?.trackingType ?: TrackingType.RepsOnly) }
    Scaffold { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Exercise Editor", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(name, { name = it }, label = { Text("Name") })
            OutlinedTextField(sets, { sets = it.filter(Char::isDigit) }, label = { Text("Sets") })
            OutlinedTextField(repsTarget, { repsTarget = it }, label = { Text("Reps target") })
            OutlinedTextField(rest, { rest = it.filter(Char::isDigit) }, label = { Text("Rest seconds") })
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TrackingType.entries.forEach { type ->
                    FilterChip(selected = tracking == type, onClick = { tracking = type }, label = { Text(type.key) })
                }
            }
            Button(onClick = { onSave(name, sets.toIntOrNull() ?: 1, repsTarget, rest.toIntOrNull() ?: 0, tracking) }) { Text("Save exercise") }
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}

@Composable
fun WorkoutPreviewScreen(day: DayWorkout, latestResults: Map<String, ExerciseResult>, onSummary: () -> Unit, onBack: () -> Unit) {
    Scaffold { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(day.workoutName, style = MaterialTheme.typography.headlineSmall)
            Text("Session flow preview")
            day.exercises.forEach { ex ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(ex.name, fontWeight = FontWeight.Bold)
                        Text("${ex.sets} sets • target ${ex.repsTarget} • rest ${ex.restSeconds}s")
                        latestResults[ex.name]?.let { last -> Text("Last: ${last.valuePrimary ?: "-"} ${last.valueSecondary ?: ""} ${last.note ?: ""}") }
                    }
                }
            }
            Button(onClick = onSummary) { Text("Finish session") }
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}

@Composable
fun WorkoutSummaryScreen(day: DayWorkout, onSave: (List<ExerciseResult>) -> Unit, onBack: () -> Unit) {
    val valueMap = remember { mutableStateMapOf<String, String>() }
    val secondaryMap = remember { mutableStateMapOf<String, String>() }
    val notes = remember { mutableStateMapOf<String, String>() }
    Scaffold { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Text("Workout Summary", style = MaterialTheme.typography.headlineSmall) }
            items(day.exercises) { ex ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(ex.name, fontWeight = FontWeight.Bold)
                        when (ex.trackingType) {
                            TrackingType.WeightReps -> {
                                OutlinedTextField(valueMap[ex.id] ?: "", { valueMap[ex.id] = it }, label = { Text("Weight") })
                                OutlinedTextField(secondaryMap[ex.id] ?: "", { secondaryMap[ex.id] = it }, label = { Text("Reps") })
                            }
                            TrackingType.RepsOnly, TrackingType.TimeOnly -> OutlinedTextField(valueMap[ex.id] ?: "", { valueMap[ex.id] = it }, label = { Text("Value") })
                            TrackingType.NoteOnly -> OutlinedTextField(notes[ex.id] ?: "", { notes[ex.id] = it }, label = { Text("Note") })
                        }
                        if (ex.trackingType != TrackingType.NoteOnly) {
                            OutlinedTextField(notes[ex.id] ?: "", { notes[ex.id] = it }, label = { Text("Optional note") })
                        }
                    }
                }
            }
            item {
                Button(onClick = {
                    onSave(day.exercises.map { ex ->
                        ExerciseResult(
                            exerciseName = ex.name,
                            trackingType = ex.trackingType,
                            valuePrimary = valueMap[ex.id],
                            valueSecondary = secondaryMap[ex.id],
                            note = notes[ex.id]
                        )
                    })
                }) { Text("Save workout") }
                TextButton(onClick = onBack) { Text("Back") }
            }
        }
    }
}

@Composable
fun HistoryScreen(sessions: List<WorkoutSession>, onBack: () -> Unit) {
    Scaffold { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Text("History", style = MaterialTheme.typography.headlineSmall) }
            items(sessions) { session ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(session.workoutName, fontWeight = FontWeight.Bold)
                        Text("${session.results.size} results")
                        session.results.forEach { Text("• ${it.exerciseName}: ${it.valuePrimary ?: it.note ?: "-"}") }
                    }
                }
            }
            item { TextButton(onClick = onBack) { Text("Back") } }
        }
    }
}

@Composable
fun SettingsScreen(settings: Settings, onSoundChange: (Boolean) -> Unit, onVibrationChange: (Boolean) -> Unit, onBack: () -> Unit) {
    Scaffold { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Settings", style = MaterialTheme.typography.headlineSmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Sound enabled")
                Switch(checked = settings.soundEnabled, onCheckedChange = onSoundChange)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Vibration enabled")
                Switch(checked = settings.vibrationEnabled, onCheckedChange = onVibrationChange)
            }
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}
