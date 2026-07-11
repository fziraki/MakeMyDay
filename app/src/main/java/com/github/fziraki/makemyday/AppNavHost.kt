package com.github.fziraki.makemyday

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.fziraki.makemyday.locationsearch.LocationSearchScreen
import com.github.fziraki.makemyday.myday.MyDayScreen
import com.github.fziraki.makemyday.onboarding.OnboardingScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    onboardingCompleted: Boolean,
    preferences: AppPreferences,
    themeMode: String,
    onToggleTheme: () -> Unit,
) {

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val startDestination = if (onboardingCompleted) Main else Onboarding

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
                },
                themeMode = themeMode,
                onToggleTheme = onToggleTheme
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
