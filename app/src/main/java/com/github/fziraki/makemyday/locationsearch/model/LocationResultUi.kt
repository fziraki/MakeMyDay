package com.github.fziraki.makemyday.locationsearch.model

import com.github.fziraki.daykit.model.LocationResult

data class LocationResultUi(
    val city: String,
    val country: String,
    val lat: Double,
    val lon: Double
)

fun LocationResult.toLocationResultUi(): LocationResultUi = LocationResultUi(
    city = city,
    country = country,
    lat = lat,
    lon = lon
)
