package com.example.todolist.core.extensions

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val displayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val displayDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Checks if the [LocalDate] is today.
 *
 * @return True if the date matches [LocalDate.now], false otherwise.
 */
fun LocalDate.isToday(): Boolean {
    return this == LocalDate.now()
}

/**
 * Checks if the [LocalDate] is tomorrow.
 *
 * @return True if the date matches tomorrow's date, false otherwise.
 */
fun LocalDate.isTomorrow(): Boolean {
    return this == LocalDate.now().plusDays(1)
}

/**
 * Checks if the [LocalDate] is yesterday.
 *
 * @return True if the date matches yesterday's date, false otherwise.
 */
fun LocalDate.isYesterday(): Boolean {
    return this == LocalDate.now().minusDays(1)
}

/**
 * Formats a [LocalDate] into a human-readable display string.
 *
 * Special values:
 * - Today -> "Hôm nay"
 * - Tomorrow -> "Ngày mai"
 * - Yesterday -> "Hôm qua"
 * - Other -> "dd/MM/yyyy"
 *
 * @return The display string.
 */
fun LocalDate.toDisplayString(): String {
    return when {
        isToday() -> "Hôm nay"
        isTomorrow() -> "Ngày mai"
        isYesterday() -> "Hôm qua"
        else -> this.format(displayDateFormatter)
    }
}

/**
 * Formats a [LocalDateTime] into a human-readable display string containing date and time.
 *
 * Special values:
 * - Today -> "Hôm nay lúc HH:mm"
 * - Tomorrow -> "Ngày mai lúc HH:mm"
 * - Yesterday -> "Hôm qua lúc HH:mm"
 * - Other -> "dd/MM/yyyy HH:mm"
 *
 * @return The display string.
 */
fun LocalDateTime.toDisplayString(): String {
    val localDate = this.toLocalDate()
    val formattedTime = this.format(timeFormatter)
    return when {
        localDate.isToday() -> "Hôm nay lúc $formattedTime"
        localDate.isTomorrow() -> "Ngày mai lúc $formattedTime"
        localDate.isYesterday() -> "Hôm qua lúc $formattedTime"
        else -> this.format(displayDateTimeFormatter)
    }
}

/**
 * Computes the number of days between this date and [other] date.
 *
 * @param other The date to calculate the difference to.
 * @return The number of days between the two dates (can be negative if other is before this date).
 */
fun LocalDate.daysBetween(other: LocalDate): Long {
    return ChronoUnit.DAYS.between(this, other)
}
