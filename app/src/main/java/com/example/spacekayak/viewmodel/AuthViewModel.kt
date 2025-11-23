// Manages authentication state and logic, including random OTP generation and notification.

package com.example.spacekayak.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private var timerJob: Job? = null

    private val _authState = MutableStateFlow(0)
    val authState: StateFlow<Int> = _authState

    private val _showPhoneVerificationModal = MutableStateFlow(false)
    val showPhoneVerificationModal: StateFlow<Boolean> = _showPhoneVerificationModal

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _otpInput = MutableStateFlow("")
    val otpInput: StateFlow<String> = _otpInput

    private val _resendTimer = MutableStateFlow(0)
    val resendTimer: StateFlow<Int> = _resendTimer

    private val _showSmsNotification = MutableStateFlow(false)
    val showSmsNotification: StateFlow<Boolean> = _showSmsNotification

    private val _generatedOtp = MutableStateFlow("123456")
    val generatedOtp: StateFlow<String> = _generatedOtp

    private fun generateRandomOtp(): String {
        return (100000..999999).random().toString()
    }

    fun showPhoneVerificationModal() {
        _showPhoneVerificationModal.value = true
    }

    fun hidePhoneVerificationModal() {
        _showPhoneVerificationModal.value = false
        _authState.value = 0
    }

    fun updatePhoneNumber(newNumber: String) {
        if (newNumber.length <= 10) _phoneNumber.value = newNumber
    }

    fun updateOtpInput(newOtp: String) {
        if (newOtp.length <= 6) _otpInput.value = newOtp
    }

    fun sendOtp() {
        _authState.value = 1
        viewModelScope.launch {
            delay(1000L)

            _generatedOtp.value = generateRandomOtp()
            startResendTimer()
            showIncomingSmsNotification()
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

    fun showIncomingSmsNotification() {
        viewModelScope.launch {
            _showSmsNotification.value = true
            delay(10000L)
            _showSmsNotification.value = false
        }
    }

    fun resendOtp() {
        // Supabase resend OTP function call
        _generatedOtp.value = generateRandomOtp()
        startResendTimer()
        showIncomingSmsNotification()
    }

    fun verifyOtp() {

        // Supabase OTP verification function call
        hidePhoneVerificationModal()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}