package com.github.fziraki.makemyday

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.fziraki.makemyday.locationsearch.LocationSearchScreen
import com.github.fziraki.makemyday.myday.MyDayScreen
import com.github.fziraki.makemyday.onboarding.OnboardingScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AppNavHost(preferences: AppPreferences = koinInject()) {

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val completed by preferences.onboardingCompleted.collectAsState(initial = false)

    val startDestination = if (completed) Main else Onboarding

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Onboarding> {
            OnboardingScreen(
                onFinish = {
                    scope.launch { preferences.completeOnboarding() }
                    navController.navigate(Main) {
                        popUpTo<Onboarding> { inclusive = true }
                    }
                },
                onNavigateToLocationSearch = {
                    navController.navigate(LocationSearch)
                }
            )
        }

        composable<Main> {
            MyDayScreen(
                onNavigateToLocationSearch = {
                    navController.navigate(LocationSearch)
                }
            )
        }

        composable<LocationSearch> {
            LocationSearchScreen(
                onLocationSelected = {
                    navController.navigateUp()
                }
            )
        }
    }
}
