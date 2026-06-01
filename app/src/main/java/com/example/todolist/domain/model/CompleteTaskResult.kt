package com.example.todolist.domain.model

/**
 * Result representing the score points earned and the new streak achieved after completing a task.
 */
data class CompleteTaskResult(
    val pointsGained: Int,
    val newStreak: Int
)
