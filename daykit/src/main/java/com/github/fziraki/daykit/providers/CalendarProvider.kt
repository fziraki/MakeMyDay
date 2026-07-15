package com.github.fziraki.daykit.providers

import com.github.fziraki.daykit.model.CalendarEvent
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result

interface CalendarProvider {
    suspend fun getTodayEvents(): Result<List<CalendarEvent>, DataError.Local>
}