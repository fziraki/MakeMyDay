package com.github.fziraki.daykit.model

import com.github.fziraki.daykit.providers.CalendarResult
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result

data class MyDaySummary(
    val calendarResult: CalendarResult,
    val weather: WeatherInfo?,
    val tasks: List<TodoItem>?,
    val recommendedTrack: Result<Track, DataError.Network>?
)