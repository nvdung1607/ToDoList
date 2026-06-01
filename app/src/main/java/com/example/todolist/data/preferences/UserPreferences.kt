package com.example.todolist.data.preferences

import com.example.todolist.domain.model.SortOrder
import com.example.todolist.domain.model.ThemeMode

/**
 * Data class representing user preference options.
 */
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val habitNotificationsEnabled: Boolean = true,
    val dailyEveningReminderEnabled: Boolean = true,
    val eveningReminderTime: String = "21:00",
    val taskSortOrder: SortOrder = SortOrder.PRIORITY
)
