package com.github.fziraki.makemyday

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.fziraki.daykit.model.LocationResult
import com.github.fziraki.makemyday.data.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("app_preferences")

class AppPreferences(
    context: Context
) : PreferencesRepository {

    private val dataStore = context.dataStore

    companion object {
        private val ONBOARDING_COMPLETED =
            booleanPreferencesKey("onboarding_completed")

        private val SAVED_LOCATION =
            stringPreferencesKey("saved_location")

        private val FAVORITE_ARTIST =
            stringPreferencesKey("favorite_artist")

        private val THEME_MODE =
            stringPreferencesKey("theme_mode")

        private val json = Json {
            ignoreUnknownKeys = true
        }
    }

    // -------------------------
    // Onboarding
    // -------------------------

    override val onboardingCompleted: Flow<Boolean> =
        dataStore.data.map {
            it[ONBOARDING_COMPLETED]?:false
        }.distinctUntilChanged()

    override suspend fun completeOnboarding() {
        dataStore.edit {
            it[ONBOARDING_COMPLETED] = true
        }
    }

    // -------------------------
    // Generic permission flags
    // -------------------------

    override fun wasAsked(permission: String): Flow<Boolean> =
        dataStore.data.map {
            it[booleanPreferencesKey(permission)] ?: false
        }

    override suspend fun markAsked(permission: String) {
        dataStore.edit {
            it[booleanPreferencesKey(permission)] = true
        }
    }

    override suspend fun saveLocation(location: LocationResult) {
        dataStore.edit {
            it[SAVED_LOCATION] = json.encodeToString(location)
        }
    }

    override val savedLocation: Flow<LocationResult?> =
        dataStore.data.map { preferences ->
            preferences[SAVED_LOCATION]?.let {
                runCatching {
                    json.decodeFromString<LocationResult>(it)
                }.getOrNull()
            }
        }.distinctUntilChanged()

    override suspend fun saveFavoriteArtist(artist: String) {
        dataStore.edit {
            it[FAVORITE_ARTIST] = artist
        }
    }

    override val savedFavoriteArtist: Flow<String?> =
        dataStore.data.map { preferences ->
            preferences[FAVORITE_ARTIST]
        }.distinctUntilChanged()

    // -------------------------
    // Theme Mode
    // -------------------------

    override val themeMode: Flow<String> =
        dataStore.data.map { it[THEME_MODE] ?: "system" }.distinctUntilChanged()

    override suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[THEME_MODE] = mode }
    }
}