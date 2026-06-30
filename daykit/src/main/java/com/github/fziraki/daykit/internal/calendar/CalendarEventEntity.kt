package com.github.fziraki.daykit.internal.calendar

internal data class CalendarEventEntity(
    val title: String?,
    val startTime: Long,
    val endTime: Long,
    val isAllDay: Boolean
)