package com.pjdev.trainflow.domain.session

import com.pjdev.trainflow.domain.model.DayWorkout

data class WorkoutRuntimeSnapshot(
    val day: DayWorkout,
    val phase: WorkoutPhase,
    val currentExerciseIndex: Int,
    val currentSetIndex: Int,
    val phaseStartedAtMillis: Long,
    val phaseDurationSeconds: Int,
    val workoutStartedAtMillis: Long
)