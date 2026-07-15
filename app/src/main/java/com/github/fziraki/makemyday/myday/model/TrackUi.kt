package com.github.fziraki.makemyday.myday.model

import com.github.fziraki.daykit.model.Track

data class TrackUi(
    val title: String,
    val artist: String?,
    val source: String,
    val playedAt: Long
)

fun Track.toTrackUi(): TrackUi = TrackUi(
    title = title,
    artist = artist,
    source = source,
    playedAt = playedAt
)
