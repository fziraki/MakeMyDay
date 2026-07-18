package com.github.fziraki.makemyday.myday

import com.github.fziraki.daykit.model.Track
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result
import com.github.fziraki.makemyday.myday.model.toTrackUi

fun Result<Track, DataError.Network>?.toMusicUiState(): MusicUiState {
    return when (this) {
        null -> MusicUiState.Idle

        is Result.Success -> MusicUiState.Success(data.toTrackUi())

        is Result.Error -> MusicUiState.Error(
            message = error.toUserMessage(),
            action = when (error) {
                DataError.Network.NO_INTERNET,
                DataError.Network.SERVER_ERROR,
                DataError.Network.REQUEST_TIMEOUT -> ErrorAction.RETRY

                DataError.Network.NOT_FOUND -> ErrorAction.EDIT_ARTIST
                else -> ErrorAction.RETRY
            }
        )
    }
}

fun DataError.Network.toUserMessage(): String =
    when (this) {
        DataError.Network.NO_INTERNET -> "No internet connection."
        DataError.Network.REQUEST_TIMEOUT -> "Request timed out."
        DataError.Network.SERVER_ERROR -> "Server error."
        DataError.Network.NOT_FOUND -> "Artist not found."
        else -> {"Something went wrong!"}
    }
