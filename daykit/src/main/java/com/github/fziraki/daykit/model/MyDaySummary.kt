package com.github.fziraki.daykit.model

import com.github.fziraki.daykit.providers.CalendarResult

data class MyDaySummary(
    val calendarResult: CalendarResult,
    val weather: WeatherInfo?,
    val tasks: List<TodoItem>?,
    val recommendedTrack: Track?
)