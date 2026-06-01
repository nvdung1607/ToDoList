package com.example.todolist.domain.model

/**
 * Combined result containing the active tasks for today grouped by their types.
 */
data class TodayTasksResult(
    val habitTasks: List<Task.Habit>,
    val dailyTasks: List<Task.Daily>,
    val oneTimeTasks: List<Task.OneTime>
)
