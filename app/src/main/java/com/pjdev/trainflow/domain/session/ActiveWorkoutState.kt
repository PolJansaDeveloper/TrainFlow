package com.pjdev.trainflow.domain.session

data class ActiveWorkoutState(
    val workoutName: String = "",
    val phase: WorkoutPhase = WorkoutPhase.READY,

    val currentExerciseIndex: Int = 0,
    val currentSetIndex: Int = 0,

    val totalExercises: Int = 0,
    val totalWorkoutElapsedSeconds: Int = 0,

    val phaseDurationSeconds: Int = 0,
    val phaseRemainingSeconds: Int = 0,

    val isRunning: Boolean = false,
    val isCompleted: Boolean = false,

    val currentExerciseName: String = "",
    val currentExerciseSets: Int = 0,
    val currentExerciseRepsTarget: String = "",
    val currentExerciseWorkSeconds: Int = 0,
    val currentExerciseRestSeconds: Int = 0,

    val nextExerciseName: String? = null
)