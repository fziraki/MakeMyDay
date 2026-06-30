package com.github.fziraki.daykit.model

data class WeatherInfo(
    val tempC: Double,
    val condition: String,
    val feelsLikeC: Double,
    val city: String
)