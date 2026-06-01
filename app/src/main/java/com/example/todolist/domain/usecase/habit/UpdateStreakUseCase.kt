package com.example.todolist.domain.usecase.habit

import com.example.todolist.domain.model.Task
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case to update and calculate the new completion streak for a Habit task.
 */
class UpdateStreakUseCase @Inject constructor() {
    /**
     * Calculates the new streak for a Habit task.
     *
     * Rules:
     * - If last completed date is yesterday -> streak increments by 1.
     * - If last completed date is today -> streak remains unchanged (already completed today).
     * - Otherwise (e.g. break of 1 or more days, or never completed before) -> streak resets to 1.
     *
     * @param habit The Habit task.
     * @return The updated streak count.
     */
    operator fun invoke(habit: Task.Habit): Int {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val lastDate = habit.lastCompletedDate

        return when (lastDate) {
            today -> habit.currentStreak
            yesterday -> habit.currentStreak + 1
            else -> 1
        }
    }
}
