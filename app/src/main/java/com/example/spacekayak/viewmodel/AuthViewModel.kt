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
        // Supabase function to send OTP will be called here
        _authState.value = 1
        startResendTimer()
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
        // Supabase resend OTP function call
        startResendTimer()
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