package com.github.fziraki.daykit.model

import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result

data class MyDaySummary(
    val calendarResult: Result<List<CalendarEvent>, DataError.Local>,
    val weather: Result<WeatherInfo, DataError.Network>?,
    val recommendedTrack: Result<Track, DataError.Network>?
)