package com.example.todolist.data.repository

import com.example.todolist.data.local.dao.ScoreDao
import com.example.todolist.data.mapper.toDomain
import com.example.todolist.data.mapper.toEntity
import com.example.todolist.domain.model.ScoreRecord
import com.example.todolist.domain.repository.ScoreRepository
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScoreRepositoryImpl @Inject constructor(
    private val scoreDao: ScoreDao
) : ScoreRepository {

    override fun observeRecordForDate(date: LocalDate): Flow<ScoreRecord?> {
        return scoreDao.observeRecordForDate(date.toString()).map { it?.toDomain() }
    }

    override fun observeRecordsBetweenDates(from: LocalDate, to: LocalDate): Flow<List<ScoreRecord>> {
        return scoreDao.observeRecordsBetweenDates(from.toString(), to.toString()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeTotalPointsBetweenDates(from: LocalDate, to: LocalDate): Flow<Int> {
        return scoreDao.observeTotalPointsBetweenDates(from.toString(), to.toString())
    }

    override fun observeAllTimePoints(): Flow<Int> {
        return scoreDao.observeAllTimePoints()
    }

    override suspend fun addPointsForToday(points: Int) {
        val todayRecord = getOrCreateTodayRecord()
        val updatedRecord = todayRecord.copy(
            pointsEarned = todayRecord.pointsEarned + points,
            tasksCompleted = todayRecord.tasksCompleted + 1
        )
        scoreDao.insertOrReplace(updatedRecord.toEntity())
    }

    override suspend fun getOrCreateTodayRecord(): ScoreRecord {
        val todayStr = LocalDate.now().toString()
        val existingEntity = scoreDao.getRecordForDate(todayStr)
        if (existingEntity != null) {
            return existingEntity.toDomain()
        }
        val newRecord = ScoreRecord(
            id = UUID.randomUUID().toString(),
            date = LocalDate.now(),
            pointsEarned = 0,
            tasksCompleted = 0,
            tasksTotal = 0
        )
        scoreDao.insertOrReplace(newRecord.toEntity())
        return newRecord
    }
}
