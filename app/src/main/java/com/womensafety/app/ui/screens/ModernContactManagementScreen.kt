package com.womensafety.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.womensafety.app.data.models.EmergencyContact
import com.womensafety.app.ui.components.ContactCard
import com.womensafety.app.ui.theme.AuraSpacing
import com.womensafety.app.ui.theme.AuraSizes
import com.womensafety.app.ui.theme.AuraElevation

/**
 * Modern Contact Management Screen
 * 
 * Features:
 * - List of emergency contacts using ContactCard
 * - FAB for adding new contacts
 * - Empty state with illustration
 * - Search functionality (future)
 * - Pull to refresh (future)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ModernContactManagementScreen(
    contacts: List<EmergencyContact>,
    onAddContact: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Emergency Contacts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Future: Add search icon
                    // IconButton(onClick = { /* Search */ }) {
                    //     Icon(Icons.Default.Search, "Search")
                    // }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddContact,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = { Text("Add Contact") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (contacts.isEmpty()) {
                EmptyContactsState(
                    onAddContact = onAddContact,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(AuraSpacing.comfortable),
                    verticalArrangement = Arrangement.spacedBy(AuraSpacing.default)
                ) {
                    // Header stats
                    item {
                        ContactListHeader(contactCount = contacts.size)
                    }
                    
                    // Contact cards
                    items(
                        items = contacts,
                        key = { it.id }
                    ) { contact ->
                        ContactCard(
                            contact = contact,
                            onDelete = null, // DISABLED: Delete not allowed from main contact screen
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                    
                    // Bottom spacer for FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

/**
 * Contact List Header with stats
 */
@Composable
private fun ContactListHeader(
    contactCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = AuraSpacing.tight),
        verticalArrangement = Arrangement.spacedBy(AuraSpacing.minimal)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AuraSpacing.tight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(AuraSizes.iconSmall)
            )
            Text(
                text = "$contactCount Emergency Contact${if (contactCount != 1) "s" else ""}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Text(
            text = "These contacts will receive alerts when you trigger SOS",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Divider(modifier = Modifier.padding(top = AuraSpacing.default))
    }
}

/**
 * Empty State when no contacts exist
 */
@Composable
private fun EmptyContactsState(
    onAddContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(AuraSpacing.generous),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AuraSpacing.comfortable)
    ) {
        // Illustration
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        // Text content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AuraSpacing.tight)
        ) {
            Text(
                text = "No Emergency Contacts",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Add trusted contacts who will be notified during emergencies. They'll receive your location and can help coordinate assistance.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = AuraSpacing.comfortable)
            )
        }
        
        // CTA Button
        Button(
            onClick = onAddContact,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AuraSpacing.generous),
            contentPadding = PaddingValues(
                horizontal = AuraSpacing.comfortable,
                vertical = AuraSpacing.default
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(AuraSizes.iconMedium)
            )
            Spacer(modifier = Modifier.width(AuraSpacing.tight))
            Text(
                "Add Your First Contact",
                style = MaterialTheme.typography.labelLarge
            )
        }
        
        // Info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AuraSpacing.comfortable),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AuraSpacing.comfortable),
                verticalArrangement = Arrangement.spacedBy(AuraSpacing.tight)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AuraSpacing.tight)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(AuraSizes.iconSmall)
                    )
                    Text(
                        "What you need",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                
                ContactRequirement(text = "Contact's phone number")
                ContactRequirement(text = "Their name")
                ContactRequirement(text = "Your relationship (e.g., Mom, Friend)")
                ContactRequirement(text = "Phone number verification via OTP")
            }
        }
    }
}

/**
 * Single requirement item
 */
@Composable
private fun ContactRequirement(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(start = AuraSpacing.default),
        horizontalArrangement = Arrangement.spacedBy(AuraSpacing.tight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}
