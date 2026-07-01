package com.github.fziraki.makemyday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {

    private val preferences: AppPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        val onboardingCompleted = MutableStateFlow<Boolean?>(null)

        lifecycleScope.launch {
            preferences.onboardingCompleted.collect {
                onboardingCompleted.value = it
            }
        }

        splashScreen.setKeepOnScreenCondition {
            onboardingCompleted.value == null
        }

        setContent {
            MaterialTheme {
                onboardingCompleted.collectAsState().value?.let {
                    AppNavHost(it, preferences)
                }
            }
        }
    }
}



