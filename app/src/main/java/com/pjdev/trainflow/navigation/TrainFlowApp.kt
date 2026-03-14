package com.pjdev.trainflow.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.presentation.TrainFlowViewModel
import com.pjdev.trainflow.ui.screens.EditDayWorkoutScreen
import com.pjdev.trainflow.ui.screens.ExerciseEditorScreen
import com.pjdev.trainflow.ui.screens.HistoryScreen
import com.pjdev.trainflow.ui.screens.HomeScreen
import com.pjdev.trainflow.ui.screens.SettingsScreen
import com.pjdev.trainflow.ui.screens.WeekPlannerScreen
import com.pjdev.trainflow.ui.screens.WorkoutPreviewScreen
import com.pjdev.trainflow.ui.screens.WorkoutSummaryScreen

@Composable
fun TrainFlowApp(viewModel: TrainFlowViewModel) {
    val state by viewModel.uiState.collectAsState()
    var route: AppRoute by remember { mutableStateOf(AppRoute.Home) }

    fun dayBy(dayOfWeek: Int): DayWorkout = state.plan.days.firstOrNull { it.dayOfWeek == dayOfWeek }
        ?: DayWorkout(dayOfWeek, "", true)

    when (val current = route) {
        AppRoute.Home -> HomeScreen(
            day = dayBy(state.currentDayOfWeek),
            currentDayOfWeek = state.currentDayOfWeek,
            onWeekPlanner = { route = AppRoute.WeekPlanner },
            onOpenWorkout = { day -> route = AppRoute.WorkoutPreview(day) },
            onHistory = { route = AppRoute.History },
            onSettings = { route = AppRoute.Settings }
        )

        AppRoute.WeekPlanner -> WeekPlannerScreen(
            days = state.plan.days,
            onEdit = { route = AppRoute.EditDay(it) },
            onOpenWorkout = { route = AppRoute.WorkoutPreview(it) },
            onBack = { route = AppRoute.Home }
        )

        is AppRoute.EditDay -> EditDayWorkoutScreen(
            day = dayBy(current.dayOfWeek),
            onSave = { viewModel.saveDay(it); route = AppRoute.WeekPlanner },
            onEditExercise = { route = AppRoute.EditExercise(current.dayOfWeek, it) },
            onDeleteExercise = { viewModel.deleteExercise(current.dayOfWeek, it) },
            onBack = { route = AppRoute.WeekPlanner }
        )

        is AppRoute.EditExercise -> ExerciseEditorScreen(
            exercise = dayBy(current.dayOfWeek).exercises.firstOrNull { it.id == current.exerciseId },
            onSave ={ name, sets, reps, work, rest, tracking ->
                viewModel.saveExercise(
                    current.dayOfWeek,
                    current.exerciseId,
                    name,
                    sets,
                    reps,
                    work,
                    rest,
                    tracking
                )
                route = AppRoute.EditDay(current.dayOfWeek)
            },
            onBack = { route = AppRoute.EditDay(current.dayOfWeek) }
        )

        is AppRoute.WorkoutPreview -> WorkoutPreviewScreen(
            day = dayBy(current.dayOfWeek),
            latestResults = state.latestResults,
            onSummary = { route = AppRoute.WorkoutSummary(current.dayOfWeek) },
            onBack = { route = AppRoute.WeekPlanner }
        )

        is AppRoute.WorkoutSummary -> WorkoutSummaryScreen(
            day = dayBy(current.dayOfWeek),
            onSave = {
                viewModel.saveSession(current.dayOfWeek, dayBy(current.dayOfWeek).workoutName, it)
                route = AppRoute.History
            },
            onBack = { route = AppRoute.WorkoutPreview(current.dayOfWeek) }
        )

        AppRoute.History -> HistoryScreen(sessions = state.sessions, onBack = { route = AppRoute.Home })
        AppRoute.Settings -> SettingsScreen(
            settings = state.settings,
            onSoundChange = viewModel::updateSound,
            onVibrationChange = viewModel::updateVibration,
            onBack = { route = AppRoute.Home }
        )
    }
}
