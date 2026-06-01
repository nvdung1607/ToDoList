package com.example.todolist.domain.model

/**
 * Result representing daily tasks and completion logs within a specified date range.
 */
data class DateRangeTasksResult(
    val dailyTasks: List<Task.Daily>,
    val completionLogs: List<CompletionLog>
)
