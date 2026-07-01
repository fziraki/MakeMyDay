package com.github.fziraki.daykit

import android.content.Context
import com.github.fziraki.daykit.di.ServiceLocator
import com.github.fziraki.daykit.di.ServiceLocator.locationDataSource
import com.github.fziraki.daykit.internal.calendar.AndroidCalendarProvider
import com.github.fziraki.daykit.model.LocationResult
import com.github.fziraki.daykit.model.MyDaySummary
import com.github.fziraki.daykit.model.WeatherInfo
import com.github.fziraki.daykit.providers.CalendarProvider
import com.github.fziraki.daykit.providers.CalendarResult
import com.github.fziraki.daykit.providers.MusicProvider
import com.github.fziraki.daykit.providers.WeatherProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DayKitClient private constructor(
    private val weather: WeatherProvider,
    private val calendar: CalendarProvider,
    private val music: MusicProvider,
) {

    // ===== Aggregation API =====
    suspend fun getMyDay(
        lat: Double? = null,
        lon: Double? = null,
        city: String? = null,
        artist: String? = null
    ): MyDaySummary = coroutineScope {

        val weather = async {
            if (lat != null && lon != null && city != null)
                getWeather(lat, lon, city)
            else null
        }

        val events = async { getTodayEvents() }

        val track = async {
            artist?.let { getRecommendedTrack(it) }
        }

        MyDaySummary(
            calendarResult = events.await(),
            weather = weather.await(),
            recommendedTrack = track.await()
        )
    }


    // ===== Location / Search =====
    suspend fun searchCity(query: String): List<LocationResult> =
        locationDataSource.search(query)

    // ===== Weather =====
    suspend fun getWeather(lat: Double, lon: Double, city: String): WeatherInfo? =
        runCatching {
            weather.getCurrentWeather(lat, lon, city)
        }.getOrNull()

    // ===== Calendar =====
    suspend fun getTodayEvents(): CalendarResult =
        runCatching { calendar.getTodayEvents() }
            .getOrDefault(CalendarResult.Error)

    // ===== Music =====
    suspend fun getRecommendedTrack(artist: String) =
        runCatching { music.getRecommendedTrack(artist) }
            .getOrNull()



    class Builder(private val context: Context) {

        private var weather: WeatherProvider? = null
        private var calendar: CalendarProvider? = null
        private var music: MusicProvider? = null

        fun weather(provider: WeatherProvider) = apply {
            weather = provider
        }

        fun calendar(provider: CalendarProvider) = apply {
            calendar = provider
        }

        fun music(provider: MusicProvider) = apply {
            music = provider
        }

        fun build(): DayKitClient {
            return DayKitClient(
                calendar = calendar ?: AndroidCalendarProvider(context),
                weather = weather ?: ServiceLocator.weatherProvider,
                music = music ?: ServiceLocator.musicProvider,
            )
        }
    }

}