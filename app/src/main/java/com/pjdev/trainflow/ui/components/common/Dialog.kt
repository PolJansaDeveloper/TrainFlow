package com.pjdev.trainflow.ui.components.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.pjdev.trainflow.domain.model.DayWorkout

@Composable
fun StartWorkoutDialog(
    day: DayWorkout,
    onDismiss: () -> Unit,
    onStart: () -> Unit
) {
    val workout = day.workouts.firstOrNull()
    val workoutName = workout?.name ?: "Workout"
    val exerciseCount = workout?.exercises?.size ?: 0
    val totalSets = workout?.exercises?.sumOf { it.sets } ?: 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Ready to start?")
        },
        text = {
            Text(
                "$workoutName\n$exerciseCount exercises · $totalSets total sets"
            )
        },
        confirmButton = {
            Button(onClick = onStart) {
                Text("Start workout")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}