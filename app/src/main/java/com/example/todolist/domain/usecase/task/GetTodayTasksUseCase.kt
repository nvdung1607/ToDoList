package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.model.Task
import com.example.todolist.domain.model.TodayTasksResult
import com.example.todolist.domain.repository.TaskRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case to retrieve and group all active tasks scheduled for today.
 */
class GetTodayTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Executes the use case.
     *
     * @return A Flow emitting [TodayTasksResult] with categorized tasks.
     */
    operator fun invoke(): Flow<TodayTasksResult> {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek.value // 1 = Monday, 7 = Sunday

        return combine(
            taskRepository.observeHabitTasksForToday(dayOfWeek),
            taskRepository.observeDailyTasksForDate(today),
            taskRepository.observeActiveOneTimeTasks()
        ) { habits, dailies, oneTimes ->
            TodayTasksResult(
                habitTasks = habits.filterIsInstance<Task.Habit>(),
                dailyTasks = dailies.filterIsInstance<Task.Daily>(),
                oneTimeTasks = oneTimes.filterIsInstance<Task.OneTime>()
            )
        }
    }
}
