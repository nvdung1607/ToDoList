package com.example.todolist.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.todolist.core.utils.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles scheduling task and habit reminders using AlarmManager.
 */
@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedules a daily repeating reminder for a Habit task.
     */
    fun scheduleHabitReminder(taskId: String, taskTitle: String, time: LocalTime) {
        val now = LocalDateTime.now()
        var triggerTime = LocalDate.now().atTime(time)
        if (triggerTime.isBefore(now)) {
            triggerTime = triggerTime.plusDays(1)
        }
        val triggerMs = triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val pendingIntent = buildPendingIntent(taskId, taskTitle, "habit")

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerMs,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    /**
     * Schedules an exact alarm reminder for a task deadline.
     */
    fun scheduleOneTimeReminder(
        taskId: String,
        taskTitle: String,
        deadline: LocalDateTime,
        minutesBefore: Int
    ) {
        val triggerTime = deadline.minusMinutes(minutesBefore.toLong())
        val triggerMs = triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val pendingIntent = buildPendingIntent(taskId, taskTitle, "deadline")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent)
        }
    }

    /**
     * Cancels an existing reminder alarm.
     */
    fun cancelReminder(taskId: String) {
        val pendingIntent = buildPendingIntent(taskId, "", "")
        alarmManager.cancel(pendingIntent)
    }

    private fun buildPendingIntent(taskId: String, taskTitle: String, type: String): PendingIntent {
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra("task_id", taskId)
            putExtra("task_title", taskTitle)
            putExtra("reminder_type", type)
        }
        return PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
