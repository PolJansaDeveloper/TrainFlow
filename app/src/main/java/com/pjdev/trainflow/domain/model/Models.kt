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
        (1..7).map { DayWorkout(dayOfWeek = it, workoutName = "", isRestDay = true) }
)

data class DayWorkout(
    val dayOfWeek: Int,
    val workoutName: String,
    val isRestDay: Boolean,
    val exercises: List<Exercise> = emptyList()
) {

    /**
     * Duración total del entreno en segundos
     */
    fun totalWorkoutSeconds(): Int {
        if (isRestDay) return 0

        return exercises.sumOf { it.totalExerciseSeconds() }
    }
}

data class Exercise(
    val id: String,
    val name: String,
    val sets: Int,
    val repsTarget: String,
    val workSeconds: Int,
    val restSeconds: Int,
    val trackingType: TrackingType
) {

    /**
     * Tiempo total de este ejercicio (trabajo + descansos)
     */
    fun totalExerciseSeconds(): Int {

        val totalWork = sets * workSeconds

        val totalRest =
            if (sets > 1) (sets - 1) * restSeconds
            else 0

        return totalWork + totalRest
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


// ------------------------------------------------------------
// Helpers de tiempo (muy útiles para UI)
// ------------------------------------------------------------

fun Int.toReadableDuration(): String {

    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> if (seconds == 0) "${minutes} min" else "${minutes}m ${seconds}s"
        else -> "${seconds}s"
    }
}