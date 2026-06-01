package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case to validate and update an existing task.
 */
class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Executes the use case.
     *
     * @param task The task to update.
     * @throws IllegalArgumentException if the task title is blank.
     */
    suspend operator fun invoke(task: Task) {
        if (task.title.isBlank()) {
            throw IllegalArgumentException("Task title cannot be empty")
        }
        taskRepository.updateTask(task)
    }
}
