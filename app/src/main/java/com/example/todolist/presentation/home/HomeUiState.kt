package com.example.todolist.presentation.home

import com.example.todolist.domain.model.ScoreRecord
import com.example.todolist.domain.model.SortOrder
import com.example.todolist.domain.model.Task

data class HomeUiState(
    val isLoading: Boolean = true,
    val habitTasks: List<Task.Habit> = emptyList(),
    val dailyTasks: List<Task.Daily> = emptyList(),
    val oneTimeTasks: List<Task.OneTime> = emptyList(),
    val todayScore: ScoreRecord? = null,
    val selectedCategoryFilter: String? = null,
    val sortOrder: SortOrder = SortOrder.PRIORITY,
    val showPointsAnimation: Boolean = false,
    val animationPoints: Int = 0,
    val animationStreak: Int? = null,
    val deletedTask: Task? = null,
    val showUndoSnackbar: Boolean = false,
    val errorMessage: String? = null
)
