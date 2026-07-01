package com.github.fziraki.makemyday

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.fziraki.daykit.model.LocationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("app_preferences")

class AppPreferences(
    context: Context
) {

    private val dataStore = context.dataStore

    companion object {
        private val ONBOARDING_COMPLETED =
            booleanPreferencesKey("onboarding_completed")

        private val SAVED_LOCATION =
            stringPreferencesKey("saved_location")

        private val FAVORITE_ARTIST =
            stringPreferencesKey("favorite_artist")

        private val json = Json {
            ignoreUnknownKeys = true
        }
    }

    // -------------------------
    // Onboarding
    // -------------------------

    val onboardingCompleted: Flow<Boolean> =
        dataStore.data.map {
            it[ONBOARDING_COMPLETED]?:false
        }

    suspend fun completeOnboarding() {
        dataStore.edit {
            it[ONBOARDING_COMPLETED] = true
        }
    }

    // -------------------------
    // Generic permission flags
    // -------------------------

    fun wasAsked(permission: String): Flow<Boolean> =
        dataStore.data.map {
            it[booleanPreferencesKey(permission)] ?: false
        }

    suspend fun markAsked(permission: String) {
        dataStore.edit {
            it[booleanPreferencesKey(permission)] = true
        }
    }

    suspend fun saveLocation(location: LocationResult) {
        dataStore.edit {
            it[SAVED_LOCATION] = json.encodeToString(location)
        }
    }

    val savedLocation: Flow<LocationResult?> =
        dataStore.data.map { preferences ->
            preferences[SAVED_LOCATION]?.let {
                runCatching {
                    json.decodeFromString<LocationResult>(it)
                }.getOrNull()
            }
        }

    suspend fun saveFavoriteArtist(artist: String) {
        dataStore.edit {
            it[FAVORITE_ARTIST] = artist
        }
    }

    val savedFavoriteArtist: Flow<String?> =
        dataStore.data.map { preferences ->
            preferences[FAVORITE_ARTIST]
        }
}