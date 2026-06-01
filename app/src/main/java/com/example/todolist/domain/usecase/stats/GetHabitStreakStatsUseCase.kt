package com.example.todolist.domain.usecase.stats

import com.example.todolist.domain.model.HabitStreakStat
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to observe Habit tasks and rank them by their current completion streaks.
 */
class GetHabitStreakStatsUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Executes the use case.
     *
     * @return A Flow emitting the ranked list of [HabitStreakStat] objects.
     */
    operator fun invoke(): Flow<List<HabitStreakStat>> {
        return taskRepository.observeAllTasks().map { tasks ->
            tasks.filterIsInstance<Task.Habit>()
                .map { habit ->
                    HabitStreakStat(
                        taskId = habit.id,
                        taskTitle = habit.title,
                        currentStreak = habit.currentStreak,
                        longestStreak = habit.longestStreak
                    )
                }
                .sortedByDescending { it.currentStreak }
        }
    }
}
