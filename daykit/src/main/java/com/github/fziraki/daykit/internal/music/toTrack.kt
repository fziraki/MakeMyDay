package com.github.fziraki.daykit.internal.music

import com.github.fziraki.daykit.model.Track

internal fun DeezerTrackDto.toTrack(): Track = Track(
    title = title,
    artist = artist.name,
    source = preview,
    playedAt = System.currentTimeMillis()
)