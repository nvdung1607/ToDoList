package com.example.todolist.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver triggered by scheduled AlarmManager alarms to display notifications.
 */
class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra("task_id") ?: return
        val taskTitle = intent.getStringExtra("task_title") ?: ""
        val type = intent.getStringExtra("reminder_type") ?: return

        when (type) {
            "habit" -> {
                NotificationHelper.showHabitReminder(context, taskId, taskTitle)
            }
            "deadline" -> {
                // If it is a deadline notification, we show 15 minutes left (or default)
                NotificationHelper.showDeadlineReminder(context, taskId, taskTitle, minutesLeft = 15)
            }
            "evening" -> {
                // Evening summary, parse pending tasks count from extra or use fallback
                val count = intent.getIntExtra("pending_task_count", 0)
                NotificationHelper.showEveningReminder(context, count)
            }
        }
    }
}
