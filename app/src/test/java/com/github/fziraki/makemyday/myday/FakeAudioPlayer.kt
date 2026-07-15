package com.github.fziraki.makemyday.myday

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAudioPlayer : AudioPlayer {

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    var lastPlayedUrl: String? = null

    override fun play(url: String) {
        lastPlayedUrl = url
        _isPlaying.value = true
    }

    override fun pause() {
        _isPlaying.value = false
    }

    override fun release() {
        _isPlaying.value = false
    }
}
