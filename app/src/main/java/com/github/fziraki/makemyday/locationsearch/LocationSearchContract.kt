package com.github.fziraki.makemyday.locationsearch

import com.github.fziraki.daykit.model.LocationResult

data class LocationSearchState(
    val query: String = "",
    val results: List<LocationResult> = emptyList(),
    val isLoading: Boolean = false,
    val selectedLocation: LocationResult? = null
)

sealed interface LocationSearchAction {
    data class QueryChanged(val value: String) : LocationSearchAction
    data class LocationSelected(val location: LocationResult) : LocationSearchAction
}