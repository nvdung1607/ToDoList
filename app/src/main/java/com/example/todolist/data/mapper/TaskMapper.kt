package com.example.todolist.data.mapper

import com.example.todolist.data.local.entity.TaskEntity
import com.example.todolist.domain.model.Priority
import com.example.todolist.domain.model.RecurrenceConfig
import com.example.todolist.domain.model.RecurrenceType
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.model.TaskType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Mapper chuyển đổi giữa [TaskEntity] (Room) và [Task] (Domain model).
 *
 * Hai chiều:
 *  - [TaskEntity.toDomain]  : Entity → Domain (đọc từ DB, trước khi gửi lên ViewModel)
 *  - [Task.toEntity]        : Domain → Entity (trước khi lưu xuống DB)
 */

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
private val ZONE = ZoneId.systemDefault()

// ─────────────────────────────────────────────────────────────────────────────
// Entity → Domain
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Chuyển [TaskEntity] thành sealed class [Task] tương ứng với [taskType].
 * Ném [IllegalStateException] nếu [taskType] không hợp lệ (không nên xảy ra ở production).
 */
fun TaskEntity.toDomain(): Task {
    return when (TaskType.valueOf(taskType)) {
        TaskType.HABIT -> toHabitTask()
        TaskType.DAILY -> toDailyTask()
        TaskType.ONE_TIME -> toOneTimeTask()
    }
}

private fun TaskEntity.toHabitTask(): Task.Habit {
    // Parse recurrence days từ JSON string "[1,3,5]" → List<Int>
    val days = recurrenceDays
        ?.trim('[', ']')
        ?.split(",")
        ?.mapNotNull { it.trim().toIntOrNull() }
        ?: emptyList()

    return Task.Habit(
        id = id,
        title = title,
        note = note,
        categoryId = categoryId,
        priority = Priority.valueOf(priority),
        isCompleted = isCompleted,
        createdAt = epochToLocalDateTime(createdAt),
        recurrence = RecurrenceConfig(
            type = recurrenceType?.let { RecurrenceType.valueOf(it) } ?: RecurrenceType.DAILY,
            daysOfWeek = days
        ),
        reminderTime = reminderTime?.let { LocalTime.parse(it, TIME_FORMATTER) },
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        durationGoalMinutes = durationGoalMinutes,
        lastCompletedDate = lastCompletedDate?.let { LocalDate.parse(it, DATE_FORMATTER) }
    )
}

private fun TaskEntity.toDailyTask(): Task.Daily = Task.Daily(
    id = id,
    title = title,
    note = note,
    categoryId = categoryId,
    priority = Priority.valueOf(priority),
    isCompleted = isCompleted,
    createdAt = epochToLocalDateTime(createdAt),
    scheduledDate = scheduledDate?.let { LocalDate.parse(it, DATE_FORMATTER) } ?: LocalDate.now(),
    deadlineTime = deadlineTime?.let { LocalTime.parse(it, TIME_FORMATTER) },
    rollOverCount = rollOverCount,
    originalDate = originalDate?.let { LocalDate.parse(it, DATE_FORMATTER) } ?: LocalDate.now()
)

private fun TaskEntity.toOneTimeTask(): Task.OneTime = Task.OneTime(
    id = id,
    title = title,
    note = note,
    categoryId = categoryId,
    priority = Priority.valueOf(priority),
    isCompleted = isCompleted,
    createdAt = epochToLocalDateTime(createdAt),
    deadline = epochToLocalDateTime(deadlineDateTime ?: createdAt),
    reminderMinutesBefore = reminderMinutesBefore,
    completedAt = completedAt?.let { epochToLocalDateTime(it) }
)

// ─────────────────────────────────────────────────────────────────────────────
// Domain → Entity
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Chuyển [Task] sealed class thành [TaskEntity] để lưu vào Room.
 */
fun Task.toEntity(): TaskEntity = when (this) {
    is Task.Habit -> this.toEntity()
    is Task.Daily -> this.toEntity()
    is Task.OneTime -> this.toEntity()
}

private fun Task.Habit.toEntity() = TaskEntity(
    id = id,
    taskType = TaskType.HABIT.name,
    title = title,
    note = note,
    categoryId = categoryId,
    priority = priority.name,
    isCompleted = isCompleted,
    createdAt = localDateTimeToEpoch(createdAt),
    // Habit-specific
    recurrenceType = recurrence.type.name,
    recurrenceDays = if (recurrence.daysOfWeek.isEmpty()) null
                     else "[${recurrence.daysOfWeek.joinToString(",")}]",
    reminderTime = reminderTime?.format(TIME_FORMATTER),
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    durationGoalMinutes = durationGoalMinutes,
    lastCompletedDate = lastCompletedDate?.format(DATE_FORMATTER)
)

private fun Task.Daily.toEntity() = TaskEntity(
    id = id,
    taskType = TaskType.DAILY.name,
    title = title,
    note = note,
    categoryId = categoryId,
    priority = priority.name,
    isCompleted = isCompleted,
    createdAt = localDateTimeToEpoch(createdAt),
    // Daily-specific
    scheduledDate = scheduledDate.format(DATE_FORMATTER),
    deadlineTime = deadlineTime?.format(TIME_FORMATTER),
    rollOverCount = rollOverCount,
    originalDate = originalDate.format(DATE_FORMATTER)
)

private fun Task.OneTime.toEntity() = TaskEntity(
    id = id,
    taskType = TaskType.ONE_TIME.name,
    title = title,
    note = note,
    categoryId = categoryId,
    priority = priority.name,
    isCompleted = isCompleted,
    createdAt = localDateTimeToEpoch(createdAt),
    // One-time specific
    deadlineDateTime = localDateTimeToEpoch(deadline),
    reminderMinutesBefore = reminderMinutesBefore,
    completedAt = completedAt?.let { localDateTimeToEpoch(it) }
)

// ─────────────────────────────────────────────────────────────────────────────
// Helpers — Epoch ↔ LocalDateTime
// ─────────────────────────────────────────────────────────────────────────────

private fun epochToLocalDateTime(epoch: Long): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZONE)

private fun localDateTimeToEpoch(dt: LocalDateTime): Long =
    dt.atZone(ZONE).toInstant().toEpochMilli()
