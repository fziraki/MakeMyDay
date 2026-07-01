package com.github.fziraki.daykit.internal.music

import kotlinx.serialization.Serializable

// feature:music:data
@Serializable
data class DeezerArtistSearchResponseDto(val data: List<DeezerArtistDto> = emptyList())

@Serializable
data class DeezerRelatedArtistsResponseDto(val data: List<DeezerArtistDto> = emptyList())

@Serializable
data class DeezerArtistDto(val id: Long, val name: String)

@Serializable
data class DeezerTopTracksResponseDto(val data: List<DeezerTrackDto> = emptyList())

@Serializable
data class DeezerTrackDto(
    val title: String,
    val preview: String, // 30s mp3 preview URL — no auth needed to stream it
    val artist: DeezerTrackArtistDto
)

@Serializable
data class DeezerTrackArtistDto(val name: String)