package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.TaskRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case to roll over all uncompleted daily tasks scheduled before today
 * and reset habit completions for the new day.
 */
class RollOverTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Executes the rollover logic.
     */
    suspend operator fun invoke() {
        val today = LocalDate.now()
        // Get all daily tasks scheduled before today that were not completed
        val uncompletedTasks = taskRepository.getUncompletedDailyBefore(today)

        uncompletedTasks.filterIsInstance<Task.Daily>().forEach { task ->
            val updatedTask = task.copy(
                scheduledDate = today,
                rollOverCount = task.rollOverCount + 1
            )
            taskRepository.updateTask(updatedTask)
        }

        // Reset all habit completions for the new day
        taskRepository.resetHabitTasksCompletion()
    }
}
