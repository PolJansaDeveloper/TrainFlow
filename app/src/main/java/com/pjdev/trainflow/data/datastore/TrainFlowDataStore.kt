package com.pjdev.trainflow.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.domain.model.Exercise
import com.pjdev.trainflow.domain.model.ExerciseResult
import com.pjdev.trainflow.domain.model.Settings
import com.pjdev.trainflow.domain.model.TrackingType
import com.pjdev.trainflow.domain.model.WeeklyPlan
import com.pjdev.trainflow.domain.model.WorkoutBlock
import com.pjdev.trainflow.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "trainflow_store")

class TrainFlowDataStore(private val context: Context) {

    private object Keys {
        val WEEKLY_PLAN = stringPreferencesKey("weekly_plan")
        val SESSIONS = stringPreferencesKey("sessions")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }

    val weeklyPlanFlow: Flow<WeeklyPlan> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[Keys.WEEKLY_PLAN]?.let(::weeklyPlanFromJson) ?: demoWeeklyPlan()
        }

    val sessionsFlow: Flow<List<WorkoutSession>> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[Keys.SESSIONS]?.let(::sessionsFromJson) ?: emptyList()
        }

    val settingsFlow: Flow<Settings> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            Settings(
                soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
                vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: true
            )
        }

    suspend fun ensureSeeded() {
        if (context.dataStore.data.first()[Keys.WEEKLY_PLAN].isNullOrBlank()) {
            saveWeeklyPlan(demoWeeklyPlan())
        }
    }

    suspend fun saveWeeklyPlan(plan: WeeklyPlan) {
        context.dataStore.edit { prefs ->
            prefs[Keys.WEEKLY_PLAN] = weeklyPlanToJson(plan)
        }
    }

    suspend fun saveSessions(sessions: List<WorkoutSession>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SESSIONS] = sessionsToJson(sessions)
        }
    }

    suspend fun updateSettings(transform: (Settings) -> Settings) {
        context.dataStore.edit { prefs ->
            val current = Settings(
                soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
                vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: true
            )
            val updated = transform(current)

            prefs[Keys.SOUND_ENABLED] = updated.soundEnabled
            prefs[Keys.VIBRATION_ENABLED] = updated.vibrationEnabled
        }
    }

    private fun weeklyPlanToJson(plan: WeeklyPlan): String =
        JSONObject().apply {
            put("id", plan.id)
            put("name", plan.name)
            put("days", JSONArray().apply {
                plan.days.forEach { day ->
                    put(JSONObject().apply {
                        put("dayOfWeek", day.dayOfWeek)
                        put("isRestDay", day.isRestDay)
                        put("workouts", JSONArray().apply {
                            day.workouts.forEach { workout ->
                                put(JSONObject().apply {
                                    put("id", workout.id)
                                    put("name", workout.name)
                                    put("exercises", JSONArray().apply {
                                        workout.exercises.forEach { exercise ->
                                            put(JSONObject().apply {
                                                put("id", exercise.id)
                                                put("name", exercise.name)
                                                put("sets", exercise.sets)
                                                put("repsTarget", exercise.repsTarget)
                                                put("workSeconds", exercise.workSeconds)
                                                put("restSeconds", exercise.restSeconds)
                                                put("trackingType", exercise.trackingType.key)
                                            })
                                        }
                                    })
                                })
                            }
                        })
                    })
                }
            })
        }.toString()

    private fun weeklyPlanFromJson(raw: String): WeeklyPlan {
        val obj = JSONObject(raw)
        val dayArray = obj.optJSONArray("days") ?: JSONArray()

        return WeeklyPlan(
            id = obj.optString("id", "default-plan"),
            name = obj.optString("name", "Weekly Plan"),
            days = (0 until dayArray.length()).map { index ->
                val dayObj = dayArray.getJSONObject(index)
                val workoutsArray = dayObj.optJSONArray("workouts") ?: JSONArray()

                DayWorkout(
                    dayOfWeek = dayObj.optInt("dayOfWeek", index + 1),
                    isRestDay = dayObj.optBoolean("isRestDay", true),
                    workouts = (0 until workoutsArray.length()).map { workoutIndex ->
                        val workoutObj = workoutsArray.getJSONObject(workoutIndex)
                        val exercisesArray = workoutObj.optJSONArray("exercises") ?: JSONArray()

                        WorkoutBlock(
                            id = workoutObj.optString("id"),
                            name = workoutObj.optString("name", "Workout"),
                            exercises = (0 until exercisesArray.length()).map { exIndex ->
                                val ex = exercisesArray.getJSONObject(exIndex)
                                Exercise(
                                    id = ex.optString("id"),
                                    name = ex.optString("name"),
                                    sets = ex.optInt("sets", 3),
                                    repsTarget = ex.optString("repsTarget", "10"),
                                    workSeconds = ex.optInt("workSeconds", 40),
                                    restSeconds = ex.optInt("restSeconds", 60),
                                    trackingType = TrackingType.fromKey(
                                        ex.optString("trackingType", TrackingType.RepsOnly.key)
                                    )
                                )
                            }
                        )
                    }
                )
            }
        )
    }

    private fun sessionsToJson(sessions: List<WorkoutSession>): String =
        JSONArray().apply {
            sessions.forEach { session ->
                put(JSONObject().apply {
                    put("id", session.id)
                    put("plannedDayOfWeek", session.plannedDayOfWeek)
                    put("performedAt", session.performedAt)
                    put("workoutName", session.workoutName)
                    put("results", JSONArray().apply {
                        session.results.forEach { result ->
                            put(JSONObject().apply {
                                put("exerciseName", result.exerciseName)
                                put("trackingType", result.trackingType.key)
                                put("valuePrimary", result.valuePrimary)
                                put("valueSecondary", result.valueSecondary)
                                put("note", result.note)
                            })
                        }
                    })
                })
            }
        }.toString()

    private fun sessionsFromJson(raw: String): List<WorkoutSession> {
        val array = JSONArray(raw)

        return (0 until array.length()).map { index ->
            val obj = array.getJSONObject(index)
            val results = obj.optJSONArray("results") ?: JSONArray()

            WorkoutSession(
                id = obj.optString("id"),
                plannedDayOfWeek = obj.optInt("plannedDayOfWeek").takeIf { it in 1..7 },
                performedAt = obj.optLong("performedAt"),
                workoutName = obj.optString("workoutName"),
                results = (0 until results.length()).map { i ->
                    val res = results.getJSONObject(i)
                    ExerciseResult(
                        exerciseName = res.optString("exerciseName"),
                        trackingType = TrackingType.fromKey(
                            res.optString("trackingType", TrackingType.RepsOnly.key)
                        ),
                        valuePrimary = res.optString("valuePrimary").ifBlank { null },
                        valueSecondary = res.optString("valueSecondary").ifBlank { null },
                        note = res.optString("note").ifBlank { null }
                    )
                }
            )
        }
    }

    private fun demoWeeklyPlan(): WeeklyPlan {
        val strengthExercises = listOf(
            Exercise(
                id = "ex_push_ups",
                name = "Push Ups",
                sets = 4,
                repsTarget = "10",
                workSeconds = 40,
                restSeconds = 90,
                trackingType = TrackingType.RepsOnly
            ),
            Exercise(
                id = "ex_squat",
                name = "Squat",
                sets = 4,
                repsTarget = "12",
                workSeconds = 40,
                restSeconds = 90,
                trackingType = TrackingType.RepsOnly
            )
        )

        val cardioExercises = listOf(
            Exercise(
                id = "ex_running_intervals",
                name = "Running Intervals",
                sets = 6,
                repsTarget = "60 sec",
                workSeconds = 60,
                restSeconds = 60,
                trackingType = TrackingType.TimeOnly
            )
        )

        val mobilityExercises = listOf(
            Exercise(
                id = "ex_stretch_flow",
                name = "Stretch Flow",
                sets = 3,
                repsTarget = "45 sec",
                workSeconds = 45,
                restSeconds = 30,
                trackingType = TrackingType.TimeOnly
            )
        )

        return WeeklyPlan(
            days = (1..7).map { day ->
                when (day) {
                    1 -> DayWorkout(
                        dayOfWeek = day,
                        isRestDay = false,
                        workouts = listOf(
                            WorkoutBlock(
                                id = "wk_strength",
                                name = "Strength",
                                exercises = strengthExercises
                            )
                        )
                    )

                    3 -> DayWorkout(
                        dayOfWeek = day,
                        isRestDay = false,
                        workouts = listOf(
                            WorkoutBlock(
                                id = "wk_cardio",
                                name = "Cardio",
                                exercises = cardioExercises
                            )
                        )
                    )

                    5 -> DayWorkout(
                        dayOfWeek = day,
                        isRestDay = false,
                        workouts = listOf(
                            WorkoutBlock(
                                id = "wk_mobility",
                                name = "Mobility",
                                exercises = mobilityExercises
                            )
                        )
                    )

                    else -> DayWorkout(
                        dayOfWeek = day,
                        isRestDay = true,
                        workouts = emptyList()
                    )
                }
            }
        )
    }
}