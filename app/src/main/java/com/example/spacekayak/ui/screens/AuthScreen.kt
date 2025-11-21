package com.example.spacekayak.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFF0C2442))
                    .padding(top = 24.dp)
                    .padding(
                        bottom = WindowInsets.ime
                            .asPaddingValues()
                            .calculateBottomPadding() * 0.5f
                    )
            ) {
                Crossfade(targetState = authState) { state ->
                    when (state) {
                        0 -> PhoneInputScreen(viewModel)
                        1 -> OtpInputScreen(viewModel)
                    }
                }
            }
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
            .padding(bottom = 0.dp)
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
                            val newOtp = otpInput.padEnd(index, ' ').replaceRange(index, index + 1, newValue)
                            viewModel.updateOtpInput(newOtp.replace(" ", ""))
                        } else if (newValue.isEmpty() && index > 0) {
                            val newOtp = otpInput.replaceRange(index - 1, index, "")
                            viewModel.updateOtpInput(newOtp)
                            focusRequesters.getOrNull(index - 1)?.requestFocus()
                        }
                    },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    focusRequester = focusRequesters[index]
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (timer > 0) {
                Text(
                    text = "Resend code in $timer s",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            } else {
                TextButton(onClick = viewModel::resendOtp) {
                    Text(
                        text = "Resend Code",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

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
fun OtpCell(value: String, onValueChange: (String) -> Unit, modifier: Modifier, focusRequester: FocusRequester) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
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
