package com.github.fziraki.makemyday.onboarding

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.fziraki.makemyday.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetupPageViewModel(
    private val preferences: AppPreferences
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
    }

    fun onAction(action: SetupAction) {
        when (action) {
            is SetupAction.ArtistChanged -> {
                _state.update { it.copy(artistInput = action.value) }
            }

            is SetupAction.SetCalendarGranted -> {
                _state.update { it.copy(isCalendarGranted = action.value) }
            }

            SetupAction.OnInit -> {
                viewModelScope.launch {
                    val savedLocation = preferences.savedLocation.first()
                    _state.update { it.copy(selectedCity = savedLocation?.city?:"") }
                }
            }
        }
    }

}