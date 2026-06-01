package com.example.todolist.domain.model

import java.time.LocalDate

/**
 * Bản ghi điểm số tích lũy theo ngày.
 * Mỗi ngày có 1 bản ghi tổng hợp điểm kiếm được trong ngày đó.
 *
 * @property id             ID duy nhất
 * @property date           Ngày ghi nhận điểm
 * @property pointsEarned   Tổng điểm kiếm được trong ngày
 * @property tasksCompleted Số task đã hoàn thành trong ngày
 * @property tasksTotal     Tổng số task có trong ngày (để tính completion rate)
 */
data class ScoreRecord(
    val id: String,
    val date: LocalDate,
    val pointsEarned: Int,
    val tasksCompleted: Int,
    val tasksTotal: Int
) {
    /** Tỷ lệ hoàn thành trong ngày (0.0 → 1.0) */
    val completionRate: Float
        get() = if (tasksTotal == 0) 0f else tasksCompleted.toFloat() / tasksTotal.toFloat()
}
