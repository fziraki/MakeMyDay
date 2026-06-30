package com.github.fziraki.makemyday.myday

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.fziraki.daykit.DayKitClient
import com.github.fziraki.daykit.providers.CalendarResult
import com.github.fziraki.makemyday.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyDayViewModel(
    private val client: DayKitClient,
    private val preferences: AppPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(MyDayState())
    val state = _state.asStateFlow()

    val isCalendarAsked = preferences.wasAsked(
        Manifest.permission.READ_CALENDAR
    )

    init {
        viewModelScope.launch {
            preferences.savedLocation.collect { location ->
                if (location != null) {
                    loadDay()
                } else {
                    _state.update { it.copy(isLoading = false, locationNotSet = true) }
                }
            }
        }
    }
    fun markCalendarAsked() {
        viewModelScope.launch {
            preferences.markAsked(
                Manifest.permission.READ_CALENDAR
            )
        }
    }

    fun onAction(action: MyDayAction) {
        when (action) {
            is MyDayAction.CompleteTask -> completeTask(action.id)
            MyDayAction.RetryCalendar -> loadDay()
        }
    }

    private fun loadDay() {
        viewModelScope.launch {

            val savedLocation = preferences.savedLocation.first()

            val summary = client.getMyDay(
                lat = savedLocation?.lat,
                lon = savedLocation?.lon,
                city = savedLocation?.city
            )

            val (events, permissionDenied, error) = when (val result = summary.calendarResult) {
                is CalendarResult.Success -> Triple(result.events, false, false)
                CalendarResult.PermissionDenied -> Triple(emptyList(), true, false)
                CalendarResult.Error -> Triple(emptyList(), false, true)
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    locationNotSet = savedLocation == null,
                    weather = summary.weather,
                    events = events,
                    calendarPermissionDenied = permissionDenied,
                    calendarError = error,
                    tasks = summary.tasks,
                    recommendedTrack = summary.recommendedTrack
                )
            }
        }
    }

    private fun completeTask(id: String) {
        viewModelScope.launch {
            val success = client.completeTask(id)
            if (success) {
                _state.update { state ->
                    state.copy(
                        tasks = state.tasks?.filter { it.id != id }
                    )
                }
            }
        }
    }
}