package com.womensafety.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.womensafety.app.utils.MiuiDeviceDetector

/**
 * MIUI Background Permission Setup Screen
 * 
 * Critical setup flow for Xiaomi/Redmi/POCO devices to ensure the app
 * can run reliably in the background for emergency SOS functionality.
 * 
 * This screen blocks app usage until all MIUI-specific permissions are granted.
 */
@Composable
fun MiuiSetupScreen(
    onSetupComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var autoStartConfirmed by remember { mutableStateOf(false) }
    var batteryOptimizationConfirmed by remember { mutableStateOf(false) }
    var backgroundActivityConfirmed by remember { mutableStateOf(false) }
    var notificationsConfirmed by remember { mutableStateOf(false) }
    
    // Check actual verifiable permissions
    LaunchedEffect(Unit) {
        batteryOptimizationConfirmed = MiuiDeviceDetector.isBatteryOptimizationDisabled(context)
        notificationsConfirmed = MiuiDeviceDetector.areNotificationsEnabled(context)
    }
    
    val allPermissionsGranted = autoStartConfirmed && 
                                batteryOptimizationConfirmed && 
                                backgroundActivityConfirmed && 
                                notificationsConfirmed
    
    val miuiVersion = MiuiDeviceDetector.getMiuiVersion() ?: "Unknown"
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Pink circular phone icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color(0xFFE91E63),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone",
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
            Text(
                text = "Critical Setup Required",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Xiaomi/Redmi/POCO Device Detected",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "MIUI Version: $miuiVersion",
                fontSize = 12.sp,
                color = Color(0xFF999999),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Why Is This Needed? Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                ),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF2D2D2D),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Column {
                        Text(
                            text = "Why Is This Needed?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "MIUI aggressively restricts background apps for battery saving. For your safety, this app MUST run in the background to detect emergency IoT calls, trigger SOS, play siren, and send location even when your screen is locked.",
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Required Permissions Title
            Text(
                text = "Required Permissions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Permission Steps
            MiuiPermissionCard(
                stepNumber = 1,
                title = "Enable Auto-Start",
                description = "Allows the app to start automatically and detect IoT emergency calls in the background, even after device restart.",
                isCompleted = autoStartConfirmed,
                onOpenSettings = {
                    MiuiDeviceDetector.openAutoStartSettings(context)
                },
                onConfirm = { autoStartConfirmed = true }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MiuiPermissionCard(
                stepNumber = 2,
                title = "Disable Battery Optimization",
                description = "Set to 'No restrictions' to prevent MIUI from killing the app when screen is off or during emergencies.",
                isCompleted = batteryOptimizationConfirmed,
                onOpenSettings = {
                    MiuiDeviceDetector.openBatterySaverSettings(context)
                },
                onConfirm = { 
                    batteryOptimizationConfirmed = MiuiDeviceDetector.isBatteryOptimizationDisabled(context)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MiuiPermissionCard(
                stepNumber = 3,
                title = "Allow Background Activity",
                description = "Enables the app to run and send SOS alerts, location updates, and emergency notifications even when not actively used.",
                isCompleted = backgroundActivityConfirmed,
                onOpenSettings = {
                    MiuiDeviceDetector.openBackgroundActivitySettings(context)
                },
                onConfirm = { backgroundActivityConfirmed = true }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MiuiPermissionCard(
                stepNumber = 4,
                title = "Enable Notifications",
                description = "Critical alerts and emergency status updates must be visible on lock screen during emergencies for your safety.",
                isCompleted = notificationsConfirmed,
                onOpenSettings = {
                    MiuiDeviceDetector.openNotificationSettings(context)
                },
                onConfirm = { 
                    notificationsConfirmed = MiuiDeviceDetector.areNotificationsEnabled(context)
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Done Button (only shown when all permissions granted)
            if (allPermissionsGranted) {
                Button(
                    onClick = onSetupComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63)
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = "DONE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MiuiPermissionCard(
    stepNumber: Int,
    title: String,
    description: String,
    isCompleted: Boolean,
    onOpenSettings: () -> Unit,
    onConfirm: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Pink circular badge with number
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            Color(0xFFE91E63),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        lineHeight = 18.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF666666)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.dp
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Open settings",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Button(
                    onClick = onConfirm,
                    enabled = !isCompleted,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63),
                        disabledContainerColor = Color(0xFFE0E0E0),
                        disabledContentColor = Color(0xFF999999)
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (isCompleted) "Done" else "I've Enabled it",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}
