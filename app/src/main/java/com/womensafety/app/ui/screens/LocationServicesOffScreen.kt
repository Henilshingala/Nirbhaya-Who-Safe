package com.womensafety.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * Screen shown when Location Services (GPS/Network) are OFF. This is NOT about permissions -
 * permission is already granted.
 */
@Composable
fun LocationServicesOffScreen(onOpenLocationSettings: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFCE4EC), // Light pink
                        Color(0xFFFFF9E6)  // Light cream
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.systemBars), // Edge-to-edge safe area
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(24.dp)
        ) {
            // Icon with circle background
            Box(
                 modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFFFFD1DC).copy(alpha = 0.5f), shape = RoundedCornerShape(32.dp)),
                 contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOff,
                    contentDescription = "Location Services Off",
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFFE91E63)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Location Services Off",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = com.womensafety.app.LocationPermissionEnforcer.getLocationServicesOffMessage(),
                fontSize = 16.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onOpenLocationSettings,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "OPEN LOCATION SETTINGS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Once you enable Location/GPS, return to this app.\nThe app will continue automatically.",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
