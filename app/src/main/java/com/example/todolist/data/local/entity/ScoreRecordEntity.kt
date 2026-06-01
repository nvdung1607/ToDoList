package com.example.todolist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity — bảng `score_records`.
 * Tổng hợp điểm số và tỷ lệ hoàn thành theo từng ngày.
 * Được cập nhật mỗi khi người dùng tick hoàn thành task hoặc hệ thống chạy roll-over.
 *
 * Dùng cho:
 *   - Biểu đồ điểm tích lũy theo tuần/tháng
 *   - Biểu đồ tỷ lệ hoàn thành
 *   - Tổng điểm toàn thời gian
 *
 * Index:
 *   - [date]: truy vấn điểm theo khoảng ngày rất thường xuyên
 *
 * @property id             UUID string
 * @property date           Ngày dạng "yyyy-MM-dd" (UNIQUE — mỗi ngày chỉ có 1 bản ghi)
 * @property pointsEarned   Tổng điểm kiếm được trong ngày
 * @property tasksCompleted Số task đã hoàn thành
 * @property tasksTotal     Tổng số task có trong ngày
 */
@Entity(
    tableName = "score_records",
    indices = [Index(value = ["date"], unique = true)]
)
data class ScoreRecordEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    /** Ngày ghi nhận, dạng "yyyy-MM-dd". UNIQUE — mỗi ngày chỉ có 1 dòng */
    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "points_earned")
    val pointsEarned: Int = 0,

    @ColumnInfo(name = "tasks_completed")
    val tasksCompleted: Int = 0,

    @ColumnInfo(name = "tasks_total")
    val tasksTotal: Int = 0
)
