package com.example.todolist.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Domain model trung tâm — đại diện cho một task bất kỳ trong app.
 *
 * Sử dụng sealed class để phân biệt rõ 3 loại task khác nhau,
 * cho phép xử lý an toàn bằng `when` expression mà không cần casting.
 *
 * Các thuộc tính chung được đặt trong [Task] base class:
 * @property id          ID duy nhất (UUID string)
 * @property title       Tiêu đề task (bắt buộc)
 * @property note        Ghi chú thêm (tuỳ chọn)
 * @property categoryId  ID danh mục gán cho task (null = không có danh mục)
 * @property priority    Mức độ ưu tiên: HIGH / MEDIUM / LOW
 * @property isCompleted Trạng thái hoàn thành của ngày hiện tại
 * @property createdAt   Thời điểm tạo task
 */
sealed class Task {
    abstract val id: String
    abstract val title: String
    abstract val note: String?
    abstract val categoryId: String?
    abstract val priority: Priority
    abstract val isCompleted: Boolean
    abstract val createdAt: LocalDateTime

    // ─────────────────────────────────────────────────────────────────────────
    // HABIT TASK — Thói quen lặp lại
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Task lặp lại theo chu kỳ (hàng ngày hoặc theo ngày trong tuần).
     *
     * @property recurrence     Cấu hình chu kỳ lặp (DAILY hoặc WEEKLY + danh sách ngày)
     * @property reminderTime   Giờ nhắc nhở hàng ngày (ví dụ: 08:00). Null = không nhắc
     * @property currentStreak  Số ngày hoàn thành liên tiếp hiện tại
     * @property longestStreak  Chuỗi dài nhất đã đạt được (record cá nhân)
     * @property durationGoal   Mục tiêu thời lượng (phút). Null = không có mục tiêu
     *                          Ví dụ: 120 = "2 tiếng/ngày"
     * @property lastCompletedDate Ngày cuối cùng đã hoàn thành (để tính streak)
     */
    data class Habit(
        override val id: String,
        override val title: String,
        override val note: String? = null,
        override val categoryId: String? = null,
        override val priority: Priority = Priority.MEDIUM,
        override val isCompleted: Boolean = false,
        override val createdAt: LocalDateTime = LocalDateTime.now(),
        val recurrence: RecurrenceConfig = RecurrenceConfig(RecurrenceType.DAILY),
        val reminderTime: LocalTime? = null,
        val currentStreak: Int = 0,
        val longestStreak: Int = 0,
        val durationGoalMinutes: Int? = null,
        val lastCompletedDate: LocalDate? = null
    ) : Task()

    // ─────────────────────────────────────────────────────────────────────────
    // DAILY TASK — Công việc trong ngày
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Task trong ngày — có thể roll-over sang hôm sau nếu chưa hoàn thành.
     *
     * @property scheduledDate   Ngày thực hiện task (mặc định = hôm nay)
     * @property deadlineTime    Giờ cụ thể trong ngày cần hoàn thành (tuỳ chọn)
     * @property rollOverCount   Số lần task đã bị đẩy sang ngày hôm sau
     *                           Dùng để hiển thị badge "Từ hôm qua ⚠️" và thống kê
     * @property originalDate    Ngày task ban đầu được tạo ra (trước khi roll-over)
     */
    data class Daily(
        override val id: String,
        override val title: String,
        override val note: String? = null,
        override val categoryId: String? = null,
        override val priority: Priority = Priority.MEDIUM,
        override val isCompleted: Boolean = false,
        override val createdAt: LocalDateTime = LocalDateTime.now(),
        val scheduledDate: LocalDate = LocalDate.now(),
        val deadlineTime: LocalTime? = null,
        val rollOverCount: Int = 0,
        val originalDate: LocalDate = LocalDate.now()
    ) : Task()

    // ─────────────────────────────────────────────────────────────────────────
    // ONE-TIME TASK — Việc có deadline cụ thể
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Task chỉ xuất hiện 1 lần, có deadline ngày giờ cụ thể.
     *
     * @property deadline           Ngày giờ deadline tuyệt đối
     * @property reminderMinutesBefore  Nhắc trước deadline bao nhiêu phút
     *                                  Ví dụ: 60 = nhắc trước 1 giờ, 1440 = trước 1 ngày
     *                                  Null = không nhắc
     * @property completedAt        Thời điểm hoàn thành thực tế (null nếu chưa xong)
     */
    data class OneTime(
        override val id: String,
        override val title: String,
        override val note: String? = null,
        override val categoryId: String? = null,
        override val priority: Priority = Priority.MEDIUM,
        override val isCompleted: Boolean = false,
        override val createdAt: LocalDateTime = LocalDateTime.now(),
        val deadline: LocalDateTime,
        val reminderMinutesBefore: Int? = null,
        val completedAt: LocalDateTime? = null
    ) : Task()
}
