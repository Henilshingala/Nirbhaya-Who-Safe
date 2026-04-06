package com.womensafety.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.womensafety.app.data.models.EmergencyContact
import com.womensafety.app.ui.theme.*

/**
 * YOUR CIRCLE SCREEN - Redesigned Contacts
 * 
 * Features:
 * - Large circle avatars with soft pastel backgrounds
 * - Relationship labels (Mom, Sister, Friend)
 * - Status indicators (active/pending)
 * - Beautiful empty state
 * - Generous spacing between cards
 * - NO dense lists
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourCircleScreen(
    contacts: List<EmergencyContact> = emptyList(),
    onAddPerson: () -> Unit,
    onContactClick: (EmergencyContact) -> Unit,
    onCreateGroup: () -> Unit = {}, // Navigate to Create Group screen
    onSeeAllGroups: () -> Unit = {}, // NEW: Navigate to Group List screen
    onDeleteContact: (EmergencyContact) -> Unit = {}, // Optional for backward compatibility, but we will use it
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Pink circular phone icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        androidx.compose.ui.graphics.Color(0xFFE91E63),
                        shape = CircleShape
                    )
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone",
                    modifier = Modifier.size(40.dp),
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
            Text(
                text = "Who Can Help Protect You?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFF2D2D2D),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Add People You Trust",
                fontSize = 14.sp,
                color = androidx.compose.ui.graphics.Color(0xFF666666),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            

            
            // Enter manually card
            ContactSelectionCard(
                icon = Icons.Default.Edit,
                title = "Enter Manually",
                onClick = onAddPerson
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Create Group button (only show if contacts exist)
            if (contacts.isNotEmpty()) {
                ContactSelectionCard(
                    icon = Icons.Default.Group,
                    title = "Create Group",
                    onClick = onCreateGroup
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ContactSelectionCard(
                    icon = Icons.Default.List,
                    title = "See All Groups",
                    onClick = onSeeAllGroups
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Show existing contacts if any
            if (contacts.isNotEmpty()) {
                Text(
                    text = "Your Emergency Contacts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color(0xFF2D2D2D),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                contacts.forEach { contact ->
                    GuardianCard(
                        contact = contact,
                        onClick = { onContactClick(contact) },
                        onDelete = { onDeleteContact(contact) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Back button at top left
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = androidx.compose.ui.graphics.Color(0xFF2D2D2D)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactSelectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color(0xFFF5F5F5)
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
                        androidx.compose.ui.graphics.Color(0xFFE91E63),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = androidx.compose.ui.graphics.Color(0xFF2D2D2D),
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = androidx.compose.ui.graphics.Color(0xFF999999),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Guardian Card - Beautiful contact card with pink theme
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuardianCard(
    contact: EmergencyContact,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val initials = contact.name.trim().split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "?" }
    
    val avatarColor = remember(contact.id) {
        listOf(
            androidx.compose.ui.graphics.Color(0xFFE1BEE7), // Light purple
            androidx.compose.ui.graphics.Color(0xFFF8BBD0), // Light pink
            androidx.compose.ui.graphics.Color(0xFFCE93D8), // Medium purple
            androidx.compose.ui.graphics.Color(0xFFF48FB1)  // Medium pink
        ).random()
    }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.White
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(avatarColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color(0xFFE91E63)
                )
            }
            
            // Contact info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = contact.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color(0xFF2D2D2D)
                    )
                    // Status indicator
                    if (contact.isActive) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Active",
                            modifier = Modifier.size(16.dp),
                            tint = androidx.compose.ui.graphics.Color(0xFF4CAF50)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Relationship chip
                    if (contact.relationship.isNotBlank()) {
                        Surface(
                            color = androidx.compose.ui.graphics.Color(0xFFFCE4EC),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = contact.relationship,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = androidx.compose.ui.graphics.Color(0xFFE91E63),
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                )
                            )
                        }
                    }
                    
                    Text(
                        text = contact.phoneNumber,
                        fontSize = 13.sp,
                        color = androidx.compose.ui.graphics.Color(0xFF666666)
                    )
                }
            }
            
            // Delete Button
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Contact",
                        tint = androidx.compose.ui.graphics.Color(0xFFE57373) // Red-ish color
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = androidx.compose.ui.graphics.Color(0xFF999999)
            )
        }
    }
}

/**
 * Empty Circle State
 */
@Composable
private fun EmptyCircleState(
    onAddPerson: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AuraSpacing.screenHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration placeholder
        Icon(
            imageVector = Icons.Default.People,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = AuraLavender.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(AuraSpacing.space5))
        
        Text(
            text = "Emergency contacts",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = AuraText,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(AuraSpacing.space3))
        
        Text(
            text = "Add trusted people who will be notified\nwhen you need help.",
            style = MaterialTheme.typography.bodyLarge,
            color = AuraTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
        )
        
        Spacer(modifier = Modifier.height(AuraSpacing.sectionGap))
        
        // Add manually button
        OutlinedButton(
            onClick = onAddPerson,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = androidx.compose.ui.graphics.Color(0xFFB39DDB)
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFFB39DDB))
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add manually",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        

        
        Spacer(modifier = Modifier.height(AuraSpacing.space5))
        
        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = AuraSky
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AuraSpacing.cardPadding),
                verticalArrangement = Arrangement.spacedBy(AuraSpacing.space3)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AuraSpacing.space2),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = OnAuraSky,
                        modifier = Modifier.size(AuraSizes.iconMedium)
                    )
                    Text(
                        text = "Who should you add?",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OnAuraSky
                    )
                }
                
                InfoItem("Family members you trust")
                InfoItem("Close friends")
                InfoItem("Neighbors nearby")
                InfoItem("People who can help quickly")
            }
        }
    }
}

@Composable
private fun InfoItem(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AuraSpacing.space2),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = AuraSpacing.space5)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = OnAuraSky
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = OnAuraSky
        )
    }
}

/**
 * Guardian Detail Screen
 */
@Composable
fun GuardianDetailScreen(
    contact: EmergencyContact,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    onRemove: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initials = contact.name.trim().split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "?" }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9E6)) // Cream background
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Header (Back button only)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF2D2D2D)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Large avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFFF3E5F5), shape = CircleShape), // Lavender circle
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8E24AA) // Purple text
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = contact.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D2D)
        )
        
        if (contact.relationship.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
             Text(
                text = contact.relationship,
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCall,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF3E5F5), // Light purple
                    contentColor = Color(0xFFBA68C8)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call", fontWeight = FontWeight.Bold)
            }
            
            Button(
                onClick = onMessage,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFCE4EC), // Light pink
                    contentColor = Color(0xFF5D4037)  // Brownish text
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Message", fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat white card on cream bg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailRow("Phone", contact.phoneNumber)
                DetailRow("Status", if (contact.isActive) "Active guardian" else "Pending")
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Remove button
        TextButton(
            onClick = onRemove,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color(0xFFFFB74D) // Orange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Remove contact globally",
                color = Color(0xFFFFB74D), // Orange
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = AuraTextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = AuraText
        )
    }
}
