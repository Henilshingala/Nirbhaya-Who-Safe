package com.womensafety.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.womensafety.app.XiaomiAutostartHelper

/**
 * Xiaomi-specific setup screen explaining Autostart requirement
 * 
 * Xiaomi devices have aggressive battery optimization that prevents
 * apps from receiving SMS when closed. This screen:
 * - Explains the issue clearly
 * - Opens Autostart settings
 * - Guides battery optimization
 * - Only shown once
 */
@Composable
fun XiaomiSetupScreen(
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFCE4EC), // Light pink
                        Color(0xFFFFF9E6)  // Light cream
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Warning icon
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = Color(0xFFE91E63)
        )
        
        // Title
        Text(
            text = "⚠️ Xiaomi Setup Required",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D2D),
            textAlign = TextAlign.Center
        )
        
        // Main explanation
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Critical for Emergency Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE91E63)
                )
                
                Text(
                    text = "Xiaomi devices block apps from receiving SMS when closed to save battery. This prevents emergency alerts from working!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    lineHeight = 22.sp
                )
                
                Text(
                    text = "You MUST enable Autostart and disable battery restrictions for this app to work reliably.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE91E63),
                    lineHeight = 22.sp
                )
            }
        }
        
        // Step 1: Autostart
        SetupStepCard(
            stepNumber = "1",
            title = "Enable Autostart",
            description = "Allow this app to start automatically in the background",
            icon = Icons.Default.PowerSettingsNew,
            buttonText = "Open Autostart Settings",
            onButtonClick = {
                XiaomiAutostartHelper.openAutostartSettings(context)
            }
        )
        
        // Step 2: Battery Optimization
        SetupStepCard(
            stepNumber = "2",
            title = "Disable Battery Restrictions",
            description = "Set battery to 'No restrictions' or 'Unrestricted'",
            icon = Icons.Default.BatterySaver,
            buttonText = "Open Battery Settings",
            onButtonClick = {
                XiaomiAutostartHelper.openBatterySettings(context)
            }
        )
        
        // Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📋 What to do:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE91E63)
                )
                
                Text(
                    text = "1. Tap 'Open Autostart Settings'\n" +
                          "2. Find 'Women Safety' in the list\n" +
                          "3. Turn ON the toggle ✅\n" +
                          "4. Return to this screen\n" +
                          "5. Tap 'Open Battery Settings'\n" +
                          "6. Select 'No restrictions' ✅\n" +
                          "7. Tap 'I'm Done' below",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    lineHeight = 22.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Complete button
        Button(
            onClick = {
                XiaomiAutostartHelper.markSetupComplete(context)
                onComplete()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE91E63),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "✅ I'm Done - Continue",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Warning message
        Text(
            text = "⚠️ Without these settings, emergency alerts will NOT work when the app is closed!",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFE91E63),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        }
    }
}

@Composable
private fun SetupStepCard(
    stepNumber: String,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Step number badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFFE91E63),
                        RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFFE91E63)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}