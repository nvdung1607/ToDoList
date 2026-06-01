package com.example.todolist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity — bảng `completion_logs`.
 * Ghi lại từng lần người dùng tick hoàn thành một task.
 *
 * Đây là nguồn dữ liệu cho:
 *   - Màn hình lịch sử (task đã xong theo ngày/tuần/tháng)
 *   - Heatmap hoạt động (đếm số log mỗi ngày)
 *   - Bảng streak Habit Task
 *
 * Foreign Key:
 *   - [taskId] → bảng `tasks` ([TaskEntity.id])
 *     ON DELETE CASCADE: Xóa task thì xóa luôn toàn bộ log của task đó
 *
 * Index:
 *   - [completedDate]: group by ngày để render heatmap và lịch sử rất nhanh
 *   - [taskId]       : tra cứu lịch sử của 1 task cụ thể
 *
 * @property id                 UUID string
 * @property taskId             ID task (FK → tasks.id)
 * @property taskTitle          Snapshot tên task tại thời điểm hoàn thành
 * @property taskType           Loại task: "HABIT" | "DAILY" | "ONE_TIME"
 * @property completedAt        Epoch milliseconds lúc hoàn thành
 * @property completedDate      Ngày hoàn thành dạng "yyyy-MM-dd" (denormalized để query nhanh)
 * @property pointsGained       Điểm thực nhận (đã áp dụng streak multiplier)
 * @property streakAtCompletion Streak tại thời điểm hoàn thành (snapshot, chỉ HABIT)
 */
@Entity(
    tableName = "completion_logs",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["completed_date"]),
        Index(value = ["task_id"])
    ]
)
data class CompletionLogEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "task_id")
    val taskId: String,

    @ColumnInfo(name = "task_title")
    val taskTitle: String,

    @ColumnInfo(name = "task_type")
    val taskType: String,

    @ColumnInfo(name = "completed_at")
    val completedAt: Long,

    /** Denormalized "yyyy-MM-dd" để WHERE completedDate = '2026-06-01' không cần parse Long */
    @ColumnInfo(name = "completed_date")
    val completedDate: String,

    @ColumnInfo(name = "points_gained")
    val pointsGained: Int,

    @ColumnInfo(name = "streak_at_completion")
    val streakAtCompletion: Int = 0
)
