package com.github.fziraki.daykit

import android.content.Context
import com.github.fziraki.daykit.di.ServiceLocator
import com.github.fziraki.daykit.internal.calendar.AndroidCalendarProvider
import com.github.fziraki.daykit.internal.music.StubMusicProvider
import com.github.fziraki.daykit.internal.todo.StubTodoProvider
import com.github.fziraki.daykit.model.LocationResult
import com.github.fziraki.daykit.model.MyDaySummary
import com.github.fziraki.daykit.providers.CalendarProvider
import com.github.fziraki.daykit.providers.CalendarResult
import com.github.fziraki.daykit.providers.MusicProvider
import com.github.fziraki.daykit.providers.TodoProvider
import com.github.fziraki.daykit.providers.WeatherProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.cancellation.CancellationException

class DayKitClient private constructor(
    private val calendar: CalendarProvider,
    private val weather: WeatherProvider,
    private val todos: TodoProvider,
    private val music: MusicProvider
) {

    suspend fun searchCity(query: String): List<LocationResult>{
        return ServiceLocator.locationDataSource.search(query)
    }

    suspend fun getMyDay(lat: Double? = null, lon: Double? = null, city: String? = null): MyDaySummary = coroutineScope {


        val eventsDeferred = async {
            runCatching { calendar.getTodayEvents() }
                .getOrDefault(CalendarResult.Error)
        }

        val weatherDeferred = async {
            if (lat != null && lon != null && city != null){
                runCatching { weather.getCurrentWeather(lat = lat, lon = lon, city = city) }
                    .onFailure { if (it is CancellationException) throw it }
                    .getOrNull()
            }else{
                null
            }
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

        fun build(): DayKitClient {

            return DayKitClient(
                calendar = AndroidCalendarProvider(context),
                weather = ServiceLocator.weatherProvider,
                todos = StubTodoProvider(),
                music = StubMusicProvider()
            )
        }
    }
}