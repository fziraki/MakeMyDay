package com.github.fziraki.makemyday.myday

import com.github.fziraki.daykit.DayKitClient
import com.github.fziraki.daykit.model.CalendarEvent
import com.github.fziraki.daykit.model.LocationResult
import com.github.fziraki.daykit.model.MyDaySummary
import com.github.fziraki.daykit.model.Track
import com.github.fziraki.daykit.model.WeatherInfo
import com.github.fziraki.daykit.providers.CalendarProvider
import com.github.fziraki.daykit.providers.LocationSearchRepository
import com.github.fziraki.daykit.providers.MusicProvider
import com.github.fziraki.daykit.providers.WeatherProvider
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result
import com.github.fziraki.makemyday.data.FakePreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class MyDayViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakePreferences: FakePreferencesRepository
    private lateinit var fakeAudioPlayer: FakeAudioPlayer
    private lateinit var fakeWeatherProvider: FakeWeatherProvider
    private lateinit var fakeCalendarProvider: FakeCalendarProvider
    private lateinit var fakeMusicProvider: FakeMusicProvider

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePreferences = FakePreferencesRepository()
        fakeAudioPlayer = FakeAudioPlayer()
        fakeWeatherProvider = FakeWeatherProvider()
        fakeCalendarProvider = FakeCalendarProvider()
        fakeMusicProvider = FakeMusicProvider()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createClient(): DayKitClient {
        return DayKitClient.Builder(mock())
            .weather(fakeWeatherProvider)
            .calendar(fakeCalendarProvider)
            .music(fakeMusicProvider)
            .locationSearch(FakeLocationSearchRepository())
            .build()
    }

    private fun createViewModel(): MyDayViewModel {
        return MyDayViewModel(
            client = createClient(),
            preferences = fakePreferences,
            audioPlayer = fakeAudioPlayer
        )
    }

    @Test
    fun `initial state is loading`() = runTest(testDispatcher) {
        val vm = createViewModel()
        assertEquals(true, vm.state.value.isLoading)
    }

    @Test
    fun `loadDay sets loading false and shows weather and events`() = runTest(testDispatcher) {
        fakePreferences.saveLocation(
            LocationResult(city = "London", country = "UK", lat = 51.5, lon = -0.13)
        )
        fakeWeatherProvider.result = Result.Success(
            WeatherInfo(tempC = 15.0, condition = "Cloudy", feelsLikeC = 13.0, city = "London")
        )
        fakeCalendarProvider.result = Result.Success(
            listOf(
                CalendarEvent(title = "Meeting", startTime = 0L, endTime = 3600000L, isAllDay = false)
            )
        )

        val vm = createViewModel()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.state.value
        assertFalse(state.isLoading)
        assertFalse(state.locationNotSet)
        val weather = state.weather!!
        assertEquals(15.0, weather.tempC, 0.01)
        assertEquals("Cloudy", weather.condition)
        assertEquals("London", weather.city)
        assertEquals(1, state.events.size)
        assertEquals("Meeting", state.events[0].title)
    }

    @Test
    fun `no location sets locationNotSet true`() = runTest(testDispatcher) {
        val vm = createViewModel()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.state.value
        assertFalse(state.isLoading)
        assertTrue(state.locationNotSet)
    }

    @Test
    fun `calendar permission denied sets flag`() = runTest(testDispatcher) {
        fakePreferences.saveLocation(
            LocationResult(city = "London", country = "UK", lat = 51.5, lon = -0.13)
        )
        fakeCalendarProvider.result = Result.Error(DataError.Local.PERMISSION_DENIED)
        fakeWeatherProvider.result = Result.Success(
            WeatherInfo(tempC = 10.0, condition = "Sunny", feelsLikeC = 8.0, city = "London")
        )

        val vm = createViewModel()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.state.value
        assertTrue(state.calendarPermissionDenied)
        assertFalse(state.calendarError)
    }

    @Test
    fun `getMusic updates musicUiState to success`() = runTest(testDispatcher) {
        fakePreferences.saveFavoriteArtist("Test Artist")
        fakeMusicProvider.result = Result.Success(
            Track(title = "Song", artist = "Test Artist", source = "url", playedAt = 0L)
        )

        val vm = createViewModel()
        vm.onAction(MyDayAction.OnGetTrackClick)

        testDispatcher.scheduler.advanceUntilIdle()

        val musicState = vm.state.value.musicUiState
        assertTrue(musicState is MusicUiState.Success)
        assertEquals("Song", (musicState as MusicUiState.Success).track.title)
    }

    @Test
    fun `onPlayPause delegates to audioPlayer`() = runTest(testDispatcher) {
        val vm = createViewModel()

        vm.onAction(MyDayAction.OnPlayPause("https://example.com/audio.mp3"))

        assertEquals("https://example.com/audio.mp3", fakeAudioPlayer.lastPlayedUrl)
        assertTrue(fakeAudioPlayer.isPlaying.value)

        vm.onAction(MyDayAction.OnPlayPause(""))

        assertFalse(fakeAudioPlayer.isPlaying.value)
    }

    @Test
    fun `OnArtistChange updates inputArtist`() = runTest(testDispatcher) {
        val vm = createViewModel()

        vm.onAction(MyDayAction.OnArtistChange("Arctic Monkeys"))

        assertEquals("Arctic Monkeys", vm.state.value.inputArtist)
    }
}

class FakeWeatherProvider : WeatherProvider {
    var result: Result<WeatherInfo, DataError.Network> = Result.Error(DataError.Network.SERVER_ERROR)

    override suspend fun getCurrentWeather(lat: Double, lon: Double, city: String): Result<WeatherInfo, DataError.Network> {
        return result
    }
}

class FakeCalendarProvider : CalendarProvider {
    var result: Result<List<CalendarEvent>, DataError.Local> = Result.Error(DataError.Local.UNKNOWN)

    override suspend fun getTodayEvents(): Result<List<CalendarEvent>, DataError.Local> {
        return result
    }
}

class FakeMusicProvider : MusicProvider {
    var result: Result<Track, DataError.Network> = Result.Error(DataError.Network.SERVER_ERROR)

    override suspend fun getRecommendedTrack(favoriteArtist: String): Result<Track, DataError.Network> {
        return result
    }
}

class FakeLocationSearchRepository : LocationSearchRepository {
    override suspend fun search(query: String): List<LocationResult> = emptyList()
}
