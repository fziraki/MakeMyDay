package com.github.fziraki.makemyday.locationsearch

import com.github.fziraki.daykit.DayKitClient
import com.github.fziraki.daykit.model.LocationResult
import com.github.fziraki.daykit.providers.LocationProvider
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result
import com.github.fziraki.makemyday.data.FakePreferencesRepository
import com.github.fziraki.makemyday.locationsearch.model.LocationResultUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class LocationSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakePreferences: FakePreferencesRepository
    private lateinit var fakeSearchRepo: FakeLocationProvider

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePreferences = FakePreferencesRepository()
        fakeSearchRepo = FakeLocationProvider()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): LocationSearchViewModel {
        return LocationSearchViewModel(
            client = DayKitClient.Builder(mock())
                .location(fakeSearchRepo)
                .weather(FakeWeatherForSearchProvider())
                .calendar(FakeCalendarForSearchProvider())
                .music(FakeMusicForSearchProvider())
                .build(),
            preferences = fakePreferences
        )
    }

    @Test
    fun `initial state has empty query and no results`() = runTest(testDispatcher) {
        val vm = createViewModel()
        assertEquals("", vm.state.value.query)
        assertTrue(vm.state.value.results.isEmpty())
    }

    @Test
    fun `selecting location saves to preferences`() = runTest(testDispatcher) {
        val vm = createViewModel()
        val location = LocationResultUi(
            city = "Paris", country = "France", lat = 48.85, lon = 2.34
        )

        vm.onAction(LocationSearchAction.LocationSelected(location))
        testDispatcher.scheduler.advanceUntilIdle()

        val saved = fakePreferences.savedLocation.first()
        assertNotNull(saved)
        assertEquals("Paris", saved?.city)
        assertEquals("France", saved?.country)
    }
}

class FakeLocationProvider : LocationProvider {
    var results = listOf(
        LocationResult("London", "UK", 51.5, -0.13),
        LocationResult("Paris", "France", 48.85, 2.34)
    )

    override suspend fun search(query: String): Result<List<LocationResult>, DataError.Network> {
        return Result.Success(results.filter { it.city.contains(query, ignoreCase = true) })
    }
}

class FakeWeatherForSearchProvider : com.github.fziraki.daykit.providers.WeatherProvider {
    override suspend fun getCurrentWeather(
        lat: Double, lon: Double, city: String
    ): com.github.fziraki.daykit.result.Result<com.github.fziraki.daykit.model.WeatherInfo, com.github.fziraki.daykit.result.DataError.Network> {
        return com.github.fziraki.daykit.result.Result.Error(com.github.fziraki.daykit.result.DataError.Network.SERVER_ERROR)
    }
}

class FakeCalendarForSearchProvider : com.github.fziraki.daykit.providers.CalendarProvider {
    override suspend fun getTodayEvents(): com.github.fziraki.daykit.result.Result<List<com.github.fziraki.daykit.model.CalendarEvent>, com.github.fziraki.daykit.result.DataError.Local> {
        return com.github.fziraki.daykit.result.Result.Error(com.github.fziraki.daykit.result.DataError.Local.UNKNOWN)
    }
}

class FakeMusicForSearchProvider : com.github.fziraki.daykit.providers.MusicProvider {
    override suspend fun getRecommendedTrack(favoriteArtist: String): com.github.fziraki.daykit.result.Result<com.github.fziraki.daykit.model.Track, com.github.fziraki.daykit.result.DataError.Network> {
        return com.github.fziraki.daykit.result.Result.Error(com.github.fziraki.daykit.result.DataError.Network.SERVER_ERROR)
    }
}
