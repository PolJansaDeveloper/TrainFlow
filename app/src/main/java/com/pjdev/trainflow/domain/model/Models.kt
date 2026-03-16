package com.pjdev.trainflow.domain.model

enum class TrackingType(val key: String) {
    WeightReps("weight_reps"),
    RepsOnly("reps_only"),
    TimeOnly("time_only"),
    NoteOnly("note_only");

    companion object {
        fun fromKey(key: String): TrackingType =
            entries.firstOrNull { it.key == key } ?: RepsOnly
    }
}

data class WeeklyPlan(
    val id: String = "default-plan",
    val name: String = "Weekly Plan",
    val days: List<DayWorkout> =
        (1..7).map { DayWorkout(dayOfWeek = it, isRestDay = true) }
)

data class DayWorkout(
    val dayOfWeek: Int,
    val isRestDay: Boolean,
    val workouts: List<WorkoutBlock> = emptyList()
) {
    fun totalWorkoutSeconds(): Int {
        if (isRestDay) return 0
        return workouts.sumOf { it.totalWorkoutSeconds() }
    }

    fun totalSets(): Int = workouts.sumOf { it.totalSets() }

    fun totalExercises(): Int = workouts.sumOf { it.exercises.size }

}

data class WorkoutBlock(
    val id: String,
    val name: String,
    val exercises: List<Exercise> = emptyList()
) {
    fun totalWorkoutSeconds(): Int {
        return exercises.mapIndexed { index, exercise ->
            val isLastExercise = index == exercises.lastIndex

            val totalWork = exercise.sets * exercise.workSeconds
            val restIntervals = if (isLastExercise) {
                (exercise.sets - 1).coerceAtLeast(0)
            } else {
                exercise.sets
            }
            val totalRest = restIntervals * exercise.restSeconds

            totalWork + totalRest
        }.sum()
    }

    fun totalSets(): Int = exercises.sumOf { it.sets }
}

data class Exercise(
    val id: String,
    val name: String,
    val sets: Int,
    val repsTarget: String,
    val workSeconds: Int,
    val restSeconds: Int,
    val trackingType: TrackingType
)

fun Int.secondsToHoursMinutes(): String {
    val totalSeconds = this.coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> if (seconds > 0) "${minutes}m ${seconds}s" else "${minutes}m"
        else -> "${seconds}s"
    }
}

data class WorkoutSession(
    val id: String,
    val plannedDayOfWeek: Int?,
    val performedAt: Long,
    val workoutName: String,
    val results: List<ExerciseResult>
)

data class ExerciseResult(
    val exerciseName: String,
    val trackingType: TrackingType,
    val valuePrimary: String? = null,
    val valueSecondary: String? = null,
    val note: String? = null
)

data class Settings(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

fun Int.toReadableDuration(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> if (seconds == 0) "$minutes min" else "${minutes}m ${seconds}s"
        else -> "${seconds}s"
    }
}