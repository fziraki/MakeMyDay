package com.github.fziraki.daykit.providers

import com.github.fziraki.daykit.model.Track

interface MusicProvider {
    suspend fun getRecommendedTrack(): Track?
}