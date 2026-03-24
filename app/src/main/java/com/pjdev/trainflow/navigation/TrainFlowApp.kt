package com.pjdev.trainflow.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.domain.model.Exercise
import com.pjdev.trainflow.domain.model.WorkoutBlock
import com.pjdev.trainflow.presentation.TrainFlowViewModel
import com.pjdev.trainflow.ui.screens.EditDayWorkoutScreen
import com.pjdev.trainflow.ui.screens.ExerciseEditorScreen
import com.pjdev.trainflow.ui.screens.HistoryScreen
import com.pjdev.trainflow.ui.screens.HomeScreen
import com.pjdev.trainflow.ui.screens.SettingsScreen
import com.pjdev.trainflow.ui.screens.WeekPlannerScreen
import com.pjdev.trainflow.ui.screens.WorkoutRunningScreen
import com.pjdev.trainflow.ui.screens.WorkoutSummaryScreen
import com.pjdev.trainflow.presentation.WorkoutRunningViewModel

@Composable
fun TrainFlowApp(viewModel: TrainFlowViewModel) {
    val state by viewModel.uiState.collectAsState()
    var route: AppRoute by remember { mutableStateOf(AppRoute.Home) }

    fun dayBy(dayOfWeek: Int): DayWorkout =
        state.plan.days.firstOrNull { it.dayOfWeek == dayOfWeek }
            ?: DayWorkout(
                dayOfWeek = dayOfWeek,
                isRestDay = true
            )

    fun workoutBy(dayOfWeek: Int, workoutId: String): WorkoutBlock? =
        dayBy(dayOfWeek).workouts.firstOrNull { it.id == workoutId }

    fun exerciseBy(dayOfWeek: Int, workoutId: String, exerciseId: String?): Exercise? =
        if (exerciseId == null) {
            null
        } else {
            workoutBy(dayOfWeek, workoutId)
                ?.exercises
                ?.firstOrNull { it.id == exerciseId }
        }

    fun firstWorkoutIdOf(dayOfWeek: Int): String? =
        dayBy(dayOfWeek).workouts.firstOrNull()?.id

    fun selectedWorkoutDay(dayOfWeek: Int, workoutId: String): DayWorkout {
        val day = dayBy(dayOfWeek)
        val workout = workoutBy(dayOfWeek, workoutId)

        return if (workout != null) {
            day.copy(
                isRestDay = false,
                workouts = listOf(workout)
            )
        } else {
            day.copy(
                isRestDay = true,
                workouts = emptyList()
            )
        }
    }

    when (val current = route) {
        AppRoute.Home -> {
            HomeScreen(
                day = dayBy(state.currentDayOfWeek),
                currentDayOfWeek = state.currentDayOfWeek,
                onWeekPlanner = {
                    route = AppRoute.WeekPlanner
                },
                onOpenWorkout = { dayOfWeek, workoutId ->
                    route = AppRoute.WorkoutPreview(dayOfWeek, workoutId)
                },
                onHistory = { route = AppRoute.History },
                onSettings = { route = AppRoute.Settings }
            )
        }

        AppRoute.WeekPlanner -> {
            BackHandler {
                route = AppRoute.Home
            }

            WeekPlannerScreen(
                days = state.plan.days,
                onEdit = { route = AppRoute.EditDay(it) },
                onOpenWorkout = { dayOfWeek ->
                    val workoutId = firstWorkoutIdOf(dayOfWeek)
                    route = if (workoutId != null) {
                        AppRoute.WorkoutPreview(dayOfWeek, workoutId)
                    } else {
                        AppRoute.EditDay(dayOfWeek)
                    }
                },
                onBack = { route = AppRoute.Home }
            )
        }

        is AppRoute.EditDay -> {
            BackHandler {
                route = AppRoute.WeekPlanner
            }

            EditDayWorkoutScreen(
                day = dayBy(current.dayOfWeek),
                onSave = {
                    viewModel.saveDay(it)
                    route = AppRoute.WeekPlanner
                },
                onAddWorkout = {
                    viewModel.addWorkout(current.dayOfWeek)
                },
                onUpdateWorkoutName = { workoutId, name ->
                    viewModel.updateWorkoutName(current.dayOfWeek, workoutId, name)
                },
                onDeleteWorkout = { workoutId ->
                    viewModel.deleteWorkout(current.dayOfWeek, workoutId)
                },
                onEditExercise = { workoutId, exerciseId ->
                    route = AppRoute.EditExercise(
                        dayOfWeek = current.dayOfWeek,
                        workoutId = workoutId,
                        exerciseId = exerciseId
                    )
                },
                onDeleteExercise = { workoutId, exerciseId ->
                    viewModel.deleteExercise(
                        dayOfWeek = current.dayOfWeek,
                        workoutId = workoutId,
                        exerciseId = exerciseId
                    )
                },
                onBack = { route = AppRoute.WeekPlanner }
            )
        }

        is AppRoute.EditExercise -> {
            BackHandler {
                route = AppRoute.EditDay(current.dayOfWeek)
            }

            ExerciseEditorScreen(
                exercise = exerciseBy(
                    dayOfWeek = current.dayOfWeek,
                    workoutId = current.workoutId,
                    exerciseId = current.exerciseId
                ),
                onSave = { name, sets, reps, work, rest, tracking ->
                    viewModel.saveExercise(
                        dayOfWeek = current.dayOfWeek,
                        workoutId = current.workoutId,
                        existingId = current.exerciseId,
                        name = name,
                        sets = sets,
                        repsTarget = reps,
                        workSeconds = work,
                        restSeconds = rest,
                        trackingType = tracking
                    )
                    route = AppRoute.EditDay(current.dayOfWeek)
                },
                onBack = { route = AppRoute.EditDay(current.dayOfWeek) }
            )
        }

        is AppRoute.WorkoutPreview -> {
            BackHandler {
                route = AppRoute.WeekPlanner
            }

            val workoutViewModel: WorkoutRunningViewModel =
                viewModel(key = "workout-${current.dayOfWeek}-${current.workoutId}")
            val uiState by workoutViewModel.uiState.collectAsState()

            val selectedDay = selectedWorkoutDay(current.dayOfWeek, current.workoutId)

            WorkoutRunningScreen(
                day = selectedDay,
                uiState = uiState,
                onStartWorkout = {
                    workoutViewModel.startWorkout(selectedDay)
                },
                onFinishWorkout = {
                    workoutViewModel.finishWorkout()
                    route = AppRoute.WorkoutSummary(
                        dayOfWeek = current.dayOfWeek,
                        workoutId = current.workoutId
                    )
                },
                onBack = { route = AppRoute.WeekPlanner }
            )
        }

        is AppRoute.WorkoutSummary -> {
            BackHandler {
                route = AppRoute.WorkoutPreview(
                    dayOfWeek = current.dayOfWeek,
                    workoutId = current.workoutId
                )
            }

            WorkoutSummaryScreen(
                day = selectedWorkoutDay(current.dayOfWeek, current.workoutId),
                onSave = { results ->
                    val workoutName = workoutBy(current.dayOfWeek, current.workoutId)?.name ?: "Workout"

                    viewModel.saveSession(
                        dayOfWeek = current.dayOfWeek,
                        workoutName = workoutName,
                        results = results
                    )
                    route = AppRoute.History
                },
                onBack = {
                    route = AppRoute.WorkoutPreview(
                        dayOfWeek = current.dayOfWeek,
                        workoutId = current.workoutId
                    )
                }
            )
        }

        AppRoute.History -> {
            BackHandler {
                route = AppRoute.Home
            }

            HistoryScreen(
                sessions = state.sessions,
                onBack = { route = AppRoute.Home }
            )
        }

        AppRoute.Settings -> {
            BackHandler {
                route = AppRoute.Home
            }

            SettingsScreen(
                settings = state.settings,
                onSoundChange = viewModel::updateSound,
                onVibrationChange = viewModel::updateVibration,
                onBack = { route = AppRoute.Home }
            )
        }
    }
}