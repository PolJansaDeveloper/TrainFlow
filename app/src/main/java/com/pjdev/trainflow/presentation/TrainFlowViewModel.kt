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
import com.pjdev.trainflow.domain.model.WorkoutSession
import kotlinx.coroutines.flow.MutableStateFlow
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

    val activeWorkoutDay = MutableStateFlow<Int?>(null)

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun setActiveWorkoutDay(day: Int?) {
        activeWorkoutDay.value = day
    }

    fun saveDay(day: DayWorkout) =
        viewModelScope.launch {
            repository.saveDay(day)
        }

    fun deleteExercise(dayOfWeek: Int, exerciseId: String) =
        viewModelScope.launch {
            repository.deleteExercise(dayOfWeek, exerciseId)
        }

    /**
     * Crear o editar ejercicio
     */
    fun saveExercise(
        dayOfWeek: Int,
        existingId: String?,
        name: String,
        sets: Int,
        repsTarget: String,
        workSeconds: Int,
        restSeconds: Int,
        trackingType: TrackingType
    ) = viewModelScope.launch {

        repository.saveExercise(
            dayOfWeek,
            Exercise(
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