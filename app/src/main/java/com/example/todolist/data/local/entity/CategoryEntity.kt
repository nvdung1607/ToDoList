package com.example.todolist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity — bảng `categories`.
 * Lưu các danh mục người dùng tự tạo (Công việc, Học tập, Sức khỏe...).
 *
 * @property id        UUID string, khoá chính
 * @property name      Tên danh mục (duy nhất, không được trùng)
 * @property colorHex  Màu sắc đại diện dạng hex (ví dụ: "#FF5733")
 * @property createdAt Epoch milliseconds lúc tạo
 */
@Entity(tableName = "categories")
data class CategoryEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color_hex")
    val colorHex: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
