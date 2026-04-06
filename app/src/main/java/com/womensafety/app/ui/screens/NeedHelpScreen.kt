package com.womensafety.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * NEED HELP SCREEN - Pink/Blue Theme
 * Beautiful pulsing circular button with ripple animations
 */
@Composable
fun NeedHelpScreen(
    contactCount: Int = 0,
    contactNames: List<String> = emptyList(),
    isSOSActive: Boolean = false,
    isInCooldown: Boolean = false,
    cooldownSeconds: Int = 0,
    onHelpSent: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    
    var isHolding by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var isSending by remember { mutableStateOf(isSOSActive) }
    
    // Update isSending when SOS state changes
    LaunchedEffect(isSOSActive) {
        isSending = isSOSActive
    }
    
    val holdDuration = 3000L // 3 seconds hold
    
    // Pulsing animation for the circles
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )
    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )
    val pulseScale3 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse3"
    )
    
    // Prevent interaction if SOS is active or in cooldown
    val canTrigger = !isSOSActive && !isInCooldown
    
    LaunchedEffect(isHolding, canTrigger) {
        if (isHolding && !isSending && canTrigger) {
            val startTime = System.currentTimeMillis()
            while (isHolding && progress < 1f && canTrigger) {
                delay(16L) // ~60fps
                val elapsed = System.currentTimeMillis() - startTime
                progress = (elapsed.toFloat() / holdDuration).coerceAtMost(1f)
                
                // Haptic feedback at intervals
                if (progress in 0.3f..0.31f || progress in 0.6f..0.61f) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                
                if (progress >= 1f) {
                    isSending = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onHelpSent()
                }
            }
        } else if (!isHolding) {
            progress = 0f
        }
    }
    
    // Scale animation when holding
    val scale by animateFloatAsState(
        targetValue = if (isHolding) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "hold_scale"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFCE4EC), // Light pink
                        Color(0xFFE3F2FD)  // Light blue
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF2D2D2D)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Need Help ?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (contactCount > 0) {
                Text(
                    text = "Emergency Contacts Will Be Notified Immediately",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Add guardians to activate help",
                    fontSize = 14.sp,
                    color = Color(0xFFE91E63),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Pulsing circles and main button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(300.dp)
            ) {
                // Outer pulsing circle 3
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .scale(pulseScale3)
                        .alpha(0.15f)
                        .background(
                            Color(0xFF42A5F5),
                            shape = CircleShape
                        )
                )
                
                // Middle pulsing circle 2
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .scale(pulseScale2)
                        .alpha(0.25f)
                        .background(
                            Color(0xFF42A5F5),
                            shape = CircleShape
                        )
                )
                
                // Inner pulsing circle 1
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(pulseScale1)
                        .alpha(0.35f)
                        .background(
                            Color(0xFF42A5F5),
                            shape = CircleShape
                        )
                )
                
                // Main button
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(scale)
                        .background(
                            Brush.verticalGradient(
                                colors = when {
                                !canTrigger || contactCount == 0 -> listOf(
                                    Color(0xFFBDBDBD),
                                    Color(0xFF9E9E9E)
                                )
                                else -> listOf(
                                    Color(0xFF42A5F5),
                                    Color(0xFF1E88E5)
                                )
                            }
                            ),
                            shape = CircleShape
                        )
                        .pointerInput(canTrigger) {
                            detectTapGestures(
                                onPress = {
                                    if (contactCount > 0 && !isSending && canTrigger) {
                                        isHolding = true
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        tryAwaitRelease()
                                        isHolding = false
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = Color.White
                        )
                        
                        Text(
                            text = when {
                                isSending -> "Alerting..."
                                isInCooldown -> "Wait ${cooldownSeconds}s"
                                isHolding -> "Hold..."
                                else -> "HELP"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        if (!isHolding && !isSending && !isInCooldown) {
                            Text(
                                text = if (contactCount > 0) "Hold to Start" else "Add guardians",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        } else if (isInCooldown) {
                            Text(
                                text = "Please wait before sending again",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
                
                // Progress ring
                if (progress > 0f) {
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.size(180.dp),
                        color = Color.White,
                        strokeWidth = 6.dp,
                        trackColor = Color.Transparent
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Who will be notified
            if (contactCount > 0 && contactNames.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Will Be Notified:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = contactNames.take(3).joinToString(", ") +
                                    if (contactNames.size > 3) " and ${contactNames.size - 3} more" else "",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Cancel button
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE91E63)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * HELP SENT CONFIRMATION POPUP
 * Compact dialog showing emergency alert confirmation
 */
@Composable
fun HelpSentScreen(
    contactNames: List<String> = emptyList(),
    onReturnToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pink circular checkmark icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFE91E63), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Emergency Contact notified",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = contactNames.firstOrNull()?.split(" ")?.first() ?: "Contact" + " is on the way",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Timeline items - compact version
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CompactTimelineItem(
                        icon = Icons.Default.Check,
                        text = "Alert Sent",
                        timestamp = "Just now"
                    )
                    CompactTimelineItem(
                        icon = Icons.Default.LocationOn,
                        text = "Location Shared",
                        timestamp = "Just now"
                    )
                    CompactTimelineItem(
                        icon = Icons.Default.Message,
                        text = "Messages delivered",
                        timestamp = "Just now"
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // YES button
                Button(
                    onClick = onReturnToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "YES",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactTimelineItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    timestamp: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFFE91E63)
            )
            Text(
                text = text,
                fontSize = 14.sp,
                color = Color(0xFF2D2D2D)
            )
        }
        Text(
            text = timestamp,
            fontSize = 12.sp,
            color = Color(0xFF999999)
        )
    }
}
