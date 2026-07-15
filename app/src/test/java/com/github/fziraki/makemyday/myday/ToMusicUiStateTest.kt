package com.github.fziraki.makemyday.myday

import com.github.fziraki.daykit.model.Track
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToMusicUiStateTest {

    private val sampleTrack = Track(
        title = "Test Song",
        artist = "Test Artist",
        source = "https://example.com/audio.mp3",
        playedAt = 1000L
    )

    @Test
    fun `null input maps to Idle`() {
        val result: Result<Track, DataError.Network>? = null
        assertEquals(MusicUiState.Idle, result.toMusicUiState())
    }

    @Test
    fun `success maps to Success with TrackUi`() {
        val result: Result<Track, DataError.Network>? = Result.Success(sampleTrack)
        val state = result.toMusicUiState()
        assertTrue(state is MusicUiState.Success)
        val success = state as MusicUiState.Success
        assertEquals("Test Song", success.track.title)
        assertEquals("Test Artist", success.track.artist)
        assertEquals("https://example.com/audio.mp3", success.track.source)
    }

    @Test
    fun `NO_INTERNET maps to Error with retry action`() {
        val result: Result<Track, DataError.Network>? = Result.Error(DataError.Network.NO_INTERNET)
        val state = result.toMusicUiState()
        assertTrue(state is MusicUiState.Error)
        val error = state as MusicUiState.Error
        assertEquals("No internet connection.", error.message)
        assertEquals(ErrorAction.RETRY, error.action)
    }

    @Test
    fun `REQUEST_TIMEOUT maps to Error with retry action`() {
        val result: Result<Track, DataError.Network>? = Result.Error(DataError.Network.REQUEST_TIMEOUT)
        val state = result.toMusicUiState()
        assertTrue(state is MusicUiState.Error)
        assertEquals(ErrorAction.RETRY, (state as MusicUiState.Error).action)
    }

    @Test
    fun `SERVER_ERROR maps to Error with retry action`() {
        val result: Result<Track, DataError.Network>? = Result.Error(DataError.Network.SERVER_ERROR)
        val state = result.toMusicUiState()
        assertTrue(state is MusicUiState.Error)
        assertEquals(ErrorAction.RETRY, (state as MusicUiState.Error).action)
    }

    @Test
    fun `INVALID_ARTIST maps to Error with edit_artist action`() {
        val result: Result<Track, DataError.Network>? = Result.Error(DataError.Network.INVALID_ARTIST)
        val state = result.toMusicUiState()
        assertTrue(state is MusicUiState.Error)
        assertEquals(ErrorAction.EDIT_ARTIST, (state as MusicUiState.Error).action)
    }

    @Test
    fun `toUserMessage maps correctly`() {
        assertEquals("No internet connection.", DataError.Network.NO_INTERNET.toUserMessage())
        assertEquals("Request timed out.", DataError.Network.REQUEST_TIMEOUT.toUserMessage())
        assertEquals("Server error.", DataError.Network.SERVER_ERROR.toUserMessage())
        assertEquals("Artist not found.", DataError.Network.INVALID_ARTIST.toUserMessage())
    }
}
