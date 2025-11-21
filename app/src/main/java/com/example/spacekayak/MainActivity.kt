package com.example.spacekayak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.spacekayak.ui.screens.OnboardingScreen
import com.example.spacekayak.ui.theme.SpaceKayakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpaceKayakTheme {
                var showOnboarding by remember { mutableStateOf(true) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { innerPadding ->
                        if (showOnboarding) {
                            OnboardingScreen(
                                onFinished = {
                                    showOnboarding = false
                                }
                            )
                        } else {
                            Text(
                                text = "Welcome to the App! Proceeding to Auth...",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                )
            }
        }
    }
}