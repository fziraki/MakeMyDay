package com.github.fziraki.makemyday.myday.model

import com.github.fziraki.daykit.model.CalendarEvent

data class CalendarEventUi(
    val title: String?,
    val startTime: Long,
    val endTime: Long,
    val isAllDay: Boolean
)

fun CalendarEvent.toCalendarEventUi(): CalendarEventUi = CalendarEventUi(
    title = title,
    startTime = startTime,
    endTime = endTime,
    isAllDay = isAllDay
)
