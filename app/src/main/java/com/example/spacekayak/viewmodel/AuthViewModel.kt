package com.example.spacekayak.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacekayak.data.supabase // Ensure this is a valid import for your Supabase client
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private var timerJob: Job? = null

    // 0: Phone Input, 1: OTP Input, 2: Verification Success
    private val _authState = MutableStateFlow(0)
    val authState: StateFlow<Int> = _authState

    private val _showPhoneVerificationModal = MutableStateFlow(false)
    val showPhoneVerificationModal: StateFlow<Boolean> = _showPhoneVerificationModal

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _otpInput = MutableStateFlow("")
    val otpInput: StateFlow<String> = _otpInput

    // State for OTP validation error (used by UI for red border and error text)
    private val _otpError = MutableStateFlow(false)
    val otpError: StateFlow<Boolean> = _otpError

    private val _resendTimer = MutableStateFlow(0)
    val resendTimer: StateFlow<Int> = _resendTimer

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private fun getFullPhoneNumber(): String = "+91${_phoneNumber.value}"

    fun showPhoneVerificationModal() {
        _showPhoneVerificationModal.value = true
        _errorMessage.value = null
    }

    fun hidePhoneVerificationModal() {
        _showPhoneVerificationModal.value = false
        _authState.value = 0
        _otpInput.value = ""
        _errorMessage.value = null
        _otpError.value = false // Reset OTP error state
    }

    // New function for the button and delayed action
    fun completeVerificationFlow() {
        // This function will be called by the "Continue to App" button OR automatically after a delay
        hidePhoneVerificationModal()
    }

    fun updatePhoneNumber(newNumber: String) {
        if (newNumber.length <= 10) _phoneNumber.value = newNumber
    }

    /**
     * Updates OTP at a specific cell index
     * @param index The index of the cell being updated (0-5)
     * @param newChar The new character (single digit or empty string for backspace)
     *
     * Simple logic:
     * - Backspace: Clear ONLY this cell
     * - Type: Set ONLY this cell
     * - No clearing of other cells
     *
     * Examples:
     * - "123456" + backspace at index 3 → "123 56" (only index 3 cleared)
     * - "123 56" + type "4" at index 3 → "123456" (only index 3 set)
     * - "123456" + type "7" at index 2 → "127456" (only index 2 changed)
     */
    fun updateOtpAtIndex(index: Int, newChar: String) {
        val currentOtp = _otpInput.value

        // Convert current OTP to mutable list, ensuring exactly 6 positions
        val otpList = mutableListOf<Char>()

        // Initialize with current OTP or spaces
        for (i in 0 until 6) {
            otpList.add(if (i < currentOtp.length) currentOtp[i] else ' ')
        }

        // Update ONLY the specific index being edited
        if (index >= 0 && index < 6) {
            if (newChar.isEmpty()) {
                // Backspace: clear this cell only
                otpList[index] = ' '
            } else if (newChar.length == 1 && newChar[0].isDigit()) {
                // Typing: set this cell only
                otpList[index] = newChar[0]
            }
        }

        // Convert back to string (keeping spaces for position tracking)
        val updatedOtp = otpList.joinToString("")
        _otpInput.value = updatedOtp
        _otpError.value = false // Clear error when user modifies input
    }

    fun updateOtpInput(newOtp: String) {
        if (newOtp.length <= 6) _otpInput.value = newOtp
        _otpError.value = false // Clear OTP error when user changes input
    }

    fun sendOtp() {
        _errorMessage.value = null
        _isLoading.value = true
        _otpError.value = false
        val fullNumber = getFullPhoneNumber()

        viewModelScope.launch {
            try {
                // Supabase Auth v3 syntax for OTP
                supabase.auth.signInWith(OTP) {
                    phone = fullNumber
                    // Optional: createUser = true (default is usually true, but depends on config)
                }

                _authState.value = 1
                startResendTimer()

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Failed to send OTP. Please check the number and try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startResendTimer() {
        timerJob?.cancel()
        _resendTimer.value = 60
        timerJob = viewModelScope.launch {
            while (_resendTimer.value > 0) {
                delay(1000L)
                _resendTimer.value--
            }
        }
    }

    fun resendOtp() {
        sendOtp()
    }

    fun verifyOtp() {
        _errorMessage.value = null
        _isLoading.value = true
        val fullNumber = getFullPhoneNumber()

        // Remove all spaces to get the actual OTP
        val otp = _otpInput.value.replace(" ", "")

        viewModelScope.launch {
            try {
                // Supabase Auth v3 syntax for verifying Phone OTP
                supabase.auth.verifyPhoneOtp(
                    phone = fullNumber,
                    token = otp,
                    type = OtpType.Phone.SMS
                )

                // SUCCESS: Transition to the success screen state (2)
                _authState.value = 2
                _otpError.value = false

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "OTP verification failed. Please check the code and try again."
                _otpError.value = true // FAILURE: Set the error state here
                _resendTimer.value = 0 // Reset timer immediately on failed verification
                timerJob?.cancel()
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}