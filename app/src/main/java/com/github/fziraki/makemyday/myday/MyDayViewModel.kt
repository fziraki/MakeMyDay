package com.github.fziraki.makemyday.myday

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.fziraki.daykit.DayKitClient
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result
import com.github.fziraki.makemyday.data.PreferencesRepository
import com.github.fziraki.makemyday.myday.model.toCalendarEventUi
import com.github.fziraki.makemyday.myday.model.toWeatherUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyDayViewModel(
    private val client: DayKitClient,
    private val preferences: PreferencesRepository,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _state = MutableStateFlow(MyDayState())
    val state = _state.asStateFlow()

    val isCalendarAsked = preferences.wasAsked(
        Manifest.permission.READ_CALENDAR
    )

    init {
        viewModelScope.launch {
            preferences.savedLocation.collect {
                loadDay()
            }
        }
        viewModelScope.launch {
            preferences.savedFavoriteArtist.collect { artist ->
                _state.update { it.copy(inputArtist = artist) }
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


    val isPlaying = audioPlayer.isPlaying

    fun onPlayPause(url: String) {
        if (isPlaying.value) {
            audioPlayer.pause()
        } else {
            audioPlayer.play(url)
        }
    }

    override fun onCleared() {
        audioPlayer.release()
    }

    fun onAction(action: MyDayAction) {
        when (action) {
            MyDayAction.RetryCalendar -> loadDay()

            MyDayAction.OnDone -> {
                _state.value.inputArtist?.let { artist ->
                    viewModelScope.launch {
                        preferences.saveFavoriteArtist(artist)
                    }
                }
            }

            is MyDayAction.OnArtistChange -> {
                _state.update { it.copy(inputArtist = action.value) }
            }
            MyDayAction.OnGetTrackClick -> {

                audioPlayer.release()

                _state.value.inputArtist?.let { artist ->
                    viewModelScope.launch {
                        preferences.saveFavoriteArtist(artist)
                    }
                    getMusic()
                }
            }

            is MyDayAction.OnPlayPause -> onPlayPause(action.value)
        }
    }

    private fun getMusic() {
        viewModelScope.launch {
            val favoriteArtist = preferences.savedFavoriteArtist.first()
            val musicUiState = favoriteArtist?.let {
                client.getRecommendedTrack(it)
            }.toMusicUiState()
            _state.update { it.copy(musicUiState = musicUiState) }
        }
    }

    private fun loadDay() {
        viewModelScope.launch {

            val savedLocation = preferences.savedLocation.first()
            val favoriteArtist = preferences.savedFavoriteArtist.first()

            val summary = client.getMyDay(
                lat = savedLocation?.lat,
                lon = savedLocation?.lon,
                city = savedLocation?.city,
                artist = favoriteArtist
            )

            val (events, permissionDenied, error) = when (val result = summary.calendarResult) {
                is Result.Success -> Triple(result.data.map { it.toCalendarEventUi() }, false, false)
                is Result.Error -> when (result.error) {
                    DataError.Local.PERMISSION_DENIED -> Triple(emptyList(), true, false)
                    else -> Triple(emptyList(), false, true)
                }
            }

            val weatherInfo = when (val w = summary.weather) {
                is Result.Success -> w.data.toWeatherUi()
                else -> null
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    locationNotSet = savedLocation == null,
                    weather = weatherInfo,
                    events = events,
                    calendarPermissionDenied = permissionDenied,
                    calendarError = error,
                    musicUiState = summary.recommendedTrack?.toMusicUiState() ?: MusicUiState.Idle
                )
            }
        }
    }

}
