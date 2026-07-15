package com.github.fziraki.daykit.internal.music

import com.github.fziraki.daykit.model.Track
import com.github.fziraki.daykit.network.safeCall
import com.github.fziraki.daykit.providers.MusicProvider
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result
import com.github.fziraki.daykit.result.map
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class DeezerMusicProvider(
    private val client: HttpClient
) : MusicProvider {

    override suspend fun getRecommendedTrack(
        favoriteArtist: String
    ): Result<Track, DataError.Network> {
        val artistId = when (val artistIdResult = searchArtist(favoriteArtist)) {
            is Result.Error -> return artistIdResult
            is Result.Success -> artistIdResult.data
        }

        val targetArtistId = when (val relatedResult = getRelatedArtists(artistId)) {
            is Result.Error -> return relatedResult
            // no related artists -> fall back to the searched artist's own top tracks
            is Result.Success -> relatedResult.data.randomOrNull()?.id
                ?: artistId
        }

        return getTopTrack(targetArtistId)
    }

    private suspend fun searchArtist(name: String): Result<Long, DataError.Network> {
        return deezerGet<DeezerArtistSearchResponseDto>(
            url = "https://api.deezer.com/search/artist",
            params = mapOf("q" to name)
        ).let { result ->
            when (result) {
                is Result.Error -> result
                is Result.Success -> result.data.data.firstOrNull()?.id
                    ?.let { Result.Success(it) }
                    ?: Result.Error(DataError.Network.INVALID_ARTIST)
            }
        }
    }

    private suspend fun getRelatedArtists(
        artistId: Long
    ): Result<List<DeezerArtistDto>, DataError.Network> {
        return deezerGet<DeezerRelatedArtistsResponseDto>(
            url = "https://api.deezer.com/artist/$artistId/related"
        ).map { it.data }
    }

    private suspend fun getTopTrack(artistId: Long): Result<Track, DataError.Network> {
        val result = deezerGet<DeezerTopTracksResponseDto>(
            url = "https://api.deezer.com/artist/$artistId/top",
            params = mapOf("limit" to 20)
        )
        return when (result) {
            is Result.Error -> result
            is Result.Success -> result.data.data.randomOrNull()
                ?.toTrack()
                ?.let { Result.Success(it) }
                ?: Result.Error(DataError.Network.NOT_FOUND)
        }
    }

    private suspend inline fun <reified T> deezerGet(
        url: String,
        params: Map<String, Any?> = mapOf()
    ): Result<T, DataError.Network> {
        return safeCall {
            client.get(url) {
                params.forEach { (key, value) -> parameter(key, value) }
            }
        }
    }


}