package com.example.todolist.domain.model

import java.time.LocalDate

/**
 * Model tổng hợp dữ liệu thống kê cho màn hình Dashboard.
 * Được tính toán bởi Use Cases từ CompletionLog + ScoreRecord,
 * không lưu trực tiếp vào database.
 *
 * @property date               Ngày thống kê
 * @property totalTasks         Tổng số task có trong ngày
 * @property completedTasks     Số task đã hoàn thành
 * @property totalPoints        Tổng điểm kiếm được
 * @property habitCompletions   Số Habit Task hoàn thành
 * @property dailyCompletions   Số Daily Task hoàn thành
 * @property oneTimeCompletions Số One-time Task hoàn thành
 */
data class DailyStats(
    val date: LocalDate,
    val totalTasks: Int,
    val completedTasks: Int,
    val totalPoints: Int,
    val habitCompletions: Int = 0,
    val dailyCompletions: Int = 0,
    val oneTimeCompletions: Int = 0
) {
    /** Tỷ lệ hoàn thành trong ngày (0.0 → 1.0) */
    val completionRate: Float
        get() = if (totalTasks == 0) 0f else completedTasks.toFloat() / totalTasks.toFloat()

    /** Phần trăm hiển thị trên UI (0 → 100) */
    val completionPercent: Int get() = (completionRate * 100).toInt()
}

/**
 * Model tổng hợp dữ liệu heatmap (kiểu GitHub contribution graph).
 *
 * @property date           Ngày
 * @property activityCount  Số task hoàn thành trong ngày đó (càng nhiều → màu càng đậm)
 */
data class HeatmapEntry(
    val date: LocalDate,
    val activityCount: Int
)

/**
 * Model thống kê streak cho một Habit Task cụ thể — hiển thị trong bảng leaderboard.
 *
 * @property taskId         ID của Habit Task
 * @property taskTitle      Tên task
 * @property currentStreak  Streak hiện tại (ngày)
 * @property longestStreak  Streak dài nhất (record)
 */
data class HabitStreakStat(
    val taskId: String,
    val taskTitle: String,
    val currentStreak: Int,
    val longestStreak: Int
)

/**
 * Model thống kê task hay bị bỏ qua — hiển thị trong danh sách "Cần chú ý".
 *
 * @property taskId       ID task
 * @property taskTitle    Tên task
 * @property rollOverCount Số lần bị đẩy sang hôm sau
 * @property taskType     Loại task (thường là DAILY)
 */
data class SkippedTaskStat(
    val taskId: String,
    val taskTitle: String,
    val rollOverCount: Int,
    val taskType: TaskType
)
