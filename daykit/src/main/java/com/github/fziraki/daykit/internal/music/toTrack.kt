package com.github.fziraki.daykit.internal.music

import com.github.fziraki.daykit.model.Track

internal fun DeezerTrackDto.toTrack(): Track = Track(
    title = title,
    artist = artist.name,
    source = preview, // swap to a `link` field instead if you want the Deezer page, not a playable preview
    playedAt = System.currentTimeMillis()
)