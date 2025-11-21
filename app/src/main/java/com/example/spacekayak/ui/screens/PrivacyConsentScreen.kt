package com.example.spacekayak.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.spacekayak.R
import com.example.spacekayak.ui.theme.DarkText
import com.example.spacekayak.ui.theme.GrayText
import com.example.spacekayak.ui.theme.PrimaryBlue

@Composable
fun PrivacyConsentScreen(
    onUnderstand: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.privacy_consent_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(300.dp))

            Text(
                text = "Private by Design.\nYou're in Control.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = DarkText
            )


            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Shield protects you from scams, risky links, and harmful apps, all while keeping your data private and secure.",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = GrayText,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            PrivacyPoint(
                icon = Icons.Filled.Lock,
                text = "All checks happen on your phone; contacts and chats stay private.",
                textColor = GrayText,
                iconTint = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(20.dp))

            PrivacyPoint(
                icon = Icons.Filled.CheckCircle,
                text = "We request only permissions needed to keep you safe.",
                textColor = GrayText,
                iconTint = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(20.dp))

            PrivacyPoint(
                icon = Icons.Filled.History,
                text = "Control your data access anytime. We never sell your information.",
                textColor = GrayText,
                iconTint = PrimaryBlue
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onUnderstand,
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "I understand",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PrivacyPoint(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, textColor: Color, iconTint: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp).padding(end = 8.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
    }
}