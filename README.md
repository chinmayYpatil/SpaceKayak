# 🚀 SpaceKayak: Mobile Security App

This is an Android application built using **Jetpack Compose** and structured with the **MVVM (Model-View-ViewModel)** architectural pattern. It features an onboarding flow, a privacy consent screen, and a phone verification modal powered by **Supabase Auth**.

---

## 🏗️ Project Architecture (MVVM)

The project adheres to the MVVM pattern for clean separation of concerns:

| Component | Files | Role |
| :--- | :--- | :--- |
| **View (UI)** | `spacekayak/ui/screens/*Screen.kt` | Composable functions that observe state from the ViewModel and delegate user events. |
| **ViewModel** | `spacekayak/viewmodel/*ViewModel.kt` | Manages UI-related data (exposed as `StateFlow`s) and contains business logic (e.g., authentication flow, navigation state). |
| **Model (Data)** | `spacekayak/data/*kt`, `spacekayak/model/*kt` | Handles data operations, including local persistence (`LocalStorage.kt`), remote database access (`SupabaseManager.kt`), and data structures (`OnboardingPage.kt`). |

---

## 🛠️ Prerequisites

* **Android Studio Jellyfish | 2023.3.1 or newer**
* **Kotlin**
* **A Supabase Project:** Required for the Phone OTP authentication feature.

---

## 🔑 Supabase and Phone Authentication Setup

The application uses the `supabase-kotlin` client for user authentication (Phone OTP). This requires two essential configuration steps.

### 1. Update Supabase Credentials

The application's Supabase client is initialized in `spacekayak/data/SupabaseManager.kt`. You **must** replace the placeholder constants with your actual project keys.

1.  Create or access your project on the [Supabase Dashboard](https://app.supabase.com/).
2.  Navigate to **Project Settings** > **API**.
3.  Copy your **Project URL** and the **`anon` public key**.
4.  Update `spacekayak/data/SupabaseManager.kt`:

    ```kotlin
    // spacekayak/data/SupabaseManager.kt
    
    private const val SUPABASE_URL = "YOUR_PROJECT_URL_HERE" 
    private const val SUPABASE_ANON_KEY = "YOUR_ANON_PUBLIC_KEY_HERE"
    
    // ... rest of the file
    ```

### 2. Configure SMS Provider (Twilio/Vonage)

Supabase requires an external SMS provider (like Twilio or Vonage) to send OTP codes, which is necessary for the phone verification flow used in `AuthViewModel.kt`.

1.  In your Supabase Dashboard, navigate to **Authentication** > **Providers** and ensure **Phone** Sign-in is enabled.
2.  Go to **Authentication** > **Settings** > **SMS Templates**.
3.  Configure your chosen provider (e.g., **Twilio, Vonage**). You will need to provide your provider's credentials (e.g., Account SID, Auth Token, Messaging Service SID).

For detailed instructions on setting up phone login with Kotlin and Twilio in Supabase, refer to the official documentation:

* **Supabase Phone Login Guide (Kotlin/Twilio):**
  [https://supabase.com/docs/guides/auth/phone-login?queryGroups=language&language=kotlin&showSmsProvider=Twilio](https://supabase.com/docs/guides/auth/phone-login?queryGroups=language&language=kotlin&showSmsProvider=Twilio)

### Supabase Code Flow

The authentication logic is encapsulated in `spacekayak/viewmodel/AuthViewModel.kt`:

| ViewModel Function | Supabase SDK Action | Description |
| :--- | :--- | :--- |
| `sendOtp()` | `supabase.auth.signInWith(OTP)` | Sends a one-time password (OTP) via SMS to the configured provider. |
| `verifyOtp()` | `supabase.auth.verifyPhoneOtp(...)` | Verifies the 6-digit code entered by the user to securely log them in. |

---

## 🚀 Running the Application

1.  **Open the project** in Android Studio.
2.  Ensure you have completed the **Supabase setup** (Steps 1 & 2 above).
3.  Run the application on an Android Emulator or a physical device.

The app flow:
`OnboardingScreen` ➡️ `PrivacyConsentScreen` ➡️ `AuthFlowModal` (for phone verification)
