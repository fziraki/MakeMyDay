package com.github.fziraki.makemyday.locationsearch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.fziraki.daykit.DayKitClient
import com.github.fziraki.makemyday.data.PreferencesRepository
import com.github.fziraki.makemyday.locationsearch.model.toLocationResultUi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class LocationSearchViewModel(
    private val client: DayKitClient,
    private val preferences: PreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LocationSearchState())
    val state = _state.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(400.milliseconds)
                .distinctUntilChanged()
                .filter { it.length >= 2 }
                .collect { query ->
                    _state.update { it.copy(isLoading = true) }
                    val results = client.searchCity(query).map { it.toLocationResultUi() }
                    Log.d("tagg","results $results")
                    _state.update { it.copy(results = results, isLoading = false) }
                }
        }
    }

    fun onAction(action: LocationSearchAction) {
        when (action) {
            is LocationSearchAction.QueryChanged -> {
                _state.update { it.copy(query = action.value) }
                queryFlow.value = action.value
            }
            is LocationSearchAction.LocationSelected -> {
                viewModelScope.launch {
                    val domain = com.github.fziraki.daykit.model.LocationResult(
                        city = action.location.city,
                        country = action.location.country,
                        lat = action.location.lat,
                        lon = action.location.lon
                    )
                    preferences.saveLocation(domain)
                    _state.update { it.copy(selectedLocation = action.location) }
                }
            }
        }
    }
}
