package com.github.fziraki.daykit.di

import com.github.fziraki.daykit.internal.music.DeezerMusicProvider
import com.github.fziraki.daykit.internal.weather.NominatimDataSource
import com.github.fziraki.daykit.internal.weather.OpenMeteoWeatherProvider
import com.github.fziraki.daykit.network.HttpClientFactory
import com.github.fziraki.daykit.network.getEngine
import com.github.fziraki.daykit.providers.MusicProvider
import com.github.fziraki.daykit.providers.WeatherProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine

internal object ServiceLocator {

    val httpClientEngine: HttpClientEngine by lazy {
        getEngine()
    }

    val httpClient: HttpClient by lazy {
        HttpClientFactory.create(
            engine = httpClientEngine,
        )
    }

    val locationDataSource by lazy {
        NominatimDataSource(httpClient)
    }

    val weatherProvider: WeatherProvider by lazy {
        OpenMeteoWeatherProvider(httpClient)
    }

    val musicProvider: MusicProvider by lazy {
        DeezerMusicProvider(httpClient)
    }

}