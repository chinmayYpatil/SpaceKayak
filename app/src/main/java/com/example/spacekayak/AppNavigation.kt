package com.example.spacekayak

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import com.example.spacekayak.ui.screens.* import com.example.spacekayak.viewmodel.OnboardingViewModel

object Routes {
    const val ONBOARDING = "onboarding"
    const val PRIVACY_CONSENT = "privacy_consent"
}
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val onboardingViewModel: OnboardingViewModel = viewModel()
    val startDestination by onboardingViewModel.startDestination.collectAsState()

    val safeStart = when (startDestination) {
        Routes.ONBOARDING -> Routes.ONBOARDING
        Routes.PRIVACY_CONSENT -> Routes.PRIVACY_CONSENT
        else -> Routes.ONBOARDING
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinished = {
                    onboardingViewModel.completeOnboarding()
                    navController.navigate(Routes.PRIVACY_CONSENT) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PRIVACY_CONSENT) {
            PrivacyConsentScreen(
                onUnderstand = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}