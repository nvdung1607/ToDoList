package com.example.todolist.core.utils

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

/**
 * Utility functions for date and time calculations, parsing, and formatting.
 */
object DateUtils {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val zoneId = ZoneId.systemDefault()

    /**
     * Gets the date range of the week (Monday to Sunday) containing the given date.
     *
     * @param date The reference date.
     * @return A Pair containing the Monday and Sunday dates of that week.
     */
    fun getWeekRange(date: LocalDate): Pair<LocalDate, LocalDate> {
        val monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        return Pair(monday, sunday)
    }

    /**
     * Gets the date range of the month (1st of the month to the last day) containing the given date.
     *
     * @param date The reference date.
     * @return A Pair containing the start and end dates of that month.
     */
    fun getMonthRange(date: LocalDate): Pair<LocalDate, LocalDate> {
        val firstDay = date.with(TemporalAdjusters.firstDayOfMonth())
        val lastDay = date.with(TemporalAdjusters.lastDayOfMonth())
        return Pair(firstDay, lastDay)
    }

    /**
     * Formats a [LocalDate] to DB format ("yyyy-MM-dd").
     *
     * @param date The local date to format.
     * @return The formatted date string.
     */
    fun formatDateForDb(date: LocalDate): String {
        return date.format(dateFormatter)
    }

    /**
     * Parses a [LocalDate] from a DB format string ("yyyy-MM-dd").
     *
     * @param str The date string to parse.
     * @return The parsed [LocalDate].
     */
    fun parseDateFromDb(str: String): LocalDate {
        return LocalDate.parse(str, dateFormatter)
    }

    /**
     * Formats a [LocalTime] to DB format ("HH:mm").
     *
     * @param time The local time to format.
     * @return The formatted time string.
     */
    fun formatTimeForDb(time: LocalTime): String {
        return time.format(timeFormatter)
    }

    /**
     * Parses a [LocalTime] from a DB format string ("HH:mm").
     *
     * @param str The time string to parse.
     * @return The parsed [LocalTime].
     */
    fun parseTimeFromDb(str: String): LocalTime {
        return LocalTime.parse(str, timeFormatter)
    }

    /**
     * Converts [LocalDateTime] to epoch milliseconds.
     *
     * @param dt The LocalDateTime.
     * @return Epoch millisecond timestamp.
     */
    fun localDateTimeToEpoch(dt: LocalDateTime): Long {
        return dt.atZone(zoneId).toInstant().toEpochMilli()
    }

    /**
     * Converts epoch milliseconds to [LocalDateTime].
     *
     * @param epoch Epoch millisecond timestamp.
     * @return The corresponding [LocalDateTime].
     */
    fun epochToLocalDateTime(epoch: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), zoneId)
    }
}
