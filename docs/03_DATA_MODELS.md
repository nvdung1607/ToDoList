# 03 — Data Models

Tài liệu này định nghĩa toàn bộ data models trong app, chia theo 3 tầng:
1. **Domain Models** — Business entities (thuần Kotlin, không phụ thuộc Android)
2. **Room Entities** — Bảng SQLite (Data layer)
3. **DAOs** — Truy vấn database
4. **Mappers** — Chuyển đổi giữa các tầng

---

## 1. Domain Models

### 1.1 Enums

```kotlin
// TaskType.kt
enum class TaskType { HABIT, DAILY, ONE_TIME }

// Priority.kt
enum class Priority(val label: String, val level: Int) {
    HIGH(label = "Cao", level = 3),
    MEDIUM(label = "Trung", level = 2),
    LOW(label = "Thấp", level = 1)
}

// RecurrenceType.kt (trong RecurrenceConfig.kt)
enum class RecurrenceType { DAILY, WEEKLY }

// ThemeMode (trong UserPreferences.kt)
enum class ThemeMode { LIGHT, DARK, SYSTEM }

// SortOrder (trong UserPreferences.kt)
enum class SortOrder { PRIORITY, DEADLINE, CATEGORY, MANUAL }
```

### 1.2 RecurrenceConfig

```kotlin
// RecurrenceConfig.kt
data class RecurrenceConfig(
    val type: RecurrenceType,
    val daysOfWeek: List<Int> = emptyList()
    // 1=Thứ Hai, 2=Thứ Ba, ..., 7=Chủ Nhật (ISO)
    // Ví dụ: [1,3,5] = Thứ Hai, Tư, Sáu
)
```

### 1.3 Task (Sealed Class — Core Model)

```kotlin
// Task.kt
sealed class Task {
    // Thuộc tính chung
    abstract val id: String           // UUID string
    abstract val title: String
    abstract val note: String?
    abstract val categoryId: String?  // null = không có danh mục
    abstract val priority: Priority
    abstract val isCompleted: Boolean
    abstract val createdAt: LocalDateTime

    // ── HABIT TASK ──────────────────────────────────────────
    data class Habit(
        override val id: String,
        override val title: String,
        override val note: String? = null,
        override val categoryId: String? = null,
        override val priority: Priority = Priority.MEDIUM,
        override val isCompleted: Boolean = false,
        override val createdAt: LocalDateTime = LocalDateTime.now(),
        val recurrence: RecurrenceConfig = RecurrenceConfig(RecurrenceType.DAILY),
        val reminderTime: LocalTime? = null,      // null = không nhắc
        val currentStreak: Int = 0,
        val longestStreak: Int = 0,
        val durationGoalMinutes: Int? = null,     // null = không có mục tiêu
        val lastCompletedDate: LocalDate? = null  // để tính streak ngày hôm sau
    ) : Task()

    // ── DAILY TASK ──────────────────────────────────────────
    data class Daily(
        override val id: String,
        override val title: String,
        override val note: String? = null,
        override val categoryId: String? = null,
        override val priority: Priority = Priority.MEDIUM,
        override val isCompleted: Boolean = false,
        override val createdAt: LocalDateTime = LocalDateTime.now(),
        val scheduledDate: LocalDate = LocalDate.now(),
        val deadlineTime: LocalTime? = null,      // null = không có deadline giờ
        val rollOverCount: Int = 0,               // số lần bị đẩy sang hôm sau
        val originalDate: LocalDate = LocalDate.now()
    ) : Task()

    // ── ONE-TIME TASK ────────────────────────────────────────
    data class OneTime(
        override val id: String,
        override val title: String,
        override val note: String? = null,
        override val categoryId: String? = null,
        override val priority: Priority = Priority.MEDIUM,
        override val isCompleted: Boolean = false,
        override val createdAt: LocalDateTime = LocalDateTime.now(),
        val deadline: LocalDateTime,
        val reminderMinutesBefore: Int? = null,   // null = không nhắc
        // 60=1h, 1440=1 ngày
        val completedAt: LocalDateTime? = null
    ) : Task()
}
```

