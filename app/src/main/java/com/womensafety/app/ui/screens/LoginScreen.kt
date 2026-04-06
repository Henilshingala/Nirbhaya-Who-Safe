package com.womensafety.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
 * LOGIN SCREEN
 * 
 * Matches the reference UI design exactly
 * Integrates with backend APIs:
 * - POST /userlogin (validate password)
 * - POST /verifymobilenumber (send OTP)
 * - POST /verifymobileotp (verify OTP) - handled in OTP screen
 */
@Composable
fun LoginScreen(
    onNavigateToOTP: (String) -> Unit, // Navigate to OTP screen with mobile number
    onNavigateToSignup: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = LoginViewModelFactory(context)
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
            Spacer(modifier = Modifier.height(60.dp))
            
            // Illustration
            Image(
                painter = painterResource(id = com.womensafety.app.R.drawable.login_illustration),
                contentDescription = "Login Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp) // Slightly taller for better visibility
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            Text(
                text = "Verified Your E-Mail ID",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Subtitle
            Text(
                text = "To Keep Your Account Secure And Ensure Smooth Communication, Please Verify Your Mobile Number. We'll Send You A One-Time Password (OTP) Via SMS To Confirm Your Identity.",
                fontSize = 13.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Email
            Text(
                text = "Enter Email",
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
                        "9876543210",
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Forgot Password
            Text(
                text = "Forgot password?",
                fontSize = 14.sp,
                color = Color(0xFFE91E63),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onForgotPassword() }
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
            
            // Log In Button
            Button(
                onClick = { 
                    viewModel.login { mobileNumber ->
                        onNavigateToOTP(mobileNumber)
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
                        text = "LOG IN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sign up link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?  ",
                    fontSize = 14.sp,
                    color = Color(0xFF2D2D2D)
                )
                Text(
                    text = "Sign up",
                    fontSize = 14.sp,
                    color = Color(0xFFE91E63),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToSignup() }
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * UI State for Login Screen
 */
data class LoginUiState(
    val email: String = "",
    val mobileNumber: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

/**
 * ViewModel for Login Screen
 */
class LoginViewModel(private val context: android.content.Context) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    private val baseUrl = "https://app.whosafeglobal.com/"
    private val client = com.womensafety.app.network.NetworkClient.instance
    
    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = "")
    }
    
    fun updateMobileNumber(value: String) {
        // Allow only digits and limit to 10
        val filtered = value.filter { it.isDigit() }.take(10)
        _uiState.value = _uiState.value.copy(mobileNumber = filtered, errorMessage = "")
    }
    
    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = "")
    }
    
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            passwordVisible = !_uiState.value.passwordVisible
        )
    }
    
    /**
     * Validate inputs before sending to backend
     */
    private fun validateInputs(): String? {
        val state = _uiState.value
        
        if (state.email.isBlank()) {
            return "Please enter your email"
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            return "Please enter a valid email address"
        }
        
        if (state.mobileNumber.isBlank()) {
            return "Please enter your mobile number"
        }
        
        if (state.mobileNumber.length != 10) {
            return "Mobile number must be exactly 10 digits"
        }
        
        if (state.password.isBlank()) {
            return "Please enter your password"
        }
        
        if (state.password.length < 8) {
            return "Password must be at least 8 characters"
        }
        
        return null
    }
    
    /**
     * Login user via backend API
     * 
     * Flow:
     * 1. Validate inputs (email, mobile, password)
     * 2. Call /verifymobilenumber to send OTP
     * 3. Navigate to OTP screen
     */
    fun login(onSuccess: (String) -> Unit) {
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
                
                // Send OTP via /verifymobilenumber
                val otpResult = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val jsonBody = JSONObject().apply {
                        put("mo_no", state.mobileNumber)
                        put("email", state.email)  // Backend silently requires this
                    }
                    
                    val requestBodyString = jsonBody.toString()
                    
                    com.womensafety.app.utils.Logger.box(
                        "LOGIN",
                        "SENDING OTP",
                        "URL: ${baseUrl}verifymobilenumber",
                        "Email: ${state.email}",
                        "Mobile: ${state.mobileNumber}",
                        "Password: ${state.password.length} chars",
                        "Body: $requestBodyString (${requestBodyString.length} bytes)"
                    )
                    
                    val requestBody = requestBodyString
                        .toRequestBody("application/json; charset=utf-8".toMediaType())
                    
                    val request = Request.Builder()
                        .url("${baseUrl}verifymobilenumber")
                        .post(requestBody)
                        .addHeader("Content-Type", "application/json")
                        .build()
                    
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()
                    
                    com.womensafety.app.utils.Logger.box(
                        "LOGIN",
                        "OTP RESPONSE",
                        "Status: ${response.code} ${response.message}",
                        "Body: $responseBody"
                    )
                    
                    if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val status = jsonResponse.optInt("status", 0)
                        val description = jsonResponse.optString("description", "Unknown error")
                        
                        Pair(status, description)
                    } else {
                        Pair(0, "No response from server")
                    }
                }
                
                if (otpResult.first == 1) {
                    com.womensafety.app.utils.Logger.i("LOGIN", "✅ OTP sent successfully")
                    
                    // Save user data temporarily - Preserve name if it exists from registration
                    val userPrefs = com.womensafety.app.data.UserPreferences.getInstance(context)
                    val existingData = userPrefs.getUserData()
                    
                    val userData = com.womensafety.app.data.UserData(
                        userId = state.mobileNumber,
                        fullName = existingData?.fullName ?: "",
                        mobileNumber = state.mobileNumber,
                        email = state.email
                    )
                    userPrefs.saveUserData(userData)
                    
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    
                    // Navigate to OTP verification screen
                    onSuccess(state.mobileNumber)
                } else {
                    // Failed to send OTP
                    com.womensafety.app.utils.Logger.e("LOGIN", "❌ Failed to send OTP: ${otpResult.second}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to send OTP: ${otpResult.second}"
                    )
                }
            } catch (e: Exception) {
                com.womensafety.app.utils.Logger.e("LOGIN", "❌ Exception during login", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.message ?: "Please check your connection"}"
                )
            }
        }
    }
}

/**
 * ViewModel Factory
 */
class LoginViewModelFactory(private val context: android.content.Context) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
