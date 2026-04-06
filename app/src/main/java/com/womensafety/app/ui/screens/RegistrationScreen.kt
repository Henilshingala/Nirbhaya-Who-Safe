package com.womensafety.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.womensafety.app.network.NetworkClient

/**
 * REGISTRATION SCREEN
 * 
 * Matches the reference UI design exactly
 * Integrates with backend API: POST /userregister
 */
@Composable
fun RegistrationScreen(
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: RegistrationViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = RegistrationViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsState()
    
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title
            Text(
                text = "Sign Up",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Full Name
            Text(
                text = "Full Name",
                fontSize = 14.sp,
                color = Color(0xFF2D2D2D),
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = { viewModel.updateFullName(it) },
                placeholder = { 
                    Text(
                        "Enter your name",
                        color = Color.Gray,
                        fontSize = 14.sp
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White.copy(alpha = 0.6f),
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedBorderColor = Color(0xFFE91E63),
                    cursorColor = Color(0xFFE91E63),
                    focusedTextColor = Color(0xFF1A1A1A),
                    unfocusedTextColor = Color(0xFF1A1A1A)
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Mobile Number
            Text(
                text = "Enter Mobile Number",
                fontSize = 14.sp,
                color = Color(0xFF2D2D2D),
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.mobileNumber,
                onValueChange = { viewModel.updateMobileNumber(it) },
                placeholder = { 
                    Text(
                        "98988 XXXXX",
                        color = Color.Gray,
                        fontSize = 14.sp
                    ) 
                },
                leadingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "🇮🇳",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "▼",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+91",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White.copy(alpha = 0.6f),
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedBorderColor = Color(0xFFE91E63),
                    cursorColor = Color(0xFFE91E63),
                    focusedTextColor = Color(0xFF1A1A1A),
                    unfocusedTextColor = Color(0xFF1A1A1A)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Email
            Text(
                text = "Enter E-mail",
                fontSize = 14.sp,
                color = Color(0xFF2D2D2D),
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                placeholder = { 
                    Text(
                        "name@example.com",
                        color = Color.Gray,
                        fontSize = 14.sp
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White.copy(alpha = 0.6f),
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedBorderColor = Color(0xFFE91E63),
                    cursorColor = Color(0xFFE91E63),
                    focusedTextColor = Color(0xFF1A1A1A),
                    unfocusedTextColor = Color(0xFF1A1A1A)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Password
            Text(
                text = "Password",
                fontSize = 14.sp,
                color = Color(0xFF2D2D2D),
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                placeholder = { 
                    Text(
                        "min. 8 characters",
                        color = Color.Gray,
                        fontSize = 14.sp
                    ) 
                },
                visualTransformation = if (uiState.passwordVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (uiState.passwordVisible) 
                                Icons.Default.Visibility 
                            else 
                                Icons.Default.VisibilityOff,
                            contentDescription = if (uiState.passwordVisible) 
                                "Hide password" 
                            else 
                                "Show password",
                            tint = Color(0xFFE91E63)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White.copy(alpha = 0.6f),
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedBorderColor = Color(0xFFE91E63),
                    cursorColor = Color(0xFFE91E63),
                    focusedTextColor = Color(0xFF1A1A1A),
                    unfocusedTextColor = Color(0xFF1A1A1A)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Re-enter Password
            Text(
                text = "Re-enter Password",
                fontSize = 14.sp,
                color = Color(0xFF2D2D2D),
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.updateConfirmPassword(it) },
                placeholder = { 
                    Text(
                        "min. 8 characters",
                        color = Color.Gray,
                        fontSize = 14.sp
                    ) 
                },
                visualTransformation = if (uiState.confirmPasswordVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                        Icon(
                            imageVector = if (uiState.confirmPasswordVisible) 
                                Icons.Default.Visibility 
                            else 
                                Icons.Default.VisibilityOff,
                            contentDescription = if (uiState.confirmPasswordVisible) 
                                "Hide password" 
                            else 
                                "Show password",
                            tint = Color(0xFFE91E63)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White.copy(alpha = 0.6f),
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedBorderColor = Color(0xFFE91E63),
                    cursorColor = Color(0xFFE91E63),
                    focusedTextColor = Color(0xFF1A1A1A),
                    unfocusedTextColor = Color(0xFF1A1A1A)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            
            // Error message
            if (uiState.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Sign Up Button
            Button(
                onClick = { 
                    viewModel.register {
                        onNavigateToLogin()
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63),
                    disabledContainerColor = Color(0xFFE91E63).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "SIGN UP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Login link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Have an account already?  ",
                    fontSize = 14.sp,
                    color = Color(0xFF2D2D2D)
                )
                Text(
                    text = "Log in",
                    fontSize = 14.sp,
                    color = Color(0xFFE91E63),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * UI State for Registration Screen
 */
data class RegistrationUiState(
    val fullName: String = "",
    val mobileNumber: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

/**
 * ViewModel for Registration Screen
 * Handles backend API integration and user data persistence
 */
class RegistrationViewModel(private val context: android.content.Context) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()
    
    private val userPreferences = com.womensafety.app.data.UserPreferences.getInstance(context)
    
    private val client = com.womensafety.app.network.NetworkClient.instance
    
    private val baseUrl = "https://app.whosafeglobal.com/"
    
    fun updateFullName(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value, errorMessage = "")
    }
    
    fun updateMobileNumber(value: String) {
        // Only allow digits
        val filtered = value.filter { it.isDigit() }
        _uiState.value = _uiState.value.copy(mobileNumber = filtered, errorMessage = "")
    }
    
    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = "")
    }
    
    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = "")
    }
    
    fun updateConfirmPassword(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = "")
    }
    
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            passwordVisible = !_uiState.value.passwordVisible
        )
    }
    
    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            confirmPasswordVisible = !_uiState.value.confirmPasswordVisible
        )
    }
    
    /**
     * Validate inputs before sending to backend
     */
    private fun validateInputs(): String? {
        val state = _uiState.value
        
        if (state.fullName.isBlank()) {
            return "Please enter your full name"
        }
        
        if (state.mobileNumber.isBlank()) {
            return "Please enter your mobile number"
        }
        
        if (state.mobileNumber.length != 10) {
            return "Mobile number must be exactly 10 digits"
        }
        
        if (state.email.isBlank()) {
            return "Please enter your email"
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            return "Please enter a valid email address"
        }
        
        // Check for valid email domains
        val validDomains = listOf(
            "gmail.com", "yahoo.com", "outlook.com", "hotmail.com",
            "icloud.com", "protonmail.com", "zoho.com", "aol.com",
            "mail.com", "yandex.com", "gmx.com"
        )
        val emailDomain = state.email.substringAfter("@").lowercase()
        if (!validDomains.any { emailDomain.endsWith(it) }) {
            return "Please use a valid email domain (gmail.com, yahoo.com, etc.)"
        }
        
        if (state.password.isBlank()) {
            return "Please enter a password"
        }
        
        if (state.password.length < 8) {
            return "Password must be at least 8 characters"
        }
        
        if (state.confirmPassword.isBlank()) {
            return "Please re-enter your password"
        }
        
        if (state.password != state.confirmPassword) {
            return "Passwords do not match"
        }
        
        return null
    }
    
    /**
     * Register user directly via backend API (NO OTP)
     * POST /userregister
     * 
     * Flow:
     * 1. Validate all inputs
     * 2. Create user account
     * 3. Navigate to Login screen
     * 
     * OTP verification happens during LOGIN, not registration
     */
    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Validate inputs
            val validationError = validateInputs()
            if (validationError != null) {
                _uiState.value = _uiState.value.copy(errorMessage = validationError)
                return@launch
            }
            
            // Set loading state
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            
            try {
                val state = _uiState.value
                
                // Call /userregister to create account
                val result = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val jsonBody = JSONObject().apply {
                        put("full_name", state.fullName)
                        put("mo_no", state.mobileNumber)
                        put("email", state.email)
                        put("password", state.password)
                    }
                    
                    android.util.Log.d("REGISTRATION", "╔════════════════════════════════════════════════╗")
                    android.util.Log.d("REGISTRATION", "║ CREATING USER ACCOUNT                          ║")
                    android.util.Log.d("REGISTRATION", "╠════════════════════════════════════════════════╣")
                    android.util.Log.d("REGISTRATION", "║ URL: ${baseUrl}userregister")
                    android.util.Log.d("REGISTRATION", "║ Mobile: ${state.mobileNumber}")
                    android.util.Log.d("REGISTRATION", "╚════════════════════════════════════════════════╝")
                    
                    val requestBody = jsonBody.toString()
                        .toRequestBody("application/json; charset=utf-8".toMediaType())
                    
                    val request = Request.Builder()
                        .url("${baseUrl}userregister")
                        .post(requestBody)
                        .addHeader("Content-Type", "application/json")
                        .build()
                    
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()
                    
                    android.util.Log.d("REGISTRATION", "╔════════════════════════════════════════════════╗")
                    android.util.Log.d("REGISTRATION", "║ REGISTRATION RESPONSE                          ║")
                    android.util.Log.d("REGISTRATION", "╠════════════════════════════════════════════════╣")
                    android.util.Log.d("REGISTRATION", "║ HTTP Status: ${response.code}")
                    android.util.Log.d("REGISTRATION", "║ Response: $responseBody")
                    android.util.Log.d("REGISTRATION", "╚════════════════════════════════════════════════╝")
                    
                    if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val status = jsonResponse.optInt("status", 0)
                        val description = jsonResponse.optString("description", "Unknown error")
                        
                        Pair(status, description)
                    } else {
                        Pair(0, "No response from server")
                    }
                }
                
                // Handle result
                if (result.first == 1) {
                    // Success - Account created
                    android.util.Log.d("REGISTRATION", "✅ Account created successfully")
                    
                    // SINGLE SOURCE OF TRUTH: Save exact user-entered name to UserPreferences ONLY
                    val userPrefs = com.womensafety.app.data.UserPreferences.getInstance(context)
                    val userData = com.womensafety.app.data.UserData(
                        userId = state.mobileNumber, // Temporary ID until real one is assigned
                        fullName = state.fullName,
                        mobileNumber = state.mobileNumber,
                        email = state.email
                    )
                    userPrefs.saveUserData(userData)
                    android.util.Log.d("USERNAME_FLOW", "📝 WRITE: Saved username to UserPreferences: '${state.fullName}'")
                    
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    
                    // Navigate to Login screen (no OTP during registration)
                    onSuccess()
                } else {
                    // Failed
                    android.util.Log.e("REGISTRATION", "❌ Registration failed: ${result.second}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.second
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("REGISTRATION", "❌ Exception during registration", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.message ?: "Please check your connection"}"
                )
            }
        }
    }
}

/**
 * Factory for creating RegistrationViewModel with Context dependency
 */
class RegistrationViewModelFactory(
    private val context: android.content.Context
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrationViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

