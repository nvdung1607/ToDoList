package com.example.todolist.domain.model

import java.time.LocalDateTime

/**
 * Domain model đại diện cho danh mục (nhãn) mà người dùng tự tạo.
 * Ví dụ: "Công việc", "Học tập", "Sức khỏe"
 *
 * @property id         ID duy nhất (UUID string)
 * @property name       Tên danh mục
 * @property colorHex   Màu sắc dạng hex (ví dụ: "#FF5733")
 * @property createdAt  Thời điểm tạo danh mục
 */
data class Category(
    val id: String,
    val name: String,
    val colorHex: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
