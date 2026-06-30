package com.github.fziraki.makemyday.onboarding


data class SetupState(
    val isCalendarGranted: Boolean = false,
    val artistInput: String = ""
)

sealed interface SetupAction {
    data class ArtistChanged(val value: String) : SetupAction
    data class SetCalendarGranted(val value: Boolean) : SetupAction
}