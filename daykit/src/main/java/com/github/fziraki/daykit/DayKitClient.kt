package com.github.fziraki.daykit

import android.content.Context
import com.github.fziraki.daykit.internal.calendar.AndroidCalendarProvider
import com.github.fziraki.daykit.internal.music.DeezerMusicProvider
import com.github.fziraki.daykit.internal.weather.NominatimDataSource
import com.github.fziraki.daykit.internal.weather.OpenMeteoWeatherProvider
import com.github.fziraki.daykit.model.LocationResult
import com.github.fziraki.daykit.model.MyDaySummary
import com.github.fziraki.daykit.model.Track
import com.github.fziraki.daykit.model.WeatherInfo
import com.github.fziraki.daykit.network.HttpClientFactory
import com.github.fziraki.daykit.network.getEngine
import com.github.fziraki.daykit.providers.CalendarProvider
import com.github.fziraki.daykit.providers.LocationSearchRepository
import com.github.fziraki.daykit.providers.MusicProvider
import com.github.fziraki.daykit.providers.WeatherProvider
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result
import io.ktor.client.HttpClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DayKitClient private constructor(
    private val weather: WeatherProvider,
    private val calendar: CalendarProvider,
    private val music: MusicProvider,
    private val locationSearch: LocationSearchRepository,
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
        locationSearch.search(query)

    // ===== Weather =====
    suspend fun getWeather(lat: Double, lon: Double, city: String): Result<WeatherInfo, DataError.Network> =
        weather.getCurrentWeather(lat, lon, city)

    // ===== Calendar =====
    suspend fun getTodayEvents(): Result<List<com.github.fziraki.daykit.model.CalendarEvent>, DataError.Local> =
        calendar.getTodayEvents()

    // ===== Music =====
    suspend fun getRecommendedTrack(artist: String): Result<Track, DataError.Network> =
        music.getRecommendedTrack(artist)

    // ===== Builder =====
    class Builder(private val context: Context) {

        private var weather: WeatherProvider? = null
        private var calendar: CalendarProvider? = null
        private var music: MusicProvider? = null
        private var locationSearch: LocationSearchRepository? = null

        fun weather(provider: WeatherProvider) = apply {
            weather = provider
        }

        fun calendar(provider: CalendarProvider) = apply {
            calendar = provider
        }

        fun music(provider: MusicProvider) = apply {
            music = provider
        }

        fun locationSearch(provider: LocationSearchRepository) = apply {
            locationSearch = provider
        }

        fun build(): DayKitClient {
            val httpClient by lazy {
                HttpClientFactory.create(getEngine())
            }
            return DayKitClient(
                calendar = calendar ?: AndroidCalendarProvider(context),
                weather = weather ?: OpenMeteoWeatherProvider(httpClient),
                music = music ?: DeezerMusicProvider(httpClient),
                locationSearch = locationSearch ?: NominatimDataSource(httpClient),
            )
        }
    }

}