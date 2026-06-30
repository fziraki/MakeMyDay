package com.github.fziraki.daykit.model

import com.github.fziraki.daykit.providers.CalendarResult

data class CalendarEvent(
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val isAllDay: Boolean
)

data class WeatherInfo(
    val tempC: Double,
    val condition: String,
    val feelsLikeC: Double,
    val city: String
)

data class TodoItem(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val dueDate: Long?
)

data class Track(
    val title: String,
    val artist: String?,
    val source: String,
    val playedAt: Long
)

data class MyDaySummary(
    val calendarResult: CalendarResult,
    val weather: WeatherInfo?,
    val tasks: List<TodoItem>?,
    val recommendedTrack: Track?
)