package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.model.CompleteTaskResult
import com.example.todolist.domain.model.CompletionLog
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.model.TaskType
import com.example.todolist.domain.repository.CompletionLogRepository
import com.example.todolist.domain.repository.ScoreRepository
import com.example.todolist.domain.repository.TaskRepository
import com.example.todolist.domain.usecase.habit.UpdateStreakUseCase
import com.example.todolist.domain.usecase.score.CalculateScoreUseCase
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.math.max

/**
 * Orchestrator use case to mark a task as completed.
 * Applies scoring, updates streaks, writes logs, and updates the score record.
 */
class CompleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val scoreRepository: ScoreRepository,
    private val completionLogRepository: CompletionLogRepository,
    private val updateStreakUseCase: UpdateStreakUseCase,
    private val calculateScoreUseCase: CalculateScoreUseCase
) {
    /**
     * Executes the task completion orchestration flow.
     *
     * @param task The task being completed.
     * @return [CompleteTaskResult] containing points gained and the new streak (for habits).
     */
    suspend operator fun invoke(task: Task): CompleteTaskResult {
        val today = LocalDate.now()
        val now = LocalDateTime.now()

        // Step 1: Calculate new streak (Habit only, others are 0)
        val newStreak = if (task is Task.Habit) {
            updateStreakUseCase(task)
        } else {
            0
        }

        // Step 2: Calculate points gained
        val taskType = when (task) {
            is Task.Habit -> TaskType.HABIT
            is Task.Daily -> TaskType.DAILY
            is Task.OneTime -> TaskType.ONE_TIME
        }
        val pointsGained = calculateScoreUseCase(taskType, newStreak)

        // Step 3: Build updatedTask based on the specific Task subtype
        val updatedTask = when (task) {
            is Task.Habit -> {
                task.copy(
                    isCompleted = true,
                    currentStreak = newStreak,
                    longestStreak = max(task.longestStreak, newStreak),
                    lastCompletedDate = today
                )
            }
            is Task.Daily -> {
                task.copy(
                    isCompleted = true
                )
            }
            is Task.OneTime -> {
                task.copy(
                    isCompleted = true,
                    completedAt = now
                )
            }
        }

        // Step 4: Persist updated task in repository
        taskRepository.updateTask(updatedTask)

        // Step 5: Record completion history log
        val log = CompletionLog(
            id = UUID.randomUUID().toString(),
            taskId = task.id,
            taskTitle = task.title,
            taskType = taskType,
            completedAt = now,
            pointsGained = pointsGained,
            streakAtCompletion = newStreak
        )
        completionLogRepository.insertLog(log)

        // Step 6: Update user score registry
        scoreRepository.addPointsForToday(pointsGained)

        return CompleteTaskResult(
            pointsGained = pointsGained,
            newStreak = newStreak
        )
    }
}
