package com.example.todolist.domain.repository

import com.example.todolist.domain.model.Task
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing tasks.
 */
interface TaskRepository {
    /**
     * Observes Habit tasks that are active/scheduled for today.
     *
     * @param dayOfWeek The current day of the week (1 = Monday, ..., 7 = Sunday).
     */
    fun observeHabitTasksForToday(dayOfWeek: Int): Flow<List<Task>>

    /**
     * Observes Daily tasks scheduled for a specific date.
     */
    fun observeDailyTasksForDate(date: LocalDate): Flow<List<Task>>

    /**
     * Observes active (uncompleted) One-Time tasks.
     */
    fun observeActiveOneTimeTasks(): Flow<List<Task>>

    /**
     * Observes all tasks associated with a specific category ID.
     */
    fun observeTasksByCategory(categoryId: String): Flow<List<Task>>

    /**
     * Observes a single task by its ID.
     */
    fun observeTaskById(taskId: String): Flow<Task?>

    /**
     * Creates a new task in the database.
     */
    suspend fun createTask(task: Task)

    /**
     * Updates an existing task in the database.
     */
    suspend fun updateTask(task: Task)

    /**
     * Deletes a task by its ID.
     */
    suspend fun deleteTask(taskId: String)

    /**
     * Retrieves a task by its ID. Returns null if not found.
     */
    suspend fun getTaskById(taskId: String): Task?

    /**
     * Retrieves all uncompleted Daily tasks scheduled before the given date.
     */
    suspend fun getUncompletedDailyBefore(date: LocalDate): List<Task>

    /**
     * Resets the completion status of all Habit tasks.
     */
    suspend fun resetHabitTasksCompletion()

    /**
     * Retrieves all tasks in the database.
     */
    suspend fun getAllTasks(): List<Task>

    /**
     * Observes all tasks in the database (reactive).
     */
    fun observeAllTasks(): Flow<List<Task>>
}
