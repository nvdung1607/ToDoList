package com.example.todolist.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todolist.core.common.Constants

/**
 * Helper object to build and display notifications.
 */
object NotificationHelper {

    private const val HABIT_NOTIFICATION_ID_OFFSET = 1000
    private const val DEADLINE_NOTIFICATION_ID_OFFSET = 2000
    private const val EVENING_NOTIFICATION_ID = 3000

    /**
     * Creates the notification channel required for task reminders on API 26+.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = Constants.NOTIFICATION_CHANNEL_NAME
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = "Kênh thông báo nhắc nhở công việc và thói quen"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Shows a notification reminder for a Habit task.
     */
    fun showHabitReminder(context: Context, taskId: String, taskTitle: String) {
        createNotificationChannel(context)

        val tapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("todolist://task/$taskId")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapPendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val completeIntent = Intent(context, CompleteTaskReceiver::class.java).apply {
            action = "COMPLETE_TASK"
            putExtra("task_id", taskId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Using standard system icon
            .setContentTitle("⏰ Nhắc nhở thói quen")
            .setContentText(taskTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.ic_menu_save,
                "Hoàn thành",
                completePendingIntent
            )

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(taskId.hashCode() + HABIT_NOTIFICATION_ID_OFFSET, builder.build())
        } catch (e: SecurityException) {
            // Handled missing permission on Android 13+
        }
    }

    /**
     * Shows a deadline reminder countdown notification for a Daily/One-time task.
     */
    fun showDeadlineReminder(context: Context, taskId: String, taskTitle: String, minutesLeft: Int) {
        createNotificationChannel(context)

        val tapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("todolist://task/$taskId")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapPendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Sắp tới hạn chót!")
            .setContentText("Nhiệm vụ \"$taskTitle\" còn $minutesLeft phút!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(taskId.hashCode() + DEADLINE_NOTIFICATION_ID_OFFSET, builder.build())
        } catch (e: SecurityException) {
            // Handled missing permission on Android 13+
        }
    }

    /**
     * Shows an evening summary notification with the number of pending tasks remaining.
     */
    fun showEveningReminder(context: Context, pendingTaskCount: Int) {
        createNotificationChannel(context)

        val mainIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val mainPendingIntent = PendingIntent.getActivity(
            context,
            EVENING_NOTIFICATION_ID,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🌙 Tổng kết buổi tối")
            .setContentText("Bạn còn $pendingTaskCount công việc chưa hoàn thành hôm nay. Cố gắng lên nhé!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(mainPendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(EVENING_NOTIFICATION_ID, builder.build())
        } catch (e: SecurityException) {
            // Handled missing permission on Android 13+
        }
    }
}
