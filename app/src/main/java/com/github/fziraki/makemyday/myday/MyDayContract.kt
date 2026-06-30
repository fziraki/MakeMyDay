package com.github.fziraki.makemyday.myday

import com.github.fziraki.daykit.model.CalendarEvent
import com.github.fziraki.daykit.model.Track
import com.github.fziraki.daykit.model.WeatherInfo
import com.github.fziraki.daykit.model.TodoItem

data class MyDayState(
    val isLoading: Boolean = true,
    val weather: WeatherInfo? = null,
    val events: List<CalendarEvent> = emptyList(),
    val calendarPermissionDenied: Boolean = false,
    val calendarError: Boolean = false,
    val tasks: List<TodoItem>? = null,
    val recommendedTrack: Track? = null
)

sealed interface MyDayAction {
    data class CompleteTask(val id: String) : MyDayAction
    data object RetryCalendar : MyDayAction

}