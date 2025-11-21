package com.example.spacekayak.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.spacekayak.data.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.spacekayak.Routes

class OnboardingViewModel(app: Application) : AndroidViewModel(app) {
    private val storage = LocalStorage(app)

    private val _startDestination = MutableStateFlow(Routes.ONBOARDING)
    val startDestination = _startDestination.asStateFlow()

    fun completeOnboarding() {
        storage.saveOnboarding(true)
    }
}