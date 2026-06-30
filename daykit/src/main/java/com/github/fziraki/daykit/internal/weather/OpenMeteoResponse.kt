package com.github.fziraki.daykit.internal.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenMeteoResponse(
    @SerialName("current") val current: CurrentWeather
)

@Serializable
internal data class CurrentWeather(
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("apparent_temperature") val apparentTemperature: Double,
    @SerialName("weather_code") val weatherCode: Int
)