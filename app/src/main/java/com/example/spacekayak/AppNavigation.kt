package com.example.spacekayak

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.spacekayak.ui.screens.*
import com.example.spacekayak.viewmodel.OnboardingViewModel
import com.example.spacekayak.viewmodel.AuthViewModel

object Routes {
    const val ONBOARDING = "onboarding"
    const val PRIVACY_CONSENT = "privacy_consent"
}

fun NavHostController.navigateToPhoneVerificationModal() {
    this.navigate(Routes.ONBOARDING) {
        popUpTo(Routes.PRIVACY_CONSENT) { inclusive = true }
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val onboardingViewModel: OnboardingViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val startDestination by onboardingViewModel.startDestination.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                authViewModel = authViewModel,
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
                    authViewModel.showPhoneVerificationModal()
                    navController.navigateToPhoneVerificationModal()
                }
            )
        }
    }
}