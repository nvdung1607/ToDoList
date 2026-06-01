package com.example.todolist.domain.repository

import com.example.todolist.domain.model.ScoreRecord
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing scores and completions history statistics.
 */
interface ScoreRepository {
    /**
     * Observes the score record for a specific date.
     */
    fun observeRecordForDate(date: LocalDate): Flow<ScoreRecord?>

    /**
     * Observes score records within a specific date range.
     */
    fun observeRecordsBetweenDates(from: LocalDate, to: LocalDate): Flow<List<ScoreRecord>>

    /**
     * Observes the total points accumulated within a date range.
     */
    fun observeTotalPointsBetweenDates(from: LocalDate, to: LocalDate): Flow<Int>

    /**
     * Observes the total points earned across all time.
     */
    fun observeAllTimePoints(): Flow<Int>

    /**
     * Adds points to today's score record (performing an upsert).
     *
     * @param points The points to add.
     */
    suspend fun addPointsForToday(points: Int)

    /**
     * Retrieves or creates a new score record for today's date.
     */
    suspend fun getOrCreateTodayRecord(): ScoreRecord
}
