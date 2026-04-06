package com.womensafety.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.womensafety.app.data.models.EmergencyContact
import com.womensafety.app.ui.theme.AuraSpacing
import com.womensafety.app.ui.theme.AuraSizes
import com.womensafety.app.ui.theme.AuraElevation

/**
 * Modern Contact Card with swipe-to-delete and expansion
 * 
 * Features:
 * - Swipe left to delete
 * - Tap to expand for details
 * - Verified badge
 * - Accessible
 * - Smooth animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactCard(
    contact: EmergencyContact,
    onDelete: ((EmergencyContact) -> Unit)? = null, // Nullable - when null, delete is disabled
    onEdit: ((EmergencyContact) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    
    val scale by animateFloatAsState(
        targetValue = if (offsetX < -100f) 0.95f else 1f,
        label = "card_scale"
    )
    
    Box(modifier = modifier) {
        // Delete background (shown when swiping)
        if (offsetX < 0) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        MaterialTheme.colorScheme.error,
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(AuraSpacing.comfortable),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AuraSpacing.tight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Delete",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onError
                    )
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete contact",
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.size(AuraSizes.iconMedium)
                    )
                }
            }
        }
        
        // Main card
        Card(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .let { mod ->
                    // Only enable swipe-to-delete if onDelete is provided
                    if (onDelete != null) {
                        mod.pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (offsetX < -200f) {
                                        showDeleteConfirm = true
                                    }
                                    offsetX = 0f
                                },
                                onHorizontalDrag = { _, dragAmount ->
                                    val newOffset = offsetX + dragAmount
                                    offsetX = newOffset.coerceAtMost(0f)
                                }
                            )
                        }
                    } else {
                        mod // No swipe gesture when delete is disabled
                    }
                }
                .semantics {
                    role = Role.Button
                    contentDescription = "Contact card for ${contact.name}, " +
                            "phone number ${contact.phoneNumber}, " +
                            "relationship ${contact.relationship}. " +
                            if (contact.isActive) "Verified. " else "Not verified. " +
                                    "Tap to expand details. Swipe left to delete."
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(
                defaultElevation = AuraElevation.soft,
                pressedElevation = AuraElevation.elevated
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Main content row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AuraSpacing.comfortable),
                    horizontalArrangement = Arrangement.spacedBy(AuraSpacing.default),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    ContactAvatar(
                        name = contact.name,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    // Contact info
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AuraSpacing.minimal)
                        ) {
                            Text(
                                text = contact.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            if (contact.isActive) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        Text(
                            text = contact.phoneNumber,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = contact.relationship,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    
                    // Expand icon
                    Icon(
                        imageVector = if (isExpanded) 
                            Icons.Default.ExpandLess 
                        else 
                            Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Expanded content
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Divider()
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AuraSpacing.comfortable),
                            horizontalArrangement = Arrangement.spacedBy(AuraSpacing.tight)
                        ) {
                            // Call button
                            OutlinedButton(
                                onClick = { /* TODO: Implement call */ },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = AuraSpacing.default)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(AuraSizes.iconSmall)
                                )
                                Spacer(modifier = Modifier.width(AuraSpacing.minimal))
                                Text("Call")
                            }
                            
                            // Message button
                            OutlinedButton(
                                onClick = { /* TODO: Implement message */ },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = AuraSpacing.default)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Message,
                                    contentDescription = null,
                                    modifier = Modifier.size(AuraSizes.iconSmall)
                                )
                                Spacer(modifier = Modifier.width(AuraSpacing.minimal))
                                Text("Message")
                            }
                            
                            // Delete button - only show if onDelete is provided
                            if (onDelete != null) {
                                OutlinedButton(
                                    onClick = { showDeleteConfirm = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    contentPadding = PaddingValues(horizontal = AuraSpacing.default)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(AuraSizes.iconSmall)
                                    )
                                    Spacer(modifier = Modifier.width(AuraSpacing.minimal))
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Delete confirmation dialog - only show if onDelete is provided
    if (showDeleteConfirm && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete Contact?") },
            text = {
                Text("Are you sure you want to remove ${contact.name} from your emergency contacts? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(contact)
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Contact Avatar with first letter
 */
@Composable
fun ContactAvatar(
    name: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
