package com.example.todolist.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) cho bảng `tasks`.
 *
 * Quy ước:
 *  - Các hàm trả về [Flow] được dùng cho UI (reactive, tự cập nhật khi DB thay đổi)
 *  - Các hàm suspend được dùng cho write operations và một lần đọc trong Use Case
 */
@Dao
interface TaskDao {

    // ── INSERT / UPDATE / DELETE ──────────────────────────────────────────────

    /**
     * Thêm task mới. Nếu trùng ID (trường hợp sync cloud) thì thay thế toàn bộ.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    /**
     * Thêm nhiều task cùng lúc (dùng khi restore từ Firebase lần đầu).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    /**
     * Cập nhật task (toàn bộ bản ghi).
     */
    @Update
    suspend fun updateTask(task: TaskEntity)

    /**
     * Xóa task khỏi DB. Các CompletionLog liên quan sẽ tự xóa theo (CASCADE).
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    /**
     * Xóa task theo ID (tiện dụng hơn khi không có object đầy đủ).
     */
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)

    // ── QUERY — HABIT TASKS ───────────────────────────────────────────────────

    /**
     * Lấy tất cả Habit Task (dùng để hiển thị section HABIT trên màn hình chính).
     * Sắp xếp: ưu tiên chưa hoàn thành lên trước, sau đó theo priority giảm dần.
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE task_type = 'HABIT' 
        ORDER BY is_completed ASC, 
                 CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END ASC
    """)
    fun observeHabitTasks(): Flow<List<TaskEntity>>

    /**
     * Lấy Habit Task cần hiển thị hôm nay theo ngày trong tuần.
     * [dayOfWeek]: 1 = Thứ Hai, ..., 7 = Chủ Nhật (ISO format)
     *
     * Logic:
     *  - recurrenceType = 'DAILY'  → luôn xuất hiện
     *  - recurrenceType = 'WEEKLY' → chỉ xuất hiện nếu [dayOfWeek] có trong [recurrenceDays]
     */
    @Query("""
        SELECT * FROM tasks
        WHERE task_type = 'HABIT'
          AND (
            recurrence_type = 'DAILY'
            OR (recurrence_type = 'WEEKLY' AND recurrence_days LIKE '%' || :dayOfWeek || '%')
          )
        ORDER BY is_completed ASC,
                 CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END ASC
    """)
    fun observeHabitTasksForDay(dayOfWeek: Int): Flow<List<TaskEntity>>

    // ── QUERY — DAILY TASKS ───────────────────────────────────────────────────

    /**
     * Lấy tất cả Daily Task của một ngày cụ thể.
     * [date]: dạng "yyyy-MM-dd"
     *
     * Task roll-over sẽ có [scheduledDate] = ngày hôm nay (đã được cập nhật bởi RollOverWorker).
     * Roll-over task phân biệt nhờ [rollOverCount] > 0.
     */
    @Query("""
        SELECT * FROM tasks
        WHERE task_type = 'DAILY' AND scheduled_date = :date
        ORDER BY roll_over_count DESC,
                 is_completed ASC,
                 CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END ASC,
                 deadline_time ASC
    """)
    fun observeDailyTasksForDate(date: String): Flow<List<TaskEntity>>

    /**
     * Lấy Daily Task chưa hoàn thành trước ngày [beforeDate] — dùng cho RollOverWorker.
     * [beforeDate]: dạng "yyyy-MM-dd"
     */
    @Query("""
        SELECT * FROM tasks
        WHERE task_type = 'DAILY' 
          AND is_completed = 0 
          AND scheduled_date < :beforeDate
    """)
    suspend fun getUncompletedDailyTasksBefore(beforeDate: String): List<TaskEntity>

    // ── QUERY — ONE-TIME TASKS ────────────────────────────────────────────────

    /**
     * Lấy tất cả One-time Task chưa hoàn thành, sắp xếp theo deadline tăng dần.
     * Dùng để hiển thị section ONE_TIME trên màn hình chính.
     */
    @Query("""
        SELECT * FROM tasks
        WHERE task_type = 'ONE_TIME' AND is_completed = 0
        ORDER BY deadline_date_time ASC
    """)
    fun observeActiveOneTimeTasks(): Flow<List<TaskEntity>>

    /**
     * Lấy One-time Task sắp đến hạn trong khoảng thời gian tới.
     * [fromEpoch]: thời điểm bắt đầu (thường là now)
     * [toEpoch]  : thời điểm kết thúc (thường là now + reminder window)
     * Dùng để lên lịch notification.
     */
    @Query("""
        SELECT * FROM tasks
        WHERE task_type = 'ONE_TIME' 
          AND is_completed = 0
          AND deadline_date_time BETWEEN :fromEpoch AND :toEpoch
    """)
    suspend fun getOneTimeTasksDueSoon(fromEpoch: Long, toEpoch: Long): List<TaskEntity>

    // ── QUERY — CHUNG ─────────────────────────────────────────────────────────

    /**
     * Lấy một task theo ID. Trả về null nếu không tồn tại.
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: String): TaskEntity?

    /**
     * Reactive version — observe một task theo ID.
     * UI sẽ tự cập nhật khi task bị sửa đổi.
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    fun observeTaskById(taskId: String): Flow<TaskEntity?>

    /**
     * Lấy tất cả task thuộc một danh mục cụ thể.
     */
    @Query("SELECT * FROM tasks WHERE category_id = :categoryId")
    fun observeTasksByCategory(categoryId: String): Flow<List<TaskEntity>>

    /**
     * Đếm số task hôm nay (dùng để tính completion rate trên header).
     * [date]: "yyyy-MM-dd"
     */
    @Query("""
        SELECT COUNT(*) FROM tasks
        WHERE (task_type = 'DAILY' AND scheduled_date = :date)
           OR task_type = 'HABIT'
           OR (task_type = 'ONE_TIME' AND is_completed = 0)
    """)
    fun observeTotalTaskCountForDate(date: String): Flow<Int>

    /**
     * Đếm số task đã hoàn thành hôm nay.
     * [date]: "yyyy-MM-dd"
     */
    @Query("""
        SELECT COUNT(*) FROM tasks
        WHERE is_completed = 1
          AND (
            (task_type = 'DAILY' AND scheduled_date = :date)
            OR task_type = 'HABIT'
            OR task_type = 'ONE_TIME'
          )
    """)
    fun observeCompletedTaskCountForDate(date: String): Flow<Int>

    /**
     * Reset trạng thái [isCompleted] của tất cả Habit Task về false mỗi ngày mới.
     * Được gọi bởi DailyRollOverWorker lúc đầu ngày (00:01).
     */
    @Query("UPDATE tasks SET is_completed = 0 WHERE task_type = 'HABIT'")
    suspend fun resetHabitTasksCompletion()

    /**
     * Lấy tất cả task để xuất lên Firebase khi bật sync lần đầu.
     */
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<TaskEntity>
}
