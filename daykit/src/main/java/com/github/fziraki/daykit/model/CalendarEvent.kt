package com.github.fziraki.daykit.model

data class CalendarEvent(
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val isAllDay: Boolean
)