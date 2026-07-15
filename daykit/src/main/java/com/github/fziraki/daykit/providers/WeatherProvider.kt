package com.github.fziraki.daykit.providers

import com.github.fziraki.daykit.model.WeatherInfo
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result

interface WeatherProvider {
    suspend fun getCurrentWeather(lat: Double, lon: Double, city: String): Result<WeatherInfo, DataError.Network>
}