package com.github.fziraki.daykit.providers

import com.github.fziraki.daykit.model.Track
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result

interface MusicProvider {
    suspend fun getRecommendedTrack(favoriteArtist: String): Result<Track, DataError.Network>
}