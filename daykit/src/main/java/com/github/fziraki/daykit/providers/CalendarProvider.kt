package com.github.fziraki.daykit.providers

import com.github.fziraki.daykit.model.CalendarEvent

interface CalendarProvider {
    suspend fun getTodayEvents(): CalendarResult
}

sealed interface CalendarResult {
    data class Success(val events: List<CalendarEvent>) : CalendarResult
    data object PermissionDenied : CalendarResult
    data object Error : CalendarResult
}