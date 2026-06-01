package com.example.todolist.domain.model

import java.time.LocalDate

/**
 * Represent a completion activity count on a specific date, used for drawing heatmaps.
 */
data class DateActivityCount(
    val date: LocalDate,
    val count: Int
)
