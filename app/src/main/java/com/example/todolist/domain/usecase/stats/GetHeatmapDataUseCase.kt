package com.example.todolist.domain.usecase.stats

import com.example.todolist.domain.model.HeatmapEntry
import com.example.todolist.domain.repository.CompletionLogRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case to retrieve and format task activity counts for contributions graph heatmaps.
 */
class GetHeatmapDataUseCase @Inject constructor(
    private val completionLogRepository: CompletionLogRepository
) {
    /**
     * Executes the use case.
     *
     * @param monthsBack The historical month range to search.
     * @return A Flow emitting the list of formatted [HeatmapEntry] objects.
     */
    operator fun invoke(monthsBack: Int = 3): Flow<List<HeatmapEntry>> = flow {
        val toDate = LocalDate.now()
        val fromDate = toDate.minusMonths(monthsBack.toLong())

        val dbActivityList = completionLogRepository.getActivityCountByDate(fromDate, toDate)
        val heatmapEntries = dbActivityList.map {
            HeatmapEntry(
                date = it.date,
                activityCount = it.count
            )
        }
        emit(heatmapEntries)
    }
}
