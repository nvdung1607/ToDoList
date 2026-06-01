package com.example.todolist.domain.model

/**
 * Mức độ ưu tiên của task.
 * Được dùng để sắp xếp và lọc task trong danh sách.
 *
 * @property label  Nhãn hiển thị tiếng Việt
 * @property level  Giá trị số để so sánh / sắp xếp (cao hơn = ưu tiên hơn)
 */
enum class Priority(val label: String, val level: Int) {
    HIGH(label = "Cao", level = 3),
    MEDIUM(label = "Trung", level = 2),
    LOW(label = "Thấp", level = 1)
}
