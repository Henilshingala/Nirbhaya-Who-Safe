package com.womensafety.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.womensafety.app.ui.theme.*
import kotlin.math.sin
import kotlinx.coroutines.delay

/**
 * WELCOME INTRO SCREEN
 * 
 * First screen users see
 * - Zen-like, minimal
 * - Floating particles animation
 * - Soft gradient background
 * - Automatic redirect after loading
 */
@Composable
fun WelcomeIntroScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    
    // Automatic redirection after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        onContinue()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        AuraLavenderLight,
                        AuraBlushLight,
                        AuraIvory
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Floating particles
        repeat(6) { index ->
            val offsetY by infiniteTransition.animateFloat(
                initialValue = -20f + (index * 10f),
                targetValue = 20f + (index * 10f),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 3000 + (index * 500),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "particle_$index"
            )
            
            Box(
                modifier = Modifier
                    .offset(
                        x = (-100 + index * 40).dp,
                        y = offsetY.dp
                    )
                    .size(8.dp + (index * 2).dp)
                    .background(
                        AuraLavender.copy(alpha = 0.2f - (index * 0.02f)),
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
        
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AuraSpacing.space5)
        ) {
            // Logo placeholder (soft icon)
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "AURA",
                modifier = Modifier.size(AuraSizes.iconXXLarge),
                tint = AuraLavender
            )
            
            // Welcome text
            Text(
                text = "Welcome to",
                style = MaterialTheme.typography.titleMedium,
                color = AuraTextSecondary
            )
            
            // Logo
            Image(
                painter = painterResource(id = com.womensafety.app.R.drawable.logo),
                contentDescription = "Nirbhaya Safe",
                modifier = Modifier
                    .size(160.dp) // Adjust size as needed for a logo
            )
            
            Spacer(modifier = Modifier.height(AuraSpacing.space3))
            
            Text(
                text = "nirbhaya is always with you",
                style = AuraTaglineStyle,
                color = AuraTextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(AuraSpacing.space7))
            
            // Premium Loading Animation
            PremiumLoadingIndicator()
            
            Spacer(modifier = Modifier.height(AuraSpacing.space3))
            
            Text(
                text = "Starting your journey...",
                style = MaterialTheme.typography.bodySmall,
                color = AuraTextTertiary,
                modifier = Modifier.alpha(0.7f)
            )
        }
    }
}

@Composable
fun PremiumLoadingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "premium_loading")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(48.dp)
    ) {
        CircularProgressIndicator(
            progress = 0.3f,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = rotation },
            color = AuraLavender,
            strokeWidth = 3.dp,
            trackColor = AuraLavender.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
        
        CircularProgressIndicator(
            progress = 0.3f,
            modifier = Modifier
                .size(30.dp)
                .graphicsLayer { rotationZ = -rotation * 1.5f },
            color = AuraBlushDark,
            strokeWidth = 2.dp,
            trackColor = AuraBlush.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
    }
}

/**
 * PERMISSION STORY SCREEN
 * 
 * Explains why permissions are needed
 * - Modern gradient background (pink to blue)
 * - Card-style permission items
 * - Clean, minimal design
 */
@Composable
fun PermissionStoryScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFC1CC), // Light pink at top
                        Color(0xFFE6E6FA)  // Light lavender at bottom
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF2D2D2D)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Header
            Text(
                text = "To keep you safe we need",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Permission cards
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PermissionCard(
                    icon = Icons.Default.Person,
                    title = "Access to Contacts",
                    description = "To notify your emergency contacts when you need help"
                )
                
                PermissionCard(
                    icon = Icons.Default.LocationOn,
                    title = "Your Location",
                    description = "To share where you are when you need help"
                )
                
                PermissionCard(
                    icon = Icons.Default.Message,
                    title = "Send Messages",
                    description = "To alert your guardians instantly"
                )
                
                PermissionCard(
                    icon = Icons.Default.Phone,
                    title = "Make Calls",
                    description = "To connect you with help quickly"
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Continue button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63) // Vibrant pink
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "I UNDERSTAND, CONTINUE",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PermissionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon with pink circular background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFFE91E63), // Pink background
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D2D2D),
                    modifier = Modifier.weight(1f)
                )
                
                // Arrow with rotation animation
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.ChevronRight,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Expanded description
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(start = 56.dp, end = 8.dp)
                )
            }
        }
    }
}

/**
 * BUILD YOUR CIRCLE SCREEN
 * 
 * First contact addition
 * - Clean modern design
 * - Pink circular icons
 * - Simple card layout
 */
@Composable
fun BuildCircleScreen(
    onAddManually: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                text = "Who Can Help Protect You?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Add People You Trust",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Option cards
            
            ContactOptionCard(
                icon = Icons.Default.Edit,
                title = "Enter Manually",
                onClick = onAddManually
            )
            
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Skip button
            TextButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Skip",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF999999)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pink circular icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(0xFFE91E63),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D2D2D),
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF999999),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

