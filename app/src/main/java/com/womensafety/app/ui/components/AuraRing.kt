package com.womensafety.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.womensafety.app.ui.theme.*

/**
 * AURA RING - The Heart of the App
 * 
 * A pulsing gradient ring that represents protection status
 * Completely unique visual - nothing like the old design
 * 
 * States:
 * - Protected: Large, lavender→blush gradient, slow breathing
 * - Partial: Medium, blush→peach gradient, moderate breathing
 * - Setup: Small, grey, static (no animation)
 */
@Composable
fun AuraRing(
    protectionLevel: ProtectionLevel,
    contactCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura_ring")
    
    // Breathing animation - size changes like breathing
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (protectionLevel != ProtectionLevel.Setup) 1.03f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = AuraMotion.breathe,
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe_scale"
    )
    
    // Opacity pulsing - like soft glow
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = if (protectionLevel != ProtectionLevel.Setup) 1f else 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    // Gradient rotation - very slow rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (protectionLevel != ProtectionLevel.Setup) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = AuraMotion.gradientRotate,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient_rotate"
    )
    
    val ringSize = when (protectionLevel) {
        ProtectionLevel.Protected -> AuraSizes.auraRingLarge
        ProtectionLevel.Partial -> AuraSizes.auraRingMedium
        ProtectionLevel.Setup -> AuraSizes.auraRingSmall
        ProtectionLevel.Listening -> AuraSizes.auraRingLarge
    }
    
    val gradient = when (protectionLevel) {
        ProtectionLevel.Protected -> AuraGradients.Primary  // Lavender→Blush
        ProtectionLevel.Partial -> Pair(AuraBlush, AuraPeach)  // Blush→Peach
        ProtectionLevel.Setup -> Pair(AuraMist, AuraMist)  // Grey
        ProtectionLevel.Listening -> AuraGradients.Primary // Lavender→Blush (Same as Protected for Guardian)
    }
    
    Box(
        modifier = modifier.sizeIn(minWidth = ringSize * 1.3f, minHeight = ringSize * 1.3f),  // Minimum space for ring
        contentAlignment = Alignment.Center
    ) {
        // The Ring itself
        Canvas(modifier = Modifier.size(ringSize * scale)) {
            val diameter = size.minDimension
            
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        gradient.first.copy(alpha = alpha),
                        gradient.second.copy(alpha = alpha),
                        gradient.first.copy(alpha = alpha)
                    ),
                    // No rotation property in sweepGradient, will use transform instead
                ),
                radius = diameter / 2,
                style = Stroke(
                    width = AuraSizes.auraRingStroke.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
        
        // Content inside ring
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = when (protectionLevel) {
                    ProtectionLevel.Protected -> "Protected"
                    ProtectionLevel.Partial -> "Partial"
                    ProtectionLevel.Setup -> "Setup"
                    ProtectionLevel.Listening -> "Listening"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = when (protectionLevel) {
                    ProtectionLevel.Protected -> AuraLavenderDark
                    ProtectionLevel.Partial -> AuraBlushDark
                    ProtectionLevel.Setup -> AuraTextSecondary
                    ProtectionLevel.Listening -> AuraLavenderDark
                }
            )
            
            if (protectionLevel != ProtectionLevel.Setup && protectionLevel != ProtectionLevel.Listening) {
                Spacer(modifier = Modifier.height(AuraSpacing.space1))
                Text(
                    text = "$contactCount ${if (contactCount == 1) "guardian" else "guardians"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraTextSecondary
                )
            }
        }
    }
}

/**
 * Protection Level Enum
 */
enum class ProtectionLevel {
    Protected,  // Full protection - all contacts active
    Partial,    // Some contacts
    Setup,      // Not configured yet
    Listening   // Guardian mode active
}
