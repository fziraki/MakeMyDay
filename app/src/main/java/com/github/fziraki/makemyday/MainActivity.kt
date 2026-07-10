package com.github.fziraki.makemyday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import androidx.compose.runtime.collectAsState
import com.github.fziraki.makemyday.ui.theme.MakeMyDayTheme

class MainActivity : ComponentActivity() {

    private val preferences: AppPreferences by inject()

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

        super.onCreate(savedInstanceState)



        setContent {
            MakeMyDayTheme {
                onboardingCompleted.collectAsState().value?.let {
                    AppNavHost(it, preferences)
                }
            }
        }
    }
}



