package com.womensafety.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import com.womensafety.app.ui.theme.AuraSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraAddContactDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, relationship: String, email: String, location: String) -> Unit,
    initialName: String = "",
    initialPhone: String = "",
    isDuplicate: (String) -> Boolean = { false }
) {
    var name by remember { mutableStateOf(initialName) }
    var phone by remember { mutableStateOf(initialPhone) }
    var relationship by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var phoneError by remember { mutableStateOf(false) }
    var phoneErrorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add Emergency Contact",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AuraSpacing.default),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { newValue ->
                        val digitsOnly = newValue.filter { it.isDigit() }
                        if (digitsOnly.length <= 10) {
                            phone = digitsOnly
                            phoneError = false
                            phoneErrorText = null
                        }
                    },
                    label = { Text("Phone Number") },
                    leadingIcon = { 
                        Text(
                            text = "+91",
                            modifier = Modifier.padding(start = 12.dp),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = MaterialTheme.shapes.medium,
                    isError = phoneError || phoneErrorText != null,
                    supportingText = {
                        phoneErrorText?.let { Text(it) } ?: if (phoneError) {
                            Text("Enter a valid 10-digit phone number")
                        } else {
                            Text("10 digits required")
                        }
                    },
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (e.g. Home, Office)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fullPhone = "+91$phone"
                    if (isDuplicate(fullPhone)) {
                        phoneErrorText = "Contact already added"
                    } else if (name.isNotBlank() && phone.length == 10) {
                        onConfirm(name, fullPhone, relationship, email, location)
                    } else if (name.isNotBlank() && phone.length < 10) {
                        phoneError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63),
                    contentColor = Color.White
                )
            ) {
                Text("Add Contact")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFE91E63)
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraEditProfileDialog(
    title: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    label: String = "Enter value",
    isPhoneField: Boolean = false,
    isEmailField: Boolean = false
) {
    // If it's a phone field, strip +91 for the editor
    val displayValue = if (isPhoneField && initialValue.startsWith("+91")) {
        initialValue.substring(3).filter { it.isDigit() }.take(10)
    } else {
        initialValue
    }

    var value by remember { mutableStateOf(displayValue) }
    var error by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Email validation function
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        if (!email.matches(emailPattern.toRegex())) return false
        
        // Check for valid domains
        val validDomains = listOf(
            "gmail.com", "yahoo.com", "outlook.com", "hotmail.com",
            "icloud.com", "protonmail.com", "zoho.com", "aol.com",
            "mail.com", "yandex.com", "gmx.com"
        )
        
        val domain = email.substringAfter("@").lowercase()
        return validDomains.any { domain.endsWith(it) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (isPhoneField) {
                            val digitsOnly = newValue.filter { it.isDigit() }
                            if (digitsOnly.length <= 10) {
                                value = digitsOnly
                                error = false
                                errorMessage = ""
                            }
                        } else {
                            value = newValue
                            error = false
                            errorMessage = ""
                        }
                    },
                    label = { Text(label) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when {
                            isPhoneField -> KeyboardType.Phone
                            isEmailField -> KeyboardType.Email
                            else -> KeyboardType.Text
                        }
                    ),
                    leadingIcon = if (isPhoneField) {
                        { 
                            Text(
                                text = "+91", 
                                modifier = Modifier.padding(start = 12.dp),
                                fontWeight = FontWeight.Bold 
                            ) 
                        }
                    } else null,
                    isError = error,
                    supportingText = {
                        when {
                            isPhoneField -> Text(
                                if (error) errorMessage.ifBlank { "10 digits required" } 
                                else "Exactly 10 digits"
                            )
                            isEmailField -> Text(
                                if (error) errorMessage.ifBlank { "Enter valid email (e.g., gmail.com, yahoo.com)" }
                                else "Enter a valid email address"
                            )
                            else -> null
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        isPhoneField -> {
                            if (value.length == 10) {
                                onConfirm("+91$value")
                                onDismiss()
                            } else {
                                error = true
                                errorMessage = "Phone number must be exactly 10 digits"
                            }
                        }
                        isEmailField -> {
                            if (isValidEmail(value)) {
                                onConfirm(value)
                                onDismiss()
                            } else {
                                error = true
                                errorMessage = if (value.isBlank()) {
                                    "Email cannot be empty"
                                } else if (!value.contains("@")) {
                                    "Email must contain @"
                                } else {
                                    "Use valid email domain (gmail.com, yahoo.com, etc.)"
                                }
                            }
                        }
                        else -> {
                            if (value.isNotBlank()) {
                                onConfirm(value)
                                onDismiss()
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63),
                    contentColor = Color.White
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFE91E63)
                )
            ) {
                Text("Cancel")
            }
        }
    )
}