### 1.4 Category

```kotlin
// Category.kt
data class Category(
    val id: String,           // UUID
    val name: String,
    val colorHex: String,     // "#FF5733"
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

### 1.5 ScoreRecord

```kotlin
// ScoreRecord.kt
data class ScoreRecord(
    val id: String,
    val date: LocalDate,
    val pointsEarned: Int,
    val tasksCompleted: Int,
    val tasksTotal: Int
) {
    val completionRate: Float
        get() = if (tasksTotal == 0) 0f else tasksCompleted.toFloat() / tasksTotal.toFloat()
}
```

### 1.6 CompletionLog

```kotlin
// CompletionLog.kt
data class CompletionLog(
    val id: String,
    val taskId: String,
    val taskTitle: String,    // snapshot tên task tại thời điểm hoàn thành
    val taskType: TaskType,
    val completedAt: LocalDateTime,
    val pointsGained: Int,    // đã áp streak multiplier
    val streakAtCompletion: Int = 0
) {
    val completedDate: LocalDate get() = completedAt.toLocalDate()
}
```

### 1.7 Dashboard Stats Models

```kotlin
// DashboardStats.kt

data class DailyStats(
    val date: LocalDate,
    val totalTasks: Int,
    val completedTasks: Int,
    val totalPoints: Int,
    val habitCompletions: Int = 0,
    val dailyCompletions: Int = 0,
    val oneTimeCompletions: Int = 0
) {
    val completionRate: Float
        get() = if (totalTasks == 0) 0f else completedTasks.toFloat() / totalTasks.toFloat()
    val completionPercent: Int get() = (completionRate * 100).toInt()
}

data class HeatmapEntry(
    val date: LocalDate,
    val activityCount: Int    // số task hoàn thành — càng nhiều màu càng đậm
)

data class HabitStreakStat(
    val taskId: String,
    val taskTitle: String,
    val currentStreak: Int,
    val longestStreak: Int
)

