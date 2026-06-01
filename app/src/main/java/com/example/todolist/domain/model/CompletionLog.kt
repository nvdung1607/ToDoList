package com.example.todolist.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Log ghi lại từng lần hoàn thành task.
 * Đây là nguồn dữ liệu chính cho:
 *   - Màn hình lịch sử (đã hoàn thành theo ngày/tuần/tháng)
 *   - Heatmap hoạt động (đếm số lần hoàn thành theo ngày)
 *   - Tính streak cho Habit Task
 *
 * @property id          ID duy nhất của bản ghi
 * @property taskId      ID task được hoàn thành (foreign key)
 * @property taskTitle   Tên task tại thời điểm hoàn thành (snapshot, phòng task bị sửa sau đó)
 * @property taskType    Loại task (HABIT / DAILY / ONE_TIME)
 * @property completedAt Thời điểm hoàn thành chính xác
 * @property pointsGained Điểm kiếm được từ lần hoàn thành này (sau khi áp streak bonus)
 * @property streakAtCompletion Streak của task tại thời điểm hoàn thành (chỉ có nghĩa với HABIT)
 */
data class CompletionLog(
    val id: String,
    val taskId: String,
    val taskTitle: String,
    val taskType: TaskType,
    val completedAt: LocalDateTime,
    val pointsGained: Int,
    val streakAtCompletion: Int = 0
) {
    /** Ngày hoàn thành (dùng để group theo ngày trong heatmap và lịch sử) */
    val completedDate: LocalDate get() = completedAt.toLocalDate()
}
