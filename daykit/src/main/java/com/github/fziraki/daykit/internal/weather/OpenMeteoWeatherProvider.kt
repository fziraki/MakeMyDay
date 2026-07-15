package com.github.fziraki.daykit.internal.weather

import com.github.fziraki.daykit.model.WeatherInfo
import com.github.fziraki.daykit.network.safeCall
import com.github.fziraki.daykit.providers.WeatherProvider
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result
import com.github.fziraki.daykit.result.map
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class OpenMeteoWeatherProvider(
    private val client: HttpClient
) : WeatherProvider {

    override suspend fun getCurrentWeather(lat: Double, lon: Double, city: String): Result<WeatherInfo, DataError.Network> {
        return safeCall<OpenMeteoResponse> {
            client.get("https://api.open-meteo.com/v1/forecast") {
                parameter("latitude", lat)
                parameter("longitude", lon)
                parameter("current", "temperature_2m,apparent_temperature,weather_code")
                parameter("timezone", "auto")
            }
        }.map { response ->
            WeatherInfo(
                tempC = response.current.temperature,
                feelsLikeC = response.current.apparentTemperature,
                condition = weatherCodeToCondition(response.current.weatherCode),
                city = city
            )
        }
    }

    private fun weatherCodeToCondition(code: Int): String = when (code) {
        0 -> "Clear sky"
        1, 2, 3 -> "Partly cloudy"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rainy"
        71, 73, 75 -> "Snowy"
        80, 81, 82 -> "Showers"
        95 -> "Thunderstorm"
        else -> "Cloudy"
    }
}