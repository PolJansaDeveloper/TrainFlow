package com.pjdev.trainflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pjdev.trainflow.domain.model.DayWorkout
import com.pjdev.trainflow.ui.components.common.HomeBackground
import com.pjdev.trainflow.ui.components.common.dayLabels
import com.pjdev.trainflow.ui.components.home.HomeHeader
import com.pjdev.trainflow.ui.components.home.PlannerButton
import com.pjdev.trainflow.ui.components.home.QuickStatsRow
import com.pjdev.trainflow.ui.components.home.TodayWorkoutCard

@Composable
fun HomeScreen(
    day: DayWorkout,
    currentDayOfWeek: Int,
    onWeekPlanner: () -> Unit,
    onOpenWorkout: (Int, String) -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit
) {
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        HomeBackground(isRestDay = day.isRestDay)

        Scaffold(
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 18.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HomeHeader(
                        onHistory = onHistory,
                        onSettings = onSettings,
                        isRestDay = day.isRestDay
                    )
                }

                item {
                    TodaySection(currentDayOfWeek = currentDayOfWeek, isRestDay = day.isRestDay)
                }

                item {
                    TodayWorkoutCard(
                        day = day,
                        currentDayOfWeek = currentDayOfWeek,
                        onOpenWorkout = onOpenWorkout
                    )
                }

                item {
                    QuickStatsRow(day = day)
                }

                item {
                    PlannerButton(
                        onWeekPlanner = onWeekPlanner,
                        day.isRestDay
                    )
                }
            }
        }
    }
}

@Composable
private fun TodaySection(currentDayOfWeek: Int, isRestDay: Boolean = false)
{
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${dayLabels[currentDayOfWeek - 1]} ",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isRestDay) "Take a breath" else "Ready to train?",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}