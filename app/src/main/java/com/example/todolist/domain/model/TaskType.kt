package com.example.todolist.domain.model

/**
 * Enum đại diện cho 3 loại task trong ứng dụng.
 *
 * - HABIT     : Thói quen lặp lại hàng ngày / theo ngày trong tuần (ví dụ: học tiếng Anh mỗi ngày)
 * - DAILY     : Công việc trong ngày, có thể roll-over sang ngày hôm sau nếu chưa hoàn thành
 * - ONE_TIME  : Việc có deadline cụ thể, chỉ xuất hiện 1 lần
 */
enum class TaskType {
    HABIT,
    DAILY,
    ONE_TIME
}
