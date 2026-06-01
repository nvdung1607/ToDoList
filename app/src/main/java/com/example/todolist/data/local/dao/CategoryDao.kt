package com.example.todolist.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO cho bảng `categories`.
 */
@Dao
interface CategoryDao {

    /**
     * Thêm hoặc thay thế danh mục (dùng khi sync từ cloud).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    /**
     * Xóa danh mục. Các task liên quan sẽ có [categoryId] = NULL (SET_NULL cascade).
     */
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    /**
     * Lấy tất cả danh mục, sắp xếp theo tên A→Z.
     * Reactive: UI tự cập nhật khi danh mục thay đổi.
     */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun observeAllCategories(): Flow<List<CategoryEntity>>

    /**
     * Lấy danh mục theo ID.
     */
    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: String): CategoryEntity?

    /**
     * Kiểm tra tên danh mục đã tồn tại chưa (để validate khi tạo mới).
     */
    @Query("SELECT COUNT(*) FROM categories WHERE name = :name")
    suspend fun countByName(name: String): Int

    /**
     * Lấy tất cả danh mục (one-shot, dùng khi export lên Firebase).
     */
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>
}
