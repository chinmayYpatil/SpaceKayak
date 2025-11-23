// Contains the Composable functions for the phone verification modal, with the OTP crash fix and the SMS notification repositioned to the top center.

package com.example.spacekayak.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.example.spacekayak.ui.theme.DarkText
import com.example.spacekayak.ui.theme.PrimaryBlue
import com.example.spacekayak.viewmodel.AuthViewModel

@Composable
fun AuthFlowModal(viewModel: AuthViewModel) {
    val isVisible by viewModel.showPhoneVerificationModal.collectAsState()
    val authState by viewModel.authState.collectAsState()
    // REMOVED: showSmsNotification and generatedOtp as they no longer exist in AuthViewModel

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
        ) {
            // Modal content is explicitly aligned to the bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Explicitly align modal to the bottom
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFF0C2442))
                    .padding(top = 24.dp)
                    .padding(
                        bottom = WindowInsets.ime
                            .asPaddingValues()
                            .calculateBottomPadding()*0.5f
                    )
            ) {
                Crossfade(targetState = authState) { state ->
                    when (state) {
                        0 -> PhoneInputScreen(viewModel)
                        1 -> OtpInputScreen(viewModel)
                        2 -> VerificationSuccessScreen(viewModel)
                    }
                }
            }

            // REMOVED: IncomingSmsNotification call as it relies on removed states
        }
    }
}

@Composable
fun PhoneInputScreen(viewModel: AuthViewModel) {
    val phoneNumber by viewModel.phoneNumber.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp)
    ) {
        HeaderSection(
            title = "Verify your phone number",
            subtitle = "We're verifying your number securely. An OTP may be sent if needed.",
            onClose = viewModel::hidePhoneVerificationModal
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .width(80.dp)
                    .height(56.dp)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "+91",
                    color = DarkText,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = viewModel::updatePhoneNumber,
                placeholder = { Text("Enter Phone Number", color = Color.White.copy(alpha = 0.5f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White.copy(alpha = 0.2f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = viewModel::sendOtp,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(30.dp),
            enabled = phoneNumber.length == 10
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        PrivacyText()

    }
}

@Composable
fun OtpInputScreen(viewModel: AuthViewModel) {
    val otpInput by viewModel.otpInput.collectAsState()
    val timer by viewModel.resendTimer.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val otpError by viewModel.otpError.collectAsState()

    val length = 6
    val focusRequesters = remember { List(length) { FocusRequester() } }

    LaunchedEffect(otpInput) {
        if (otpInput.length < length) {
            focusRequesters.getOrNull(otpInput.length)?.requestFocus()
        } else {
            focusRequesters.last().freeFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 150.dp)
    ) {
        HeaderSection(
            title = "Enter OTP",
            subtitle = "A 6 digit code was sent to +91 $phoneNumber. Please enter it below.",
            onClose = viewModel::hidePhoneVerificationModal
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(length) { index ->
                OtpCell(
                    value = otpInput.getOrNull(index)?.toString() ?: "",
                    onValueChange = { newValue ->
                        if (newValue.length == 1) {
                            // Logic for entering a digit
                            val newOtp = otpInput.padEnd(index + 1, ' ').replaceRange(index, index + 1, newValue)
                            viewModel.updateOtpInput(newOtp.replace(" ", ""))
                        } else if (newValue.isEmpty() && index > 0) {
                            // FIX: Corrected Backspace Logic: Only process backspace if it's the last character
                            if (index == otpInput.length) {
                                val newOtp = otpInput.dropLast(1)
                                viewModel.updateOtpInput(newOtp)
                                focusRequesters.getOrNull(index - 1)?.requestFocus()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    focusRequester = focusRequesters[index],
                    isError = otpError
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FIX: Combined error message and resend logic in one row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Error Message (Left)
            if (otpError) {
                Text(
                    text = "Invalid OTP. Please try again.",
                    color = Color(0xFFE53935), // Red color
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            } else {
                // Spacer to ensure resend logic stays on the right when no error
                Spacer(modifier = Modifier.width(1.dp))
            }

            // Resend Logic (Right)
            if (timer > 0) {
                Text(
                    text = "Resend code in $timer s",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            } else {
                TextButton(
                    onClick = viewModel::resendOtp,
                    // Remove default padding for a tighter fit in the row
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Resend Code",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
        // END FIX

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = viewModel::verifyOtp,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(30.dp),
            enabled = otpInput.length == 6
        ) {
            Text(
                text = "Verify",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Removed the original separate resend logic
        Spacer(modifier = Modifier.height(16.dp)) // Maintain some bottom padding

    }
}

@Composable
fun VerificationSuccessScreen(viewModel: AuthViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp) // Fixed height to position content clearly in the modal
            .padding(horizontal = 24.dp)
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Icon (Approximation of the circular blue checkmark from OTP.png)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFF00BFFF), PrimaryBlue))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Verified",
                    tint = Color.White,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Your phone is verified securely.",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "OTP Verified safely with an OTP. Stay alert on fake OTP scams.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Button to dismiss the modal and proceed to the app
        Button(
            onClick = viewModel::completeVerificationFlow,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                text = "Continue to App",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// REMOVED: IncomingSmsNotification composable definition as it is no longer used.

@Composable
fun HeaderSection(title: String, subtitle: String, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = Color.White
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = subtitle,
        fontSize = 14.sp,
        color = Color.White.copy(alpha = 0.8f),
        modifier = Modifier.padding(horizontal = 24.dp)
    )
}

@Composable
fun OtpCell(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    focusRequester: FocusRequester,
    isError: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        colors = OutlinedTextFieldDefaults.colors(
            // FIX: Set border to red if error is true
            focusedBorderColor = if (isError) Color(0xFFE53935) else PrimaryBlue,
            unfocusedBorderColor = if (isError) Color(0xFFE53935) else Color.White.copy(alpha = 0.2f),
            focusedContainerColor = Color.White.copy(alpha = 0.2f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .height(56.dp)
            .focusRequester(focusRequester)
    )
}

@Composable
fun PrivacyText() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "By continuing, I confirm I am at least 18 years old and agree to Shield's Terms and Privacy Policy, and receiving SMS alerts.",
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}