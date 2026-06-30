package com.github.fziraki.daykit.internal.music

import com.github.fziraki.daykit.model.Track
import com.github.fziraki.daykit.providers.MusicProvider

internal class StubMusicProvider : MusicProvider {
    override suspend fun getRecommendedTrack(): Track {
        return Track(
            title = "Good Days",
            artist = "SZA",
            source = "",
            playedAt = 435464654646545
        )
    }
}