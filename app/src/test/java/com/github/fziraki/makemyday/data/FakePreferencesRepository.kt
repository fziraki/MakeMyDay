package com.github.fziraki.makemyday.data

import com.github.fziraki.daykit.model.LocationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePreferencesRepository : PreferencesRepository {

    private val _onboardingCompleted = MutableStateFlow(false)
    override val onboardingCompleted: Flow<Boolean> = _onboardingCompleted

    private val askedPermissions = mutableSetOf<String>()
    private val _savedLocation = MutableStateFlow<LocationResult?>(null)
    override val savedLocation: Flow<LocationResult?> = _savedLocation

    private val _savedFavoriteArtist = MutableStateFlow<String?>(null)
    override val savedFavoriteArtist: Flow<String?> = _savedFavoriteArtist

    private val _themeMode = MutableStateFlow("system")
    override val themeMode: Flow<String> = _themeMode

    override suspend fun completeOnboarding() {
        _onboardingCompleted.value = true
    }

    override fun wasAsked(permission: String): Flow<Boolean> {
        return MutableStateFlow(permission in askedPermissions)
    }

    override suspend fun markAsked(permission: String) {
        askedPermissions.add(permission)
    }

    override suspend fun saveLocation(location: LocationResult) {
        _savedLocation.value = location
    }

    override suspend fun saveFavoriteArtist(artist: String) {
        _savedFavoriteArtist.value = artist
    }

    override suspend fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }
}
