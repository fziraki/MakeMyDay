package com.github.fziraki.daykit.model

data class Track(
    val title: String,
    val artist: String?,
    val source: String,
    val playedAt: Long
)