package com.example.todolist.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.todolist.domain.usecase.task.RollOverTasksUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

/**
 * Background worker to automatically roll over uncompleted daily tasks at midnight
 * and reset habit completions for the new day.
 */
@HiltWorker
class DailyRollOverWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val rollOverTasksUseCase: RollOverTasksUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            rollOverTasksUseCase()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "daily_rollover_worker"

        /**
         * Schedules a periodic daily work request running every 24 hours.
         * The execution starts exactly at 00:01 AM the next morning.
         */
        fun schedule(workManager: WorkManager) {
            val now = LocalDateTime.now()
            val nextMidnight = now.toLocalDate().plusDays(1).atTime(0, 1)
            val delayMs = ChronoUnit.MILLIS.between(now, nextMidnight)

            val workRequest = PeriodicWorkRequestBuilder<DailyRollOverWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        /**
         * Cancels the scheduled daily rollover work.
         */
        fun cancel(workManager: WorkManager) {
            workManager.cancelUniqueWork(WORK_NAME)
        }
    }
}
