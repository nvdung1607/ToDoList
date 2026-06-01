package com.example.todolist.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.todolist.core.common.Constants
import com.example.todolist.domain.model.SortOrder
import com.example.todolist.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages saving and retrieving user preference settings using DataStore Preferences.
 */
@Singleton
class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey(Constants.PREF_THEME_MODE)
        val HABIT_NOTIF = booleanPreferencesKey(Constants.PREF_HABIT_NOTIF)
        val EVENING_REMINDER = booleanPreferencesKey(Constants.PREF_EVENING_REMINDER)
        val EVENING_TIME = stringPreferencesKey(Constants.PREF_EVENING_TIME)
        val SORT_ORDER = stringPreferencesKey(Constants.PREF_SORT_ORDER)
    }

    /**
     * Observable flow of the parsed [UserPreferences].
     * Handles [IOException] by recovering with default settings.
     */
    val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = try {
                ThemeMode.valueOf(themeModeStr)
            } catch (e: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }

            val habitNotificationsEnabled = preferences[PreferencesKeys.HABIT_NOTIF] ?: true
            val dailyEveningReminderEnabled = preferences[PreferencesKeys.EVENING_REMINDER] ?: true
            val eveningReminderTime = preferences[PreferencesKeys.EVENING_TIME] ?: "21:00"

            val sortOrderStr = preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.PRIORITY.name
            val taskSortOrder = try {
                SortOrder.valueOf(sortOrderStr)
            } catch (e: IllegalArgumentException) {
                SortOrder.PRIORITY
            }

            UserPreferences(
                themeMode = themeMode,
                habitNotificationsEnabled = habitNotificationsEnabled,
                dailyEveningReminderEnabled = dailyEveningReminderEnabled,
                eveningReminderTime = eveningReminderTime,
                taskSortOrder = taskSortOrder
            )
        }

    /**
     * Updates the app theme mode preference.
     */
    suspend fun updateTheme(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    /**
     * Updates the habit notifications enabled state.
     */
    suspend fun updateHabitNotification(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HABIT_NOTIF] = enabled
        }
    }

    /**
     * Updates the daily evening reminder enabled state and time.
     */
    suspend fun updateEveningReminder(enabled: Boolean, time: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENING_REMINDER] = enabled
            preferences[PreferencesKeys.EVENING_TIME] = time
        }
    }

    /**
     * Updates the task sorting order preference.
     */
    suspend fun updateSortOrder(order: SortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = order.name
        }
    }
}
