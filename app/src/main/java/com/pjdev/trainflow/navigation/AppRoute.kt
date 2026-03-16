package com.pjdev.trainflow.navigation

sealed interface AppRoute {
    data object Home : AppRoute
    data object WeekPlanner : AppRoute

    data class EditDay(val dayOfWeek: Int) : AppRoute

    data class EditExercise(
        val dayOfWeek: Int,
        val workoutId: String,
        val exerciseId: String?
    ) : AppRoute

    data class WorkoutPreview(
        val dayOfWeek: Int,
        val workoutId: String
    ) : AppRoute

    data class WorkoutSummary(
        val dayOfWeek: Int,
        val workoutId: String
    ) : AppRoute

    data object History : AppRoute
    data object Settings : AppRoute
}