package com.github.fziraki.daykit.internal.calendar

import com.github.fziraki.daykit.model.CalendarEvent

internal fun CalendarEventEntity.toDomain(): CalendarEvent =
    CalendarEvent(
        title = title ?: "",
        startTime = startTime,
        endTime = endTime,
        isAllDay = isAllDay
    )