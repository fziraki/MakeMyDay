package com.github.fziraki.makemyday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.github.fziraki.makemyday.myday.MyDayScreen
import com.github.fziraki.makemyday.onboarding.OnboardingScreen
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val preferences: AppPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                val completed by preferences
                    .onboardingCompleted
                    .collectAsState(initial = false)

                val scope = rememberCoroutineScope()

                if (completed) {
                    MyDayScreen()
                } else {
                    OnboardingScreen(
                        onFinish = {
                            scope.launch {
                                preferences.completeOnboarding()
                            }
                        }
                    )
                }
            }
        }
    }
}



