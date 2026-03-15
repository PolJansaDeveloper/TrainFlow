package com.pjdev.trainflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.ui.components.common.HomeBackground
import com.pjdev.trainflow.ui.components.planner.PlannerDayCard
import com.pjdev.trainflow.ui.components.planner.PlannerHeader
import com.pjdev.trainflow.ui.components.planner.WeeklySummaryCard

@Composable
fun WeekPlannerScreen(
    days: List<DayWorkout>,
    onEdit: (Int) -> Unit,
    onOpenWorkout: (Int) -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomeBackground()

        Scaffold(
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    PlannerHeader(
                        onBack = onBack,
                        title = "Week PLanner",
                        subtitle = "Organize your training week and keep every session under control."
                    )
                }

                item {
                    WeeklySummaryCard(days = days)
                }

                items(days) { day ->
                    PlannerDayCard(
                        day = day,
                        onEdit = { onEdit(day.dayOfWeek) },
                        onOpenWorkout = { onOpenWorkout(day.dayOfWeek) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}