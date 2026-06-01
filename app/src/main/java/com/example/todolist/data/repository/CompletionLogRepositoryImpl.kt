package com.example.todolist.data.repository

import com.example.todolist.data.local.dao.CompletionLogDao
import com.example.todolist.data.mapper.toDomain
import com.example.todolist.data.mapper.toEntity
import com.example.todolist.domain.model.CompletionLog
import com.example.todolist.domain.model.DateActivityCount
import com.example.todolist.domain.repository.CompletionLogRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompletionLogRepositoryImpl @Inject constructor(
    private val completionLogDao: CompletionLogDao
) : CompletionLogRepository {

    override suspend fun insertLog(log: CompletionLog) {
        completionLogDao.insertLog(log.toEntity())
    }

    override fun observeLogsForDate(date: LocalDate): Flow<List<CompletionLog>> {
        return completionLogDao.observeLogsForDate(date.toString()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeLogsBetweenDates(from: LocalDate, to: LocalDate): Flow<List<CompletionLog>> {
        return completionLogDao.observeLogsBetweenDates(from.toString(), to.toString()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getActivityCountByDate(from: LocalDate, to: LocalDate): List<DateActivityCount> {
        return completionLogDao.getActivityCountByDate(from.toString(), to.toString()).map {
            DateActivityCount(
                date = LocalDate.parse(it.completed_date),
                count = it.count
            )
        }
    }

    override suspend fun getHabitTaskLogs(taskId: String): List<CompletionLog> {
        return completionLogDao.getHabitTaskLogs(taskId).map { it.toDomain() }
    }

    override fun observeTotalPointsBetweenDates(from: LocalDate, to: LocalDate): Flow<Int> {
        return completionLogDao.observeTotalPointsBetweenDates(from.toString(), to.toString())
    }
}
