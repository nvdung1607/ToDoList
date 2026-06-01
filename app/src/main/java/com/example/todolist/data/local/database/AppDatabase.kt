package com.example.todolist.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolist.data.local.dao.CategoryDao
import com.example.todolist.data.local.dao.CompletionLogDao
import com.example.todolist.data.local.dao.ScoreDao
import com.example.todolist.data.local.dao.TaskDao
import com.example.todolist.data.local.entity.CategoryEntity
import com.example.todolist.data.local.entity.CompletionLogEntity
import com.example.todolist.data.local.entity.ScoreRecordEntity
import com.example.todolist.data.local.entity.TaskEntity

/**
 * Room Database chính của ứng dụng.
 *
 * @Database khai báo:
 *   - [entities]  : Tất cả các bảng được quản lý bởi Room
 *   - [version]   : Tăng lên mỗi khi schema thay đổi (cần thêm Migration tương ứng)
 *   - [exportSchema]: true = Room xuất schema JSON ra thư mục để version control
 *
 * Singleton pattern được đảm bảo bởi Hilt (xem DatabaseModule).
 * KHÔNG tạo instance trực tiếp — luôn dùng qua DI.
 */
@Database(
    entities = [
        TaskEntity::class,
        CategoryEntity::class,
        CompletionLogEntity::class,
        ScoreRecordEntity::class
    ],
    version = 1,
    exportSchema = true  // Schema JSON sẽ được lưu vào app/schemas/ để track migration
)
abstract class AppDatabase : RoomDatabase() {

    /** DAO để thao tác với bảng `tasks` */
    abstract fun taskDao(): TaskDao

    /** DAO để thao tác với bảng `categories` */
    abstract fun categoryDao(): CategoryDao

    /** DAO để thao tác với bảng `completion_logs` */
    abstract fun completionLogDao(): CompletionLogDao

    /** DAO để thao tác với bảng `score_records` */
    abstract fun scoreDao(): ScoreDao

    companion object {
        /** Tên file database trên thiết bị */
        const val DATABASE_NAME = "todolist.db"
    }
}
