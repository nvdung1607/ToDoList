package com.example.todolist.domain.usecase.score

import com.example.todolist.core.utils.ScoreCalculator
import com.example.todolist.domain.model.TaskType
import javax.inject.Inject

/**
 * Use case to calculate score points for completing a task.
 */
class CalculateScoreUseCase @Inject constructor() {
    /**
     * Calculates the completion score.
     *
     * @param taskType The type of task.
     * @param currentStreak The current active streak (only relevant for habits).
     * @return The calculated score.
     */
    operator fun invoke(taskType: TaskType, currentStreak: Int): Int {
        return ScoreCalculator.calculateScore(taskType, currentStreak)
    }
}
