package com.github.fziraki.daykit.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationResult(
    val city: String,
    val country: String,
    val lat: Double,
    val lon: Double
)