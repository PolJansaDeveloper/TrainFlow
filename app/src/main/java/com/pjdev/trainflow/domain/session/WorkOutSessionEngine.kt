package com.pjdev.trainflow.domain.session

import com.pjdev.trainflow.domain.model.DayWorkout

class WorkoutSessionEngine {

    fun start(day: DayWorkout, nowMillis: Long): WorkoutRuntimeSnapshot {
        require(day.workouts.isNotEmpty()) { "Workout day must contain at least one workout" }

        val workout = day.workouts.first()
        require(workout.exercises.isNotEmpty()) { "Workout must contain at least one exercise" }

        val firstExercise = workout.exercises.first()

        return WorkoutRuntimeSnapshot(
            day = day,
            phase = WorkoutPhase.WORK,
            currentExerciseIndex = 0,
            currentSetIndex = 0,
            phaseStartedAtMillis = nowMillis,
            phaseDurationSeconds = firstExercise.workSeconds.coerceAtLeast(1),
            workoutStartedAtMillis = nowMillis
        )
    }

    fun toUiState(
        snapshot: WorkoutRuntimeSnapshot,
        nowMillis: Long
    ): ActiveWorkoutState {
        val workout = snapshot.day.workouts.first()
        val exercises = workout.exercises
        val exercise = exercises[snapshot.currentExerciseIndex]

        val elapsedPhaseSeconds =
            ((nowMillis - snapshot.phaseStartedAtMillis) / 1000L).toInt().coerceAtLeast(0)

        val phaseRemainingSeconds =
            (snapshot.phaseDurationSeconds - elapsedPhaseSeconds).coerceAtLeast(0)

        val totalElapsedSeconds =
            ((nowMillis - snapshot.workoutStartedAtMillis) / 1000L).toInt().coerceAtLeast(0)

        val isLastSetOfExercise = snapshot.currentSetIndex >= exercise.sets - 1
        val nextExerciseName = when {
            snapshot.phase == WorkoutPhase.COMPLETED -> null
            !isLastSetOfExercise -> exercise.name
            else -> exercises.getOrNull(snapshot.currentExerciseIndex + 1)?.name
        }

        return ActiveWorkoutState(
            workoutName = workout.name,
            phase = snapshot.phase,
            currentExerciseIndex = snapshot.currentExerciseIndex,
            currentSetIndex = snapshot.currentSetIndex,
            totalExercises = exercises.size,
            totalWorkoutElapsedSeconds = totalElapsedSeconds,
            phaseDurationSeconds = snapshot.phaseDurationSeconds,
            phaseRemainingSeconds = phaseRemainingSeconds,
            isRunning = snapshot.phase != WorkoutPhase.COMPLETED,
            isCompleted = snapshot.phase == WorkoutPhase.COMPLETED,
            currentExerciseName = exercise.name,
            currentExerciseSets = exercise.sets,
            currentExerciseRepsTarget = exercise.repsTarget,
            currentExerciseWorkSeconds = exercise.workSeconds,
            currentExerciseRestSeconds = exercise.restSeconds,
            nextExerciseName = nextExerciseName
        )
    }

    fun shouldAdvance(snapshot: WorkoutRuntimeSnapshot, nowMillis: Long): Boolean {
        val elapsedPhaseSeconds =
            ((nowMillis - snapshot.phaseStartedAtMillis) / 1000L).toInt().coerceAtLeast(0)

        return elapsedPhaseSeconds >= snapshot.phaseDurationSeconds
    }

    fun advance(snapshot: WorkoutRuntimeSnapshot, nowMillis: Long): WorkoutRuntimeSnapshot {
        val workout = snapshot.day.workouts.first()
        val exercises = workout.exercises
        val currentExercise = exercises[snapshot.currentExerciseIndex]
        val isLastSetOfExercise = snapshot.currentSetIndex >= currentExercise.sets - 1
        val isLastExercise = snapshot.currentExerciseIndex >= exercises.lastIndex

        return when (snapshot.phase) {
            WorkoutPhase.WORK -> {
                when {
                    !isLastSetOfExercise -> {
                        WorkoutRuntimeSnapshot(
                            day = snapshot.day,
                            phase = WorkoutPhase.REST,
                            currentExerciseIndex = snapshot.currentExerciseIndex,
                            currentSetIndex = snapshot.currentSetIndex,
                            phaseStartedAtMillis = nowMillis,
                            phaseDurationSeconds = currentExercise.restSeconds.coerceAtLeast(1),
                            workoutStartedAtMillis = snapshot.workoutStartedAtMillis
                        )
                    }

                    isLastExercise -> {
                        snapshot.copy(
                            phase = WorkoutPhase.COMPLETED,
                            phaseStartedAtMillis = nowMillis,
                            phaseDurationSeconds = 0
                        )
                    }

                    else -> {
                        val nextExerciseIndex = snapshot.currentExerciseIndex + 1
                        val nextExercise = exercises[nextExerciseIndex]

                        WorkoutRuntimeSnapshot(
                            day = snapshot.day,
                            phase = WorkoutPhase.WORK,
                            currentExerciseIndex = nextExerciseIndex,
                            currentSetIndex = 0,
                            phaseStartedAtMillis = nowMillis,
                            phaseDurationSeconds = nextExercise.workSeconds.coerceAtLeast(1),
                            workoutStartedAtMillis = snapshot.workoutStartedAtMillis
                        )
                    }
                }
            }

            WorkoutPhase.REST -> {
                WorkoutRuntimeSnapshot(
                    day = snapshot.day,
                    phase = WorkoutPhase.WORK,
                    currentExerciseIndex = snapshot.currentExerciseIndex,
                    currentSetIndex = snapshot.currentSetIndex + 1,
                    phaseStartedAtMillis = nowMillis,
                    phaseDurationSeconds = currentExercise.workSeconds.coerceAtLeast(1),
                    workoutStartedAtMillis = snapshot.workoutStartedAtMillis
                )
            }

            WorkoutPhase.READY,
            WorkoutPhase.COMPLETED -> snapshot
        }
    }
}