package com.github.fziraki.makemyday.myday

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExoAudioPlayer(
    private val context: Context
) : AudioPlayer {

    private var player: ExoPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private fun ensurePlayer(): ExoPlayer {
        if (player == null) {
            player = ExoPlayer.Builder(context).build().apply {

                addListener(object : Player.Listener {

                    override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                        _isPlaying.value = isPlayingNow
                    }

                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_ENDED) {
                            _isPlaying.value = false
                        }
                    }
                })
            }
        }
        return player!!
    }

    override fun play(url: String) {
        val exo = ensurePlayer()

        val mediaItem = MediaItem.fromUri(url)
        exo.setMediaItem(mediaItem)
        exo.prepare()
        exo.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun stop() {
        player?.stop()
        _isPlaying.value = false
    }

    override fun release() {
        player?.release()
        player = null
        _isPlaying.value = false
    }
}