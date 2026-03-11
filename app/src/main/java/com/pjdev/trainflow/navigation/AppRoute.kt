package com.pjdev.trainflow.navigation

sealed interface AppRoute {
    data object Home : AppRoute
    data object WeekPlanner : AppRoute
    data class EditDay(val dayOfWeek: Int) : AppRoute
    data class EditExercise(val dayOfWeek: Int, val exerciseId: String?) : AppRoute
    data class WorkoutPreview(val dayOfWeek: Int) : AppRoute
    data class WorkoutSummary(val dayOfWeek: Int) : AppRoute
    data object History : AppRoute
    data object Settings : AppRoute
}
