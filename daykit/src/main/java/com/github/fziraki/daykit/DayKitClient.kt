package com.github.fziraki.daykit

import android.content.Context
import com.github.fziraki.daykit.internal.calendar.AndroidCalendarProvider
import com.github.fziraki.daykit.internal.music.StubMusicProvider
import com.github.fziraki.daykit.internal.todo.StubTodoProvider
import com.github.fziraki.daykit.internal.weather.StubWeatherProvider
import com.github.fziraki.daykit.model.MyDaySummary
import com.github.fziraki.daykit.providers.CalendarResult
import com.github.fziraki.daykit.providers.CalendarProvider
import com.github.fziraki.daykit.providers.MusicProvider
import com.github.fziraki.daykit.providers.TodoProvider
import com.github.fziraki.daykit.providers.WeatherProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DayKitClient private constructor(
    private val calendar: CalendarProvider,
    private val weather: WeatherProvider,
    private val todos: TodoProvider,
    private val music: MusicProvider,
    private val homeLat: Double,
    private val homeLon: Double
) {

    suspend fun getMyDay(): MyDaySummary = coroutineScope {

        val eventsDeferred = async {
            runCatching { calendar.getTodayEvents() }
                .getOrDefault(CalendarResult.Error)
        }

        val weatherDeferred = async {
            runCatching { weather.getCurrentWeather(homeLat, homeLon) }
                .onFailure { if (it is kotlinx.coroutines.CancellationException) throw it }
                .getOrNull()
        }

        val tasksDeferred = async {
            if (todos.isAuthenticated()) {
                runCatching { todos.getPendingTasks() }
                    .onFailure { if (it is kotlinx.coroutines.CancellationException) throw it }
                    .getOrNull()
            } else {
                null
            }
        }

        val trackDeferred = async {
            runCatching { music.getRecommendedTrack() }
                .onFailure { if (it is kotlinx.coroutines.CancellationException) throw it }
                .getOrNull()
        }

        MyDaySummary(
            calendarResult = eventsDeferred.await(),
            weather = weatherDeferred.await(),
            tasks = tasksDeferred.await(),
            recommendedTrack = trackDeferred.await()
        )
    }

    suspend fun completeTask(id: String): Boolean {
        if (!todos.isAuthenticated()) return false
        return runCatching { todos.completeTask(id) }
            .onFailure { if (it is kotlinx.coroutines.CancellationException) throw it }
            .getOrDefault(false)
    }

    class Builder(private val context: Context) {

        private var latitude: Double? = null
        private var longitude: Double? = null

        fun setLocation(latitude: Double, longitude: Double) = apply {
            this.latitude = latitude
            this.longitude = longitude
        }

        fun build(): DayKitClient {
            val lat = requireNotNull(latitude) { "Location must be set before building DayKitClient." }
            val lon = requireNotNull(longitude) { "Location must be set before building DayKitClient." }

            return DayKitClient(
                calendar = AndroidCalendarProvider(context),
                weather = StubWeatherProvider(),
                todos = StubTodoProvider(),
                music = StubMusicProvider(),
                homeLat = lat,
                homeLon = lon
            )
        }
    }
}