data class SkippedTaskStat(
    val taskId: String,
    val taskTitle: String,
    val rollOverCount: Int,
    val taskType: TaskType
)
```

### 1.8 UserPreferences

```kotlin
// UserPreferences.kt
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val habitNotificationsEnabled: Boolean = true,
    val dailyEveningReminderEnabled: Boolean = true,
    val eveningReminderTime: String = "21:00",  // "HH:mm"
    val taskSortOrder: SortOrder = SortOrder.PRIORITY
)
```

---

## 2. Room Entities

### 2.1 TaskEntity — bảng `tasks`

**Chiến lược: Single Table Inheritance** — 1 bảng cho cả 3 loại task.
Cột không liên quan đến loại task sẽ là `NULL`.

```
Indexes: task_type, scheduled_date, category_id
Foreign Key: category_id → categories.id (ON DELETE SET NULL)
```

| Cột | Kiểu | Dùng cho | Ghi chú |
|---|---|---|---|
| `id` | TEXT PK | All | UUID string |
| `task_type` | TEXT | All | "HABIT" / "DAILY" / "ONE_TIME" |
| `title` | TEXT | All | Bắt buộc |
| `note` | TEXT? | All | Tuỳ chọn |
| `category_id` | TEXT? FK | All | SET NULL khi xóa category |
| `priority` | TEXT | All | "HIGH" / "MEDIUM" / "LOW" |
| `is_completed` | INTEGER | All | Boolean (0/1) |
| `created_at` | INTEGER | All | Epoch milliseconds |
| `recurrence_type` | TEXT? | HABIT | "DAILY" / "WEEKLY" |
| `recurrence_days` | TEXT? | HABIT | JSON "[1,3,5]" |
| `reminder_time` | TEXT? | HABIT | "HH:mm" |
| `current_streak` | INTEGER | HABIT | Default 0 |
| `longest_streak` | INTEGER | HABIT | Default 0 |
| `duration_goal_minutes` | INTEGER? | HABIT | Phút |
| `last_completed_date` | TEXT? | HABIT | "yyyy-MM-dd" |
| `scheduled_date` | TEXT? | DAILY | "yyyy-MM-dd" [INDEX] |
| `deadline_time` | TEXT? | DAILY | "HH:mm" |
| `roll_over_count` | INTEGER | DAILY | Default 0 |
| `original_date` | TEXT? | DAILY | "yyyy-MM-dd" |
| `deadline_date_time` | INTEGER? | ONE_TIME | Epoch ms |
| `reminder_minutes_before` | INTEGER? | ONE_TIME | Phút trước deadline |
| `completed_at` | INTEGER? | ONE_TIME | Epoch ms |

### 2.2 CategoryEntity — bảng `categories`

| Cột | Kiểu | Ghi chú |
|---|---|---|
| `id` | TEXT PK | UUID |
| `name` | TEXT | Tên danh mục |
| `color_hex` | TEXT | "#RRGGBB" |
| `created_at` | INTEGER | Epoch ms |

### 2.3 CompletionLogEntity — bảng `completion_logs`

```
Foreign Key: task_id → tasks.id (ON DELETE CASCADE)
Indexes: completed_date, task_id
```

| Cột | Kiểu | Ghi chú |
|---|---|---|
| `id` | TEXT PK | UUID |
| `task_id` | TEXT FK | CASCADE delete |
| `task_title` | TEXT | Snapshot tên task |
| `task_type` | TEXT | "HABIT" / "DAILY" / "ONE_TIME" |
| `completed_at` | INTEGER | Epoch ms |
| `completed_date` | TEXT [INDEX] | "yyyy-MM-dd" (denormalized) |
| `points_gained` | INTEGER | Sau khi áp streak bonus |
| `streak_at_completion` | INTEGER | Chỉ HABIT, default 0 |

### 2.4 ScoreRecordEntity — bảng `score_records`

```
Index: date (UNIQUE) — mỗi ngày chỉ có 1 bản ghi
```

| Cột | Kiểu | Ghi chú |
|---|---|---|
| `id` | TEXT PK | UUID |
| `date` | TEXT [UNIQUE] | "yyyy-MM-dd" |
| `points_earned` | INTEGER | Default 0 |
| `tasks_completed` | INTEGER | Default 0 |
| `tasks_total` | INTEGER | Default 0 |

---

## 3. DAOs (Data Access Objects)

### 3.1 TaskDao — interface `TaskDao`

| Method | Return | Mô tả |
|---|---|---|
| `insertTask(task)` | `suspend Unit` | Insert or REPLACE |
| `insertTasks(tasks)` | `suspend Unit` | Batch insert |
| `updateTask(task)` | `suspend Unit` | Full update |
| `deleteTaskById(id)` | `suspend Unit` | Xóa theo ID |
| `observeHabitTasksForDay(dayOfWeek)` | `Flow<List>` | Habit task hôm nay |
| `observeDailyTasksForDate(date)` | `Flow<List>` | Daily task 1 ngày |
| `observeActiveOneTimeTasks()` | `Flow<List>` | Chưa hoàn thành, sort deadline |
| `getUncompletedDailyTasksBefore(date)` | `suspend List` | Cho roll-over |
| `getOneTimeTasksDueSoon(from, to)` | `suspend List` | Cho notification |
| `getTaskById(id)` | `suspend Task?` | One-shot |
| `observeTaskById(id)` | `Flow<Task?>` | Reactive |
| `observeTasksByCategory(categoryId)` | `Flow<List>` | Lọc theo category |
| `resetHabitTasksCompletion()` | `suspend Unit` | Reset về false (đầu ngày) |
| `getAllTasks()` | `suspend List` | Export lên Firebase |

### 3.2 CategoryDao

| Method | Return | Mô tả |
|---|---|---|
| `insertCategory(cat)` | `suspend Unit` | Insert or REPLACE |
| `updateCategory(cat)` | `suspend Unit` | |
| `deleteCategory(cat)` | `suspend Unit` | SET NULL cho tasks |
| `observeAllCategories()` | `Flow<List>` | Sort A→Z |
| `getCategoryById(id)` | `suspend Cat?` | |
| `countByName(name)` | `suspend Int` | Validate duplicate |

### 3.3 CompletionLogDao

| Method | Return | Mô tả |
|---|---|---|
| `insertLog(log)` | `suspend Unit` | IGNORE nếu duplicate |
| `observeLogsForDate(date)` | `Flow<List>` | Lịch sử 1 ngày |
| `observeLogsBetweenDates(from, to)` | `Flow<List>` | Lịch sử tuần/tháng |
| `getActivityCountByDate(from, to)` | `suspend List<DateActivityCount>` | Heatmap data |
| `getHabitTaskLogs(taskId)` | `suspend List` | Tính streak |
| `observeTotalPointsBetweenDates(from, to)` | `Flow<Int>` | Điểm trong khoảng |

```kotlin
// Projection class cho GROUP BY query
data class DateActivityCount(
    val completed_date: String,
    val count: Int
)
```

### 3.4 ScoreDao

| Method | Return | Mô tả |
|---|---|---|
| `insertOrReplace(record)` | `suspend Unit` | Upsert |
| `getRecordForDate(date)` | `suspend Record?` | One-shot |
| `observeRecordForDate(date)` | `Flow<Record?>` | Reactive (Home header) |
| `observeRecordsBetweenDates(from, to)` | `Flow<List>` | Biểu đồ |
| `observeTotalPointsBetweenDates(from, to)` | `Flow<Int>` | Tổng điểm |
| `observeAllTimePoints()` | `Flow<Int>` | All-time tổng |

---

## 4. Mappers

### TaskMapper.kt
```
TaskEntity → Task (toDomain())
  ├── HABIT → Task.Habit (parse recurrenceDays JSON, reminderTime "HH:mm", dates "yyyy-MM-dd")
  ├── DAILY → Task.Daily
  └── ONE_TIME → Task.OneTime

