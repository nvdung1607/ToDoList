package com.example.todolist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.data.local.entity.ScoreRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO cho bảng `score_records`.
 * Mỗi ngày có tối đa 1 bản ghi (date là UNIQUE index).
 */
@Dao
interface ScoreDao {

    /**
     * Thêm mới hoặc thay thế bản ghi điểm của một ngày.
     * REPLACE dùng vì ngày đã tồn tại thì cần update toàn bộ.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(record: ScoreRecordEntity)

    @Update
    suspend fun updateRecord(record: ScoreRecordEntity)

    /**
     * Lấy bản ghi điểm của một ngày cụ thể.
     * [date]: "yyyy-MM-dd". Trả về null nếu chưa có điểm ngày đó.
     */
    @Query("SELECT * FROM score_records WHERE date = :date LIMIT 1")
    suspend fun getRecordForDate(date: String): ScoreRecordEntity?

    /**
     * Reactive: observe bản ghi điểm hôm nay (dùng cho header màn hình chính).
     */
    @Query("SELECT * FROM score_records WHERE date = :date LIMIT 1")
    fun observeRecordForDate(date: String): Flow<ScoreRecordEntity?>

    /**
     * Lấy danh sách bản ghi điểm trong khoảng ngày.
     * [fromDate], [toDate]: "yyyy-MM-dd"
     * Dùng cho biểu đồ cột điểm theo tuần/tháng.
     */
    @Query("""
        SELECT * FROM score_records
        WHERE date BETWEEN :fromDate AND :toDate
        ORDER BY date ASC
    """)
    fun observeRecordsBetweenDates(fromDate: String, toDate: String): Flow<List<ScoreRecordEntity>>

    /**
     * Tổng điểm tích lũy trong khoảng ngày (dùng cho thống kê tuần/tháng/toàn thời gian).
     */
    @Query("""
        SELECT COALESCE(SUM(points_earned), 0)
        FROM score_records
        WHERE date BETWEEN :fromDate AND :toDate
    """)
    fun observeTotalPointsBetweenDates(fromDate: String, toDate: String): Flow<Int>

    /**
     * Tổng điểm toàn thời gian.
     */
    @Query("SELECT COALESCE(SUM(points_earned), 0) FROM score_records")
    fun observeAllTimePoints(): Flow<Int>

    /**
     * Lấy tất cả bản ghi (dùng khi sync lên Firebase).
     */
    @Query("SELECT * FROM score_records ORDER BY date DESC")
    suspend fun getAllRecords(): List<ScoreRecordEntity>
}
