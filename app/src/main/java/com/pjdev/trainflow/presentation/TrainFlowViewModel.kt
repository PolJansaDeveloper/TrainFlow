package com.pjdev.trainflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pjdev.trainflow.data.repository.TrainFlowRepository
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.domain.model.Exercise
import com.pjdev.trainflow.domain.model.ExerciseResult
import com.pjdev.trainflow.domain.model.Settings
import com.pjdev.trainflow.domain.model.TrackingType
import com.pjdev.trainflow.domain.model.WeeklyPlan
import com.pjdev.trainflow.domain.model.WorkoutBlock
import com.pjdev.trainflow.domain.model.WorkoutSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

data class AppUiState(
    val plan: WeeklyPlan = WeeklyPlan(),
    val sessions: List<WorkoutSession> = emptyList(),
    val settings: Settings = Settings(),
    val latestResults: Map<String, ExerciseResult> = emptyMap(),
    val currentDayOfWeek: Int = LocalDate.now().dayOfWeek.value
)

class TrainFlowViewModel(
    private val repository: TrainFlowRepository
) : ViewModel() {

    val uiState: StateFlow<AppUiState> = combine(
        repository.weeklyPlanFlow,
        repository.sessionsFlow,
        repository.settingsFlow,
        repository.latestResultByExerciseNameFlow()
    ) { plan, sessions, settings, latest ->
        AppUiState(plan, sessions, settings, latest)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun saveDay(day: DayWorkout) =
        viewModelScope.launch {
            repository.saveDay(day)
        }

    fun addWorkout(dayOfWeek: Int) =
        viewModelScope.launch {
            repository.addWorkout(
                dayOfWeek = dayOfWeek,
                workout = WorkoutBlock(
                    id = UUID.randomUUID().toString(),
                    name = "Workout",
                    exercises = emptyList()
                )
            )
        }

    fun updateWorkoutName(dayOfWeek: Int, workoutId: String, name: String) =
        viewModelScope.launch {
            repository.updateWorkoutName(dayOfWeek, workoutId, name)
        }

    fun deleteWorkout(dayOfWeek: Int, workoutId: String) =
        viewModelScope.launch {
            repository.deleteWorkout(dayOfWeek, workoutId)
        }

    fun deleteExercise(dayOfWeek: Int, workoutId: String, exerciseId: String) =
        viewModelScope.launch {
            repository.deleteExercise(dayOfWeek, workoutId, exerciseId)
        }

    fun saveExercise(
        dayOfWeek: Int,
        workoutId: String,
        existingId: String?,
        name: String,
        sets: Int,
        repsTarget: String,
        workSeconds: Int,
        restSeconds: Int,
        trackingType: TrackingType
    ) = viewModelScope.launch {
        repository.saveExercise(
            dayOfWeek = dayOfWeek,
            workoutId = workoutId,
            exercise = Exercise(
                id = existingId ?: UUID.randomUUID().toString(),
                name = name,
                sets = sets,
                repsTarget = repsTarget,
                workSeconds = workSeconds,
                restSeconds = restSeconds,
                trackingType = trackingType
            )
        )
    }

    fun saveSession(
        dayOfWeek: Int?,
        workoutName: String,
        results: List<ExerciseResult>
    ) = viewModelScope.launch {
        repository.createSession(dayOfWeek, workoutName, results)
    }

    fun updateSound(enabled: Boolean) =
        viewModelScope.launch {
            repository.updateSettings(soundEnabled = enabled)
        }

    fun updateVibration(enabled: Boolean) =
        viewModelScope.launch {
            repository.updateSettings(vibrationEnabled = enabled)
        }
}