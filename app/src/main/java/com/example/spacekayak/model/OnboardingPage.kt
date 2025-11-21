package com.example.spacekayak.model

import androidx.annotation.DrawableRes
import com.example.spacekayak.R

sealed class OnboardingPage(
    @DrawableRes val image: Int,
    val title: String,
) {
    object First : OnboardingPage(
        image = R.drawable.welcome1,
        title = "Get instant alerts for scam calls, suspicious texts, harmful apps, and breaches",
    )
    object Second : OnboardingPage(
        image = R.drawable.welcome2,
        title = "Protect your family from scams with real-time alerts and safety monitoring",
    )
    object Third : OnboardingPage(
        image = R.drawable.welcome3,
        title = "Recover lost money if tricked, with Shield Protect up to Rs 1,00,000",
    )
}