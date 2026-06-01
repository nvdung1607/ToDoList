package com.example.todolist.data.mapper

import com.example.todolist.data.local.entity.CategoryEntity
import com.example.todolist.data.local.entity.CompletionLogEntity
import com.example.todolist.data.local.entity.ScoreRecordEntity
import com.example.todolist.domain.model.Category
import com.example.todolist.domain.model.CompletionLog
import com.example.todolist.domain.model.ScoreRecord
import com.example.todolist.domain.model.TaskType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val ZONE = ZoneId.systemDefault()

// ─────────────────────────────────────────────────────────────────────────────
// Category Mapper
// ─────────────────────────────────────────────────────────────────────────────

/** [CategoryEntity] → [Category] domain model */
fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    colorHex = colorHex,
    createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZONE)
)

/** [Category] domain model → [CategoryEntity] */
fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    colorHex = colorHex,
    createdAt = createdAt.atZone(ZONE).toInstant().toEpochMilli()
)

// ─────────────────────────────────────────────────────────────────────────────
// CompletionLog Mapper
// ─────────────────────────────────────────────────────────────────────────────

/** [CompletionLogEntity] → [CompletionLog] domain model */
fun CompletionLogEntity.toDomain(): CompletionLog = CompletionLog(
    id = id,
    taskId = taskId,
    taskTitle = taskTitle,
    taskType = TaskType.valueOf(taskType),
    completedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(completedAt), ZONE),
    pointsGained = pointsGained,
    streakAtCompletion = streakAtCompletion
)

/** [CompletionLog] domain model → [CompletionLogEntity] */
fun CompletionLog.toEntity(): CompletionLogEntity = CompletionLogEntity(
    id = id,
    taskId = taskId,
    taskTitle = taskTitle,
    taskType = taskType.name,
    completedAt = completedAt.atZone(ZONE).toInstant().toEpochMilli(),
    completedDate = completedAt.toLocalDate().format(DATE_FORMATTER),
    pointsGained = pointsGained,
    streakAtCompletion = streakAtCompletion
)

// ─────────────────────────────────────────────────────────────────────────────
// ScoreRecord Mapper
// ─────────────────────────────────────────────────────────────────────────────

/** [ScoreRecordEntity] → [ScoreRecord] domain model */
fun ScoreRecordEntity.toDomain(): ScoreRecord = ScoreRecord(
    id = id,
    date = LocalDate.parse(date, DATE_FORMATTER),
    pointsEarned = pointsEarned,
    tasksCompleted = tasksCompleted,
    tasksTotal = tasksTotal
)

/** [ScoreRecord] domain model → [ScoreRecordEntity] */
fun ScoreRecord.toEntity(): ScoreRecordEntity = ScoreRecordEntity(
    id = id,
    date = date.format(DATE_FORMATTER),
    pointsEarned = pointsEarned,
    tasksCompleted = tasksCompleted,
    tasksTotal = tasksTotal
)
