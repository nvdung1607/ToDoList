package com.example.todolist.domain.model

/**
 * Chu kỳ lặp lại của Habit Task.
 *
 * - DAILY       : Lặp lại mỗi ngày trong tuần
 * - WEEKLY      : Lặp lại vào các ngày cụ thể trong tuần (lưu trong [daysOfWeek])
 *
 * @property daysOfWeek  Danh sách ngày trong tuần áp dụng (1=Thứ Hai ... 7=Chủ Nhật).
 *                       Chỉ có nghĩa khi type = WEEKLY. Rỗng nếu type = DAILY.
 */
data class RecurrenceConfig(
    val type: RecurrenceType,
    val daysOfWeek: List<Int> = emptyList() // Ví dụ: [1, 3, 5] = Thứ Hai, Tư, Sáu
)

/**
 * Kiểu lặp lại.
 */
enum class RecurrenceType {
    DAILY,  // Mỗi ngày
    WEEKLY  // Chỉ các ngày được chọn trong tuần
}
