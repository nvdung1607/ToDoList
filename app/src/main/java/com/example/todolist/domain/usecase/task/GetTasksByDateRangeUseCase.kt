package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.model.DateRangeTasksResult
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.CompletionLogRepository
import com.example.todolist.domain.repository.TaskRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to combine daily tasks and completion logs within a specified date range.
 */
class GetTasksByDateRangeUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val completionLogRepository: CompletionLogRepository
) {
    /**
     * Executes the use case.
     *
     * @param fromDate Start date.
     * @param toDate End date.
     * @return A Flow emitting [DateRangeTasksResult] with combined tasks and logs.
     */
    operator fun invoke(fromDate: LocalDate, toDate: LocalDate): Flow<DateRangeTasksResult> {
        return completionLogRepository.observeLogsBetweenDates(fromDate, toDate).map { logs ->
            val allTasks = taskRepository.getAllTasks()
            val dailyTasks = allTasks.filterIsInstance<Task.Daily>()
                .filter { it.scheduledDate in fromDate..toDate }
            DateRangeTasksResult(
                dailyTasks = dailyTasks,
                completionLogs = logs
            )
        }
    }
}
