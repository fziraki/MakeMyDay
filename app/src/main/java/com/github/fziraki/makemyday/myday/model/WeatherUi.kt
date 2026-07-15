package com.github.fziraki.makemyday.myday.model

import com.github.fziraki.daykit.model.WeatherInfo

data class WeatherUi(
    val tempC: Double,
    val condition: String,
    val feelsLikeC: Double,
    val city: String
)

fun WeatherInfo.toWeatherUi(): WeatherUi = WeatherUi(
    tempC = tempC,
    condition = condition,
    feelsLikeC = feelsLikeC,
    city = city
)
