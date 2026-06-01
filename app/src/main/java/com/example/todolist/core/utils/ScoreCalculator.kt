package com.example.todolist.core.utils

import com.example.todolist.core.common.Constants
import com.example.todolist.domain.model.TaskType

/**
 * Utility class to calculate scores and streak multipliers for completed tasks.
 */
object ScoreCalculator {

    /**
     * Calculates the score awarded for completing a task, taking into account its type and streak.
     *
     * @param taskType The type of the completed task ([TaskType.HABIT], [TaskType.DAILY], or [TaskType.ONE_TIME]).
     * @param currentStreak The current completion streak (relevant only for [TaskType.HABIT]).
     * @return The calculated score as an integer.
     */
    fun calculateScore(taskType: TaskType, currentStreak: Int): Int {
        val baseScore = when (taskType) {
            TaskType.HABIT -> Constants.HABIT_BASE_SCORE
            TaskType.DAILY -> Constants.DAILY_BASE_SCORE
            TaskType.ONE_TIME -> Constants.ONE_TIME_BASE_SCORE
        }

        return if (taskType == TaskType.HABIT) {
            (baseScore * getStreakMultiplier(currentStreak)).toInt()
        } else {
            baseScore
        }
    }

    /**
     * Calculates the streak multiplier for habits.
     *
     * Multiplier rates:
     * - 0 to 2 days: x1.0
     * - 3 to 6 days: x1.5
     * - 7 to 13 days: x2.0
     * - 14 to 29 days: x2.5
     * - 30 or more days: x3.0
     *
     * @param streak The current active streak count.
     * @return The multiplier as a Float.
     */
    fun getStreakMultiplier(streak: Int): Float {
        return when (streak) {
            in 0..2 -> 1.0f
            in 3..6 -> 1.5f
            in 7..13 -> 2.0f
            in 14..29 -> 2.5f
            else -> 3.0f
        }
    }
}
