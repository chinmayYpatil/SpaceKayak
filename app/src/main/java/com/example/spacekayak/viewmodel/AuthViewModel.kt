package com.example.spacekayak.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacekayak.data.supabase
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

    private val _authState = MutableStateFlow(0)
    val authState: StateFlow<Int> = _authState

    private val _showPhoneVerificationModal = MutableStateFlow(false)
    val showPhoneVerificationModal: StateFlow<Boolean> = _showPhoneVerificationModal

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _otpInput = MutableStateFlow("")
    val otpInput: StateFlow<String> = _otpInput

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
        _otpError.value = false
    }

    fun completeVerificationFlow() {
        hidePhoneVerificationModal()
    }

    fun updatePhoneNumber(newNumber: String) {
        if (newNumber.length <= 10) _phoneNumber.value = newNumber
    }

    fun updateOtpAtIndex(index: Int, newChar: String) {
        val currentOtp = _otpInput.value

        val otpList = mutableListOf<Char>()

        for (i in 0 until 6) {
            otpList.add(if (i < currentOtp.length) currentOtp[i] else ' ')
        }

        if (index >= 0 && index < 6) {
            if (newChar.isEmpty()) {
                otpList[index] = ' '
            } else if (newChar.length == 1 && newChar[0].isDigit()) {
                otpList[index] = newChar[0]
            }
        }

        val updatedOtp = otpList.joinToString("")
        _otpInput.value = updatedOtp
        _otpError.value = false
    }

    fun updateOtpInput(newOtp: String) {
        if (newOtp.length <= 6) _otpInput.value = newOtp
        _otpError.value = false
    }

    fun sendOtp() {
        _errorMessage.value = null
        _isLoading.value = true
        _otpError.value = false
        val fullNumber = getFullPhoneNumber()

        viewModelScope.launch {
            try {
                supabase.auth.signInWith(OTP) {
                    phone = fullNumber
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

        val otp = _otpInput.value.replace(" ", "")

        viewModelScope.launch {
            try {
                supabase.auth.verifyPhoneOtp(
                    phone = fullNumber,
                    token = otp,
                    type = OtpType.Phone.SMS
                )

                _authState.value = 2
                _otpError.value = false

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "OTP verification failed. Please check the code and try again."
                _otpError.value = true
                _resendTimer.value = 0
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