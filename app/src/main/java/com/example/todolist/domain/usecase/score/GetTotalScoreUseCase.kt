package com.example.todolist.domain.usecase.score

import com.example.todolist.core.utils.DateUtils
import com.example.todolist.domain.model.DateRange
import com.example.todolist.domain.repository.ScoreRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe the total score points accumulated over a specific date range.
 */
class GetTotalScoreUseCase @Inject constructor(
    private val scoreRepository: ScoreRepository
) {
    /**
     * Executes the use case.
     *
     * @param range The date range filter scope.
     * @return A Flow emitting the total points for the selected range.
     */
    operator fun invoke(range: DateRange): Flow<Int> {
        val today = LocalDate.now()
        return when (range) {
            DateRange.DAY -> {
                scoreRepository.observeTotalPointsBetweenDates(today, today)
            }
            DateRange.WEEK -> {
                val (start, end) = DateUtils.getWeekRange(today)
                scoreRepository.observeTotalPointsBetweenDates(start, end)
            }
            DateRange.MONTH -> {
                val (start, end) = DateUtils.getMonthRange(today)
                scoreRepository.observeTotalPointsBetweenDates(start, end)
            }
            DateRange.ALL_TIME -> {
                scoreRepository.observeAllTimePoints()
            }
        }
    }
}
