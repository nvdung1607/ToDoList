package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.TaskRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to validate and create a new task.
 */
class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Executes the use case.
     *
     * @param task The task template to create.
     * @return The created task with updated ID and attributes.
     * @throws IllegalArgumentException if the task title is blank.
     */
    suspend operator fun invoke(task: Task): Task {
        if (task.title.isBlank()) {
            throw IllegalArgumentException("Task title cannot be empty")
        }

        val finalId = if (task.id.isBlank()) UUID.randomUUID().toString() else task.id

        val taskToCreate = when (task) {
            is Task.Habit -> task.copy(id = finalId)
            is Task.Daily -> {
                // Ensure daily scheduledDate falls back to today if missing
                val date = (task as? Task.Daily)?.scheduledDate ?: LocalDate.now()
                val origDate = (task as? Task.Daily)?.originalDate ?: date
                task.copy(
                    id = finalId,
                    scheduledDate = date,
                    originalDate = origDate
                )
            }
            is Task.OneTime -> task.copy(id = finalId)
        }

        taskRepository.createTask(taskToCreate)
        return taskToCreate
    }
}
