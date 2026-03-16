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
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Ready to start?")
        },
        text = {
            Text(
                "${day.workoutName}\n${day.exercises.size} exercises · ${day.exercises.sumOf { it.sets }} total sets"
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