Task → TaskEntity (toEntity())
  ├── Task.Habit → TaskEntity (serialize recurrenceDays → JSON, LocalTime → "HH:mm")
  ├── Task.Daily → TaskEntity
  └── Task.OneTime → TaskEntity

Helper: epochToLocalDateTime(Long) / localDateTimeToEpoch(LocalDateTime)
```

### EntityMappers.kt
```
CategoryEntity ↔ Category
CompletionLogEntity ↔ CompletionLog
ScoreRecordEntity ↔ ScoreRecord
```

---

## 5. Score Calculation Logic

```kotlin
// ScoreCalculator.kt
fun calculateScore(taskType: TaskType, currentStreak: Int): Int {
    val base = when (taskType) {
        TaskType.HABIT    -> 15
        TaskType.DAILY    -> 10
        TaskType.ONE_TIME -> 20
    }
    val multiplier = when (currentStreak) {
        in 0..2   -> 1.0f
        in 3..6   -> 1.5f
        in 7..13  -> 2.0f
        in 14..29 -> 2.5f
        else      -> 3.0f  // 30+
    }
    return (base * multiplier).toInt()
}
```

---

## 6. AppDatabase

```kotlin
@Database(
    entities = [TaskEntity::class, CategoryEntity::class,
                CompletionLogEntity::class, ScoreRecordEntity::class],
    version = 1,
    exportSchema = true  // lưu schema JSON vào app/schemas/
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun completionLogDao(): CompletionLogDao
    abstract fun scoreDao(): ScoreDao

    companion object {
        const val DATABASE_NAME = "todolist.db"
    }
}
```
