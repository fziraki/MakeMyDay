package com.github.fziraki.makemyday.locationsearch

import com.github.fziraki.makemyday.locationsearch.model.LocationResultUi

data class LocationSearchState(
    val query: String = "",
    val results: List<LocationResultUi> = emptyList(),
    val isLoading: Boolean = false,
    val selectedLocation: LocationResultUi? = null
)

sealed interface LocationSearchAction {
    data class QueryChanged(val value: String) : LocationSearchAction
    data class LocationSelected(val location: LocationResultUi) : LocationSearchAction
}