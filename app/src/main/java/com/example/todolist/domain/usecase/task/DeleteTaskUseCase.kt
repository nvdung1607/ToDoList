package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case to delete a task and return its copy to support undo.
 */
class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Executes the use case.
     *
     * @param taskId The ID of the task to delete.
     * @return The deleted task (fetched before deletion), or null if not found.
     */
    suspend operator fun invoke(taskId: String): Task? {
        val task = taskRepository.getTaskById(taskId)
        if (task != null) {
            taskRepository.deleteTask(taskId)
        }
        return task
    }
}
