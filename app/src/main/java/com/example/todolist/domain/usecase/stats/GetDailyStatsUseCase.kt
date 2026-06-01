package com.example.todolist.domain.usecase.stats

import com.example.todolist.core.utils.DateUtils
import com.example.todolist.domain.model.DailyStats
import com.example.todolist.domain.model.DateRange
import com.example.todolist.domain.model.TaskType
import com.example.todolist.domain.repository.CompletionLogRepository
import com.example.todolist.domain.repository.ScoreRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case to compute and retrieve daily completion statistics.
 */
class GetDailyStatsUseCase @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val completionLogRepository: CompletionLogRepository
) {
    /**
     * Executes the use case.
     *
     * @param range The date range filter scope.
     * @return A Flow emitting a list of [DailyStats] for the selected range.
     */
    operator fun invoke(range: DateRange): Flow<List<DailyStats>> {
        val today = LocalDate.now()
        val (fromDate, toDate) = when (range) {
            DateRange.DAY -> Pair(today, today)
            DateRange.WEEK -> DateUtils.getWeekRange(today)
            DateRange.MONTH -> DateUtils.getMonthRange(today)
            DateRange.ALL_TIME -> Pair(today.minusYears(10), today)
        }

        val recordsFlow = scoreRepository.observeRecordsBetweenDates(fromDate, toDate)
        val logsFlow = completionLogRepository.observeLogsBetweenDates(fromDate, toDate)

        return combine(recordsFlow, logsFlow) { records, logs ->
            val logsByDate = logs.groupBy { it.completedAt.toLocalDate() }

            records.map { record ->
                val date = record.date
                val dayLogs = logsByDate[date] ?: emptyList()

                DailyStats(
                    date = date,
                    totalTasks = record.tasksTotal,
                    completedTasks = record.tasksCompleted,
                    totalPoints = record.pointsEarned,
                    habitCompletions = dayLogs.count { it.taskType == TaskType.HABIT },
                    dailyCompletions = dayLogs.count { it.taskType == TaskType.DAILY },
                    oneTimeCompletions = dayLogs.count { it.taskType == TaskType.ONE_TIME }
                )
            }
        }
    }
}
