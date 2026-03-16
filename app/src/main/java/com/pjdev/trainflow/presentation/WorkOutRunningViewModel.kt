package com.pjdev.trainflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.domain.session.ActiveWorkoutState
import com.pjdev.trainflow.domain.session.WorkoutPhase
import com.pjdev.trainflow.domain.session.WorkoutRuntimeSnapshot
import com.pjdev.trainflow.domain.session.WorkoutSessionEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutRunningViewModel(
    private val engine: WorkoutSessionEngine = WorkoutSessionEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActiveWorkoutState())
    val uiState: StateFlow<ActiveWorkoutState> = _uiState.asStateFlow()

    private var snapshot: WorkoutRuntimeSnapshot? = null
    private var tickerJob: Job? = null

    fun startWorkout(day: DayWorkout) {
        val now = System.currentTimeMillis()
        snapshot = engine.start(day, now)
        updateUi()
        startTicker()
    }

    fun finishWorkout() {
        tickerJob?.cancel()
        snapshot = snapshot?.copy(
            phase = WorkoutPhase.COMPLETED,
            phaseStartedAtMillis = System.currentTimeMillis(),
            phaseDurationSeconds = 0
        )
        updateUi()
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                val current = snapshot
                if (current == null) {
                    delay(1000)
                    continue
                }

                val now = System.currentTimeMillis()

                snapshot = if (
                    current.phase != WorkoutPhase.COMPLETED &&
                    engine.shouldAdvance(current, now)
                ) {
                    engine.advance(current, now)
                } else {
                    current
                }

                updateUi()
                delay(1000)
            }
        }
    }

    private fun updateUi() {
        val current = snapshot ?: return
        _uiState.value = engine.toUiState(current, System.currentTimeMillis())
    }

    override fun onCleared() {
        tickerJob?.cancel()
        super.onCleared()
    }
}