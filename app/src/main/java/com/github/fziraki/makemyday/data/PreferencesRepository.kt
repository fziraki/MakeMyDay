package com.github.fziraki.makemyday.data

import com.github.fziraki.daykit.model.LocationResult
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val onboardingCompleted: Flow<Boolean>
    suspend fun completeOnboarding()

    fun wasAsked(permission: String): Flow<Boolean>
    suspend fun markAsked(permission: String)

    suspend fun saveLocation(location: LocationResult)
    val savedLocation: Flow<LocationResult?>

    suspend fun saveFavoriteArtist(artist: String)
    val savedFavoriteArtist: Flow<String?>

    val themeMode: Flow<String>
    suspend fun setThemeMode(mode: String)
}
