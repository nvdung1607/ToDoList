package com.example.todolist.domain.usecase.stats

import com.example.todolist.domain.model.SkippedTaskStat
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.model.TaskType
import com.example.todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to retrieve daily tasks that are skipped (rolled over) the most.
 */
class GetMostSkippedTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Executes the use case.
     *
     * @return A Flow emitting the top 10 skipped daily tasks.
     */
    operator fun invoke(): Flow<List<SkippedTaskStat>> {
        return taskRepository.observeAllTasks().map { tasks ->
            tasks.filterIsInstance<Task.Daily>()
                .filter { it.rollOverCount > 0 }
                .sortedByDescending { it.rollOverCount }
                .take(10)
                .map { daily ->
                    SkippedTaskStat(
                        taskId = daily.id,
                        taskTitle = daily.title,
                        rollOverCount = daily.rollOverCount,
                        taskType = TaskType.DAILY
                    )
                }
        }
    }
}
