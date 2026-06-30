package com.github.fziraki.daykit.di

import com.github.fziraki.daykit.internal.weather.NominatimDataSource
import com.github.fziraki.daykit.internal.weather.OpenMeteoWeatherProvider
import com.github.fziraki.daykit.providers.WeatherProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor

internal object ServiceLocator {

    val httpClient: HttpClient by lazy {
        HttpClient(OkHttp) {
            engine {
                val logging = HttpLoggingInterceptor().apply {
                    level = if (true)
                        HttpLoggingInterceptor.Level.BODY
                    else
                        HttpLoggingInterceptor.Level.NONE
                }
                addInterceptor(logging)
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    val locationDataSource by lazy {
        NominatimDataSource(httpClient)
    }

    val weatherProvider: WeatherProvider by lazy {
        OpenMeteoWeatherProvider(httpClient)
    }

}