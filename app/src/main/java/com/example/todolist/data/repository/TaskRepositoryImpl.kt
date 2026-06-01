package com.example.todolist.data.repository

import com.example.todolist.data.local.dao.TaskDao
import com.example.todolist.data.mapper.toDomain
import com.example.todolist.data.mapper.toEntity
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.TaskRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun observeHabitTasksForToday(dayOfWeek: Int): Flow<List<Task>> {
        return taskDao.observeHabitTasksForDay(dayOfWeek).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeDailyTasksForDate(date: LocalDate): Flow<List<Task>> {
        return taskDao.observeDailyTasksForDate(date.toString()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeActiveOneTimeTasks(): Flow<List<Task>> {
        return taskDao.observeActiveOneTimeTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeTasksByCategory(categoryId: String): Flow<List<Task>> {
        return taskDao.observeTasksByCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeTaskById(taskId: String): Flow<Task?> {
        return taskDao.observeTaskById(taskId).map { it?.toDomain() }
    }

    override suspend fun createTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteTaskById(taskId)
    }

    override suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    override suspend fun getUncompletedDailyBefore(date: LocalDate): List<Task> {
        return taskDao.getUncompletedDailyTasksBefore(date.toString()).map { it.toDomain() }
    }

    override suspend fun resetHabitTasksCompletion() {
        taskDao.resetHabitTasksCompletion()
    }

    override suspend fun getAllTasks(): List<Task> {
        return taskDao.getAllTasks().map { it.toDomain() }
    }

    override fun observeAllTasks(): Flow<List<Task>> {
        return taskDao.observeAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
