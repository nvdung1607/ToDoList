package com.example.todolist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.todolist.data.local.entity.CompletionLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO cho bảng `completion_logs`.
 * Chỉ có INSERT và SELECT — log không bao giờ được sửa, chỉ xóa theo CASCADE khi task bị xóa.
 */
@Dao
interface CompletionLogDao {

    /**
     * Ghi log một lần hoàn thành task mới.
     * IGNORE: tránh duplicate nếu người dùng tick 2 lần nhanh.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLog(log: CompletionLogEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLogs(logs: List<CompletionLogEntity>)

    // ── Lịch sử hoàn thành ───────────────────────────────────────────────────

    /**
     * Lấy tất cả log trong một ngày cụ thể.
     * [date]: "yyyy-MM-dd"
     * Sắp xếp: mới nhất lên trước.
     */
    @Query("""
        SELECT * FROM completion_logs 
        WHERE completed_date = :date 
        ORDER BY completed_at DESC
    """)
    fun observeLogsForDate(date: String): Flow<List<CompletionLogEntity>>

    /**
     * Lấy log trong khoảng ngày [fromDate] đến [toDate] (inclusive).
     * Dùng cho lịch sử tuần / tháng.
     * [fromDate], [toDate]: "yyyy-MM-dd"
     */
    @Query("""
        SELECT * FROM completion_logs
        WHERE completed_date BETWEEN :fromDate AND :toDate
        ORDER BY completed_at DESC
    """)
    fun observeLogsBetweenDates(fromDate: String, toDate: String): Flow<List<CompletionLogEntity>>

    // ── Heatmap ───────────────────────────────────────────────────────────────

    /**
     * Đếm số lần hoàn thành theo từng ngày trong khoảng thời gian.
     * Kết quả dùng để vẽ heatmap (ngày nhiều task → màu đậm hơn).
     * [fromDate], [toDate]: "yyyy-MM-dd"
     */
    @Query("""
        SELECT completed_date, COUNT(*) as count
        FROM completion_logs
        WHERE completed_date BETWEEN :fromDate AND :toDate
        GROUP BY completed_date
        ORDER BY completed_date ASC
    """)
    suspend fun getActivityCountByDate(fromDate: String, toDate: String): List<DateActivityCount>

    // ── Streak ────────────────────────────────────────────────────────────────

    /**
     * Lấy lịch sử hoàn thành của một Habit Task cụ thể, sắp xếp theo ngày giảm dần.
     * Dùng để tính streak hiện tại (kiểm tra xem có hoàn thành liên tiếp không).
     * [taskId]: ID của Habit Task
     */
    @Query("""
        SELECT * FROM completion_logs
        WHERE task_id = :taskId AND task_type = 'HABIT'
        ORDER BY completed_date DESC
    """)
    suspend fun getHabitTaskLogs(taskId: String): List<CompletionLogEntity>

    // ── Task hay bị bỏ qua ────────────────────────────────────────────────────

    /**
     * Không thể tính "task hay bị roll-over nhất" trực tiếp từ log.
     * Query này lấy top N task DAILY có roll_over_count cao nhất từ bảng tasks.
     * Được đặt ở đây để tiện import, nhưng thực tế join với TaskEntity.
     *
     * Sử dụng [TaskDao.observeTasksByCategory] hoặc SkippedTaskStat Use Case sẽ
     * query trực tiếp từ TaskDao.
     */

    /**
     * Tổng điểm kiếm được trong khoảng ngày [fromDate] → [toDate].
     * Dùng cho biểu đồ điểm tích lũy.
     */
    @Query("""
        SELECT COALESCE(SUM(points_gained), 0)
        FROM completion_logs
        WHERE completed_date BETWEEN :fromDate AND :toDate
    """)
    fun observeTotalPointsBetweenDates(fromDate: String, toDate: String): Flow<Int>

    /**
     * Lấy tất cả log (dùng khi sync lên Firebase).
     */
    @Query("SELECT * FROM completion_logs ORDER BY completed_at DESC")
    suspend fun getAllLogs(): List<CompletionLogEntity>
}

/**
 * Data class trung gian cho query GROUP BY ngày — dùng cho heatmap.
 * Room sẽ tự map kết quả SQL vào class này.
 */
data class DateActivityCount(
    val completed_date: String, // "yyyy-MM-dd"
    val count: Int
)
