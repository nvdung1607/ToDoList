package com.example.todolist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity — bảng `tasks` trong SQLite.
 *
 * Một bảng duy nhất lưu tất cả 3 loại task (HABIT, DAILY, ONE_TIME).
 * Chiến lược "Single Table Inheritance" — các cột không liên quan đến loại task
 * sẽ có giá trị NULL.
 *
 * Ví dụ:
 *  - HABIT Task  : dùng [recurrenceType], [recurrenceDays], [reminderTime], [currentStreak]...
 *  - DAILY Task  : dùng [scheduledDate], [deadlineTime], [rollOverCount]...
 *  - ONE_TIME Task: dùng [deadlineDateTime], [reminderMinutesBefore], [completedAt]...
 *
 * Foreign Key:
 *  - [categoryId] → bảng `categories` ([CategoryEntity.id])
 *    ON DELETE SET NULL: Xóa danh mục không kéo theo xóa task
 *
 * Index:
 *  - [taskType]      : lọc nhanh theo loại task
 *  - [scheduledDate] : truy vấn task theo ngày (Daily Task)
 *  - [categoryId]    : lọc task theo danh mục
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL // Xóa danh mục → task không bị xóa, chỉ mất category
        )
    ],
    indices = [
        Index(value = ["task_type"]),
        Index(value = ["scheduled_date"]),
        Index(value = ["category_id"])
    ]
)
data class TaskEntity(

    // ── Trường chung cho cả 3 loại task ──────────────────────────────────────

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,                         // UUID string

    @ColumnInfo(name = "task_type")
    val taskType: String,                   // "HABIT" | "DAILY" | "ONE_TIME"

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "note")
    val note: String? = null,

    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,         // FK → categories.id

    @ColumnInfo(name = "priority")
    val priority: String = "MEDIUM",        // "HIGH" | "MEDIUM" | "LOW"

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,       // Trạng thái hôm nay

    @ColumnInfo(name = "created_at")
    val createdAt: Long,                    // Epoch milliseconds (LocalDateTime → Long)

    // ── Habit Task fields ─────────────────────────────────────────────────────

    /** "DAILY" hoặc "WEEKLY". NULL nếu không phải HABIT */
    @ColumnInfo(name = "recurrence_type")
    val recurrenceType: String? = null,

    /**
     * Danh sách ngày trong tuần dạng JSON string.
     * Ví dụ: "[1,3,5]" = Thứ Hai, Tư, Sáu
     * NULL nếu recurrenceType = "DAILY" hoặc task không phải HABIT
     */
    @ColumnInfo(name = "recurrence_days")
    val recurrenceDays: String? = null,

    /**
     * Giờ nhắc dạng "HH:mm" (ví dụ: "08:00").
     * NULL = không có nhắc nhở.
     * Dùng cho HABIT Task.
     */
    @ColumnInfo(name = "reminder_time")
    val reminderTime: String? = null,

    /** Streak hiện tại (số ngày liên tiếp hoàn thành). Chỉ có ý nghĩa với HABIT */
    @ColumnInfo(name = "current_streak")
    val currentStreak: Int = 0,

    /** Streak dài nhất từ trước đến nay (record cá nhân). Chỉ HABIT */
    @ColumnInfo(name = "longest_streak")
    val longestStreak: Int = 0,

    /** Mục tiêu thời lượng mỗi ngày (phút). NULL = không có mục tiêu. Chỉ HABIT */
    @ColumnInfo(name = "duration_goal_minutes")
    val durationGoalMinutes: Int? = null,

    /**
     * Ngày cuối cùng đã hoàn thành dạng "yyyy-MM-dd".
     * Dùng để tính streak khi mở app ngày hôm sau. Chỉ HABIT.
     */
    @ColumnInfo(name = "last_completed_date")
    val lastCompletedDate: String? = null,

    // ── Daily Task fields ─────────────────────────────────────────────────────

    /**
     * Ngày thực hiện task dạng "yyyy-MM-dd".
     * Dùng để truy vấn "task của hôm nay" và roll-over.
     * Chỉ DAILY Task.
     */
    @ColumnInfo(name = "scheduled_date")
    val scheduledDate: String? = null,

    /**
     * Giờ deadline trong ngày dạng "HH:mm".
     * NULL = không có deadline giờ cụ thể. Dùng cho DAILY Task.
     */
    @ColumnInfo(name = "deadline_time")
    val deadlineTime: String? = null,

    /** Số lần task đã bị roll-over sang hôm sau. Chỉ DAILY Task */
    @ColumnInfo(name = "roll_over_count")
    val rollOverCount: Int = 0,

    /**
     * Ngày gốc task được tạo ra (trước khi roll-over) dạng "yyyy-MM-dd".
     * Dùng để hiển thị badge "Từ hôm qua ⚠️" và thống kê task hay bị bỏ qua.
     */
    @ColumnInfo(name = "original_date")
    val originalDate: String? = null,

    // ── One-time Task fields ──────────────────────────────────────────────────

    /**
     * Deadline tuyệt đối dạng Epoch milliseconds.
     * NULL nếu không phải ONE_TIME Task.
     */
    @ColumnInfo(name = "deadline_date_time")
    val deadlineDateTime: Long? = null,

    /**
     * Nhắc trước deadline bao nhiêu phút.
     * Ví dụ: 60 = 1 giờ, 1440 = 1 ngày. NULL = không nhắc.
     */
    @ColumnInfo(name = "reminder_minutes_before")
    val reminderMinutesBefore: Int? = null,

    /**
     * Thời điểm hoàn thành thực tế (Epoch milliseconds).
     * NULL nếu chưa hoàn thành hoặc không phải ONE_TIME.
     */
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null
)
