package com.github.fziraki.makemyday

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("app_preferences")

class AppPreferences(
    context: Context
) {

    private val dataStore = context.dataStore

    companion object {
        private val ONBOARDING_COMPLETED =
            booleanPreferencesKey("onboarding_completed")
    }

    // -------------------------
    // Onboarding
    // -------------------------

    val onboardingCompleted: Flow<Boolean> =
        dataStore.data.map {
            it[ONBOARDING_COMPLETED] ?: false
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
}