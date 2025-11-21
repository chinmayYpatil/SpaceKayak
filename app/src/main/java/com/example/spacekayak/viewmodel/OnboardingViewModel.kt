package com.example.spacekayak.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.spacekayak.data.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel(app: Application) : AndroidViewModel(app) {
    private val storage = LocalStorage(app)

    private val _startDestination = MutableStateFlow("onboarding")
    val startDestination = _startDestination.asStateFlow()

    init {
        if (storage.isOnboardingFinished()) {
            _startDestination.value = "login"
        }
    }

    fun completeOnboarding() {
        storage.saveOnboarding(true)
    }
}