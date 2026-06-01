package com.example.todolist.domain.repository

import com.example.todolist.domain.model.CompletionLog
import com.example.todolist.domain.model.DateActivityCount
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing task completion logs.
 */
interface CompletionLogRepository {
    /**
     * Inserts a new task completion log.
     */
    suspend fun insertLog(log: CompletionLog)

    /**
     * Observes task completion logs for a specific date.
     */
    fun observeLogsForDate(date: LocalDate): Flow<List<CompletionLog>>

    /**
     * Observes task completion logs within a specific date range.
     */
    fun observeLogsBetweenDates(from: LocalDate, to: LocalDate): Flow<List<CompletionLog>>

    /**
     * Retrieves the completion activity count per date within a range (used for heatmap visualizations).
     */
    suspend fun getActivityCountByDate(from: LocalDate, to: LocalDate): List<DateActivityCount>

    /**
     * Retrieves logs associated with a specific Habit task ID.
     */
    suspend fun getHabitTaskLogs(taskId: String): List<CompletionLog>

    /**
     * Observes the total points gained from completions within a date range.
     */
    fun observeTotalPointsBetweenDates(from: LocalDate, to: LocalDate): Flow<Int>
}
