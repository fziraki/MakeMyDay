package com.github.fziraki.daykit.providers

import com.github.fziraki.daykit.model.WeatherInfo

interface WeatherProvider {
    suspend fun getCurrentWeather(lat: Double, lon: Double, city: String): WeatherInfo
}