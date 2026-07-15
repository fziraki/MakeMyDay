package com.github.fziraki.makemyday.onboarding

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.fziraki.makemyday.data.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetupPageViewModel(
    private val preferences: PreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SetupState())
    val state = _state.asStateFlow()

    val isCalendarAsked = preferences.wasAsked(
        Manifest.permission.READ_CALENDAR
    )

    fun markCalendarAsked() {
        viewModelScope.launch {
            preferences.markAsked(
                Manifest.permission.READ_CALENDAR
            )
        }
    }

    init {
        viewModelScope.launch {
            preferences.savedLocation.collect { location ->
                _state.update { it.copy(selectedCity = location?.city?:"") }
            }
        }
        viewModelScope.launch {
            preferences.savedFavoriteArtist.collect { artist ->
                _state.update { it.copy(artistInput = artist) }
            }
        }
    }

    fun onAction(action: SetupAction) {
        when (action) {
            is SetupAction.ArtistChanged -> {
                _state.update { it.copy(artistInput = action.value) }
            }

            SetupAction.OnDone -> {
                viewModelScope.launch {
                    _state.value.artistInput?.let {
                        preferences.saveFavoriteArtist(it)
                    }
                }
            }

            is SetupAction.SetCalendarGranted -> {
                _state.update { it.copy(isCalendarGranted = action.value) }
            }

        }
    }

}