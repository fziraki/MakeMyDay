package com.github.fziraki.makemyday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.github.fziraki.makemyday.data.PreferencesRepository
import org.koin.android.ext.android.inject
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.github.fziraki.makemyday.ui.theme.MakeMyDayTheme

class MainActivity : ComponentActivity() {

    private val preferences: PreferencesRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        val onboardingCompleted = MutableStateFlow<Boolean?>(null)

        lifecycleScope.launch {
            preferences.onboardingCompleted.collect {
                onboardingCompleted.value = it
            }
        }

        splashScreen.setKeepOnScreenCondition {
            onboardingCompleted.value == null
        }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {

            val scope = rememberCoroutineScope()

            val themeMode by preferences.themeMode.collectAsState(initial = "system")

            val nightMode = when (themeMode) {
                "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                "light" -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }

            AppCompatDelegate.setDefaultNightMode(nightMode)

            val isDarkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            MakeMyDayTheme(darkTheme = isDarkTheme) {
                onboardingCompleted.collectAsState().value?.let {
                    AppNavHost(
                        onboardingCompleted = it,
                        preferences = preferences,
                        themeMode = themeMode,
                        onToggleTheme = {
                            scope.launch {
                                val next = when (themeMode) {
                                    "dark" -> "light"
                                    "light" -> "dark"
                                    else -> if (isDarkTheme) "light" else "dark"
                                }
                                preferences.setThemeMode(next)
                            }
                        }
                    )
                }
            }
        }
    }
}



