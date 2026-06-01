package com.example.todolist.domain.usecase.stats

import com.example.todolist.core.utils.DateUtils
import com.example.todolist.domain.model.CompletionLog
import com.example.todolist.domain.model.DateRange
import com.example.todolist.domain.repository.CompletionLogRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to observe completion logs within a selected date range.
 */
class GetCompletionHistoryUseCase @Inject constructor(
    private val completionLogRepository: CompletionLogRepository
) {
    /**
     * Executes the use case.
     *
     * @param range The date range filter scope.
     * @return A Flow emitting the sorted completion logs.
     */
    operator fun invoke(range: DateRange): Flow<List<CompletionLog>> {
        val today = LocalDate.now()
        val (fromDate, toDate) = when (range) {
            DateRange.DAY -> Pair(today, today)
            DateRange.WEEK -> DateUtils.getWeekRange(today)
            DateRange.MONTH -> DateUtils.getMonthRange(today)
            DateRange.ALL_TIME -> Pair(today.minusYears(10), today)
        }

        return completionLogRepository.observeLogsBetweenDates(fromDate, toDate).map { logs ->
            logs.sortedByDescending { it.completedAt }
        }
    }
}
