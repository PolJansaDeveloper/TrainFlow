package com.pjdev.trainflow.data.repository

import com.pjdev.trainflow.data.datastore.TrainFlowDataStore
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.domain.model.Exercise
import com.pjdev.trainflow.domain.model.ExerciseResult
import com.pjdev.trainflow.domain.model.Settings
import com.pjdev.trainflow.domain.model.WeeklyPlan
import com.pjdev.trainflow.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

class TrainFlowRepository(private val store: TrainFlowDataStore) {
    val weeklyPlanFlow: Flow<WeeklyPlan> = store.weeklyPlanFlow
    val sessionsFlow: Flow<List<WorkoutSession>> = store.sessionsFlow
    val settingsFlow: Flow<Settings> = store.settingsFlow

    suspend fun ensureSeeded() = store.ensureSeeded()

    suspend fun saveDay(dayWorkout: DayWorkout) {
        val plan = weeklyPlanFlow.first()
        val updated = plan.copy(days = plan.days.map { if (it.dayOfWeek == dayWorkout.dayOfWeek) dayWorkout else it })
        store.saveWeeklyPlan(updated)
    }

    suspend fun getDay(dayOfWeek: Int): DayWorkout = weeklyPlanFlow.first().days.first { it.dayOfWeek == dayOfWeek }

    suspend fun saveExercise(dayOfWeek: Int, exercise: Exercise) {
        val day = getDay(dayOfWeek)
        val updated = day.copy(
            isRestDay = false,
            exercises = day.exercises.filterNot { it.id == exercise.id } + exercise,
            workoutName = if (day.workoutName.isBlank()) "Workout" else day.workoutName
        )
        saveDay(updated)
    }

    suspend fun deleteExercise(dayOfWeek: Int, exerciseId: String) {
        val day = getDay(dayOfWeek)
        saveDay(day.copy(exercises = day.exercises.filterNot { it.id == exerciseId }))
    }

    suspend fun createSession(dayOfWeek: Int?, workoutName: String, results: List<ExerciseResult>) {
        val current = sessionsFlow.first()
        val newSession = WorkoutSession(
            id = UUID.randomUUID().toString(),
            plannedDayOfWeek = dayOfWeek,
            performedAt = System.currentTimeMillis(),
            workoutName = workoutName,
            results = results
        )
        store.saveSessions((listOf(newSession) + current).sortedByDescending { it.performedAt })
    }

    fun latestResultByExerciseNameFlow(): Flow<Map<String, ExerciseResult>> = sessionsFlow.map { sessions ->
        val map = mutableMapOf<String, ExerciseResult>()
        sessions.sortedByDescending { it.performedAt }.forEach { session ->
            session.results.forEach { result ->
                map.putIfAbsent(result.exerciseName, result)
            }
        }
        map
    }

    suspend fun updateSettings(soundEnabled: Boolean? = null, vibrationEnabled: Boolean? = null) {
        store.updateSettings { current ->
            current.copy(
                soundEnabled = soundEnabled ?: current.soundEnabled,
                vibrationEnabled = vibrationEnabled ?: current.vibrationEnabled
            )
        }
    }
}
