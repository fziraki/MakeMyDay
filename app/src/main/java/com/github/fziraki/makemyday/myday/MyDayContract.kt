package com.github.fziraki.makemyday.myday

import com.github.fziraki.daykit.model.CalendarEvent
import com.github.fziraki.daykit.model.Track
import com.github.fziraki.daykit.model.WeatherInfo

data class MyDayState(
    val isLoading: Boolean = true,
    val locationNotSet: Boolean = false,
    val weather: WeatherInfo? = null,
    val events: List<CalendarEvent> = emptyList(),
    val calendarPermissionDenied: Boolean = false,
    val calendarError: Boolean = false,
    val inputArtist: String? = null,
    val musicUiState: MusicUiState = MusicUiState.Idle
)

sealed interface MusicUiState {
    data object Idle : MusicUiState
    data object Loading : MusicUiState
    data class Success(val track: Track) : MusicUiState
    data class Error(val message: String, val action: ErrorAction) : MusicUiState
}

enum class ErrorAction { RETRY, EDIT_ARTIST }

sealed interface MyDayAction {
    data object RetryCalendar : MyDayAction

    data object OnGetTrackClick: MyDayAction

    data object OnDone: MyDayAction

    data class OnArtistChange(val value: String): MyDayAction

    data class OnPlayPause(val value: String): MyDayAction

}