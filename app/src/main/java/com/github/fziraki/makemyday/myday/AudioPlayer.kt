package com.github.fziraki.makemyday.myday

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val isPlaying: StateFlow<Boolean>

    fun play(url: String)
    fun pause()
    fun stop()
    fun release()
}