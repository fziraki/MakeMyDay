package com.github.fziraki.daykit.internal.weather

import com.github.fziraki.daykit.model.WeatherInfo
import com.github.fziraki.daykit.providers.WeatherProvider

internal class StubWeatherProvider : WeatherProvider {
    override suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherInfo {
        return WeatherInfo(
            tempC = 22.0,
            condition = "Sunny",
            feelsLikeC = 19.0,
            city = "Amsterdam"
        )
    }
}