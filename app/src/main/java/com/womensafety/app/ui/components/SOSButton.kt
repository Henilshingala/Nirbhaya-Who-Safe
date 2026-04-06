package com.womensafety.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.womensafety.app.ui.theme.AuraSpacing
import com.womensafety.app.ui.theme.AuraSizes
import com.womensafety.app.ui.theme.AuraElevation
import com.womensafety.app.ui.theme.SOSButtonTextStyle
import com.womensafety.app.ui.theme.alertColor

/**
 * Redesigned SOS Button - Calm, Trust-Building, Professional
 * 
 * Features:
 * - Soft gradient (lavender to pink, NO red!)
 * - Gentle pulsing (like breathing, not heartbeat)
 * - Soft ripple waves
 * - Shield icon (protection, not warning)
 * - Haptic feedback
 * - Fully accessible
 */
@Composable
fun SOSButton(
    onSOSTriggered: () -> Unit,
    isEnabled: Boolean,
    isActive: Boolean = false,
    isInCooldown: Boolean = false,
    cooldownSeconds: Int = 0,
    contactCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse")
    
    // Gentle breathing animation (slower, calmer than heartbeat)
    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,  // More subtle than before
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,  // Slower, calmer
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_breathe"
    )
    
    // Soft ripple waves
    val rippleCount = 3
    val rippleDuration = 3000  // Slower, more gentle
    
    Box(
        modifier = modifier.size(AuraSizes.auraRingLarge * 1.6f),
        contentAlignment = Alignment.Center
    ) {
        // Soft ripple waves (lavender, not red!)
        if (isEnabled) {
            repeat(rippleCount) { index ->
                val delay = (rippleDuration / rippleCount) * index
                
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.6f,
                    targetValue = 1.4f,
                    animationSpec = infiniteRepeatable(
                        initialStartOffset = StartOffset(delay),
                        animation = tween(
                            durationMillis = rippleDuration,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "ripple_$index"
                )
                
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.4f,  // Softer, less intense
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        initialStartOffset = StartOffset(delay),
                        animation = tween(
                            durationMillis = rippleDuration,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "ripple_alpha_$index"
                )
                
                Box(
                    modifier = Modifier
                        .size(AuraSizes.auraRingLarge)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            // Soft lavender ripple
                            MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                        )
                )
            }
        }
        
        // Main SOS Button with beautiful gradient
        FilledTonalButton(
            onClick = {
                if (isEnabled && !isActive && !isInCooldown) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSOSTriggered()
                }
            },
            enabled = isEnabled && !isActive && !isInCooldown,
            modifier = Modifier
                .size(AuraSizes.auraRingLarge)
                .scale(if (isEnabled) buttonScale else 1f)
                .background(
                    brush = if (isEnabled) {
                        // Beautiful gradient: lavender to pink
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    },
                    shape = CircleShape
                )
                .semantics {
                    role = Role.Button
                    contentDescription = if (isEnabled) {
                        "Emergency Help Button. Tap to send alert to $contactCount trusted contact${if (contactCount != 1) "s" else ""}. Your emergency contacts will be notified immediately with your location."
                    } else {
                        "Emergency Help Button. Currently disabled. Please add emergency contacts first to enable this safety feature."
                    }
                },
            shape = CircleShape,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Color.Transparent,  // Transparent for gradient
                contentColor = Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,  // Less dramatic shadow
                pressedElevation = 4.dp,
                disabledElevation = 2.dp
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Shield icon (protection, safety)
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(AuraSizes.iconXLarge),
                    tint = Color.White
                )
                
                Spacer(modifier = Modifier.height(AuraSpacing.tight))
                
                // "HELP" text (more empowering than "SOS")
                Text(
                    text = if (isActive) "SENDING..." else if (isInCooldown) "WAIT" else "HELP",
                    style = SOSButtonTextStyle,
                    color = Color.White
                )
                
                if (!isEnabled) {
                    Spacer(modifier = Modifier.height(AuraSpacing.minimal))
                    Text(
                        text = "Add Contacts",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else if (isInCooldown && cooldownSeconds > 0) {
                    Spacer(modifier = Modifier.height(AuraSpacing.minimal))
                    Text(
                        text = "${cooldownSeconds}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

/**
 * SOS Status Card - Soft, reassuring design
 */
@Composable
fun SOSStatusCard(
    contactCount: Int,
    isLocationEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val hasContacts = contactCount > 0
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (hasContacts)
                MaterialTheme.colorScheme.primaryContainer // Soft lavender
            else
                MaterialTheme.colorScheme.errorContainer // Soft coral
        ),
        shape = MaterialTheme.shapes.extraLarge,  // More rounded
        elevation = CardDefaults.cardElevation(
            defaultElevation = AuraElevation.soft
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AuraSpacing.comfortable),
            horizontalArrangement = Arrangement.spacedBy(AuraSpacing.default),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (hasContacts) 
                    Icons.Default.CheckCircle 
                else 
                    Icons.Default.Shield,
                contentDescription = null,
                modifier = Modifier.size(AuraSizes.iconLarge),
                tint = if (hasContacts)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.alertColor
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (hasContacts)
                        "$contactCount Trusted Contact${if (contactCount != 1) "s" else ""} Ready"
                    else
                        "No Trusted Contacts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (hasContacts)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer
                )
                
                Spacer(modifier = Modifier.height(AuraSpacing.minimal))
                
                Text(
                    text = if (hasContacts) {
                        if (isLocationEnabled)
                            "Protected • Location sharing active"
                        else
                            "Protected • Enable location for better safety"
                    } else {
                        "Add trusted contacts to activate protection"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (hasContacts)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    else
                        MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
