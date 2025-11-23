package com.example.spacekayak.data

import android.content.Context
import androidx.core.content.edit

class LocalStorage(context: Context) {
    private val pref = context.getSharedPreferences("onboarding_pref", Context.MODE_PRIVATE)

    fun saveOnboarding(completed: Boolean) {
        pref.edit { putBoolean("onboarding_complete",completed) }
    }
}