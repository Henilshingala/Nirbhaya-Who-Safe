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
 * FORGOT PASSWORD SCREEN
 * 
 * 3-Step Flow:
 * 1. Enter Email -> API /forgotpassword (sends OTP)
 * 2. Enter OTP -> API /verifymailotp
 * 3. Enter New Password -> API /resetpassword
 */
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: ForgotPasswordViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Background illustration (dimmed)
        if (uiState.step == ForgotPasswordStep.EMAIL) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(
                    id = com.womensafety.app.R.drawable.forgot_password_illustration
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                alpha = 0.3f
            )
        }
        
        // Card/Modal content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (uiState.step == ForgotPasswordStep.EMAIL) 
                Arrangement.Center 
            else 
                Arrangement.Top
        ) {
            if (uiState.step != ForgotPasswordStep.EMAIL) {
                Spacer(modifier = Modifier.height(50.dp))
                
                // Back button for OTP and NEW_PASSWORD steps
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
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
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Card container
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (uiState.step == ForgotPasswordStep.EMAIL) 24.dp else 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(
                    defaultElevation = if (uiState.step == ForgotPasswordStep.EMAIL) 8.dp else 0.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = when(uiState.step) {
                            ForgotPasswordStep.EMAIL -> "Forgot your password"
                            ForgotPasswordStep.OTP -> "Verify OTP"
                            ForgotPasswordStep.NEW_PASSWORD -> "Reset Password"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Subtitle
                    Text(
                        text = when(uiState.step) {
                            ForgotPasswordStep.EMAIL -> "Please enter the email address you'd like your password reset information sent to"
                            ForgotPasswordStep.OTP -> "Enter the 6-digit OTP sent to your email address: ${uiState.email}"
                            ForgotPasswordStep.NEW_PASSWORD -> "Create a new strong password for your account."
                        },
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        lineHeight = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Content based on Step
                    when (uiState.step) {
                        ForgotPasswordStep.EMAIL -> EmailInputContent(uiState, viewModel)
                        ForgotPasswordStep.OTP -> OtpInputContent(uiState, viewModel)
                        ForgotPasswordStep.NEW_PASSWORD -> NewPasswordInputContent(uiState, viewModel)
                    }
                    
                    // Error message
                    if (uiState.errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.errorMessage,
                            color = Color.Red,
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Success message
                    if (uiState.successMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.successMessage,
                            color = Color(0xFF4CAF50), // Green
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Action Button
                    Button(
                        onClick = { 
                             when(uiState.step) {
                                 ForgotPasswordStep.EMAIL -> viewModel.submitEmail()
                                 ForgotPasswordStep.OTP -> viewModel.verifyOtp()
                                 ForgotPasswordStep.NEW_PASSWORD -> viewModel.resetPassword(onSuccess = onBack)
                             }
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
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
                                text = when(uiState.step) {
                                    ForgotPasswordStep.EMAIL -> "REQUEST RESET LINK"
                                    ForgotPasswordStep.OTP -> "VERIFY OTP"
                                    ForgotPasswordStep.NEW_PASSWORD -> "UPDATE PASSWORD"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    
                    // "Back to Login" link (only for EMAIL step)
                    if (uiState.step == ForgotPasswordStep.EMAIL) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Back to Login",
                            fontSize = 14.sp,
                            color = Color(0xFFE91E63),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onBack() }
                        )
                    }
                }
            }
            
            if (uiState.step != ForgotPasswordStep.EMAIL) {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun EmailInputContent(uiState: ForgotPasswordUiState, viewModel: ForgotPasswordViewModel) {
    Text(
        text = "Enter E-mail",
        fontSize = 14.sp,
        color = Color.Black,
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
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color(0xFFE91E63),
            cursorColor = Color(0xFFE91E63)
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
}

@Composable
fun OtpInputContent(uiState: ForgotPasswordUiState, viewModel: ForgotPasswordViewModel) {
    Text(
        text = "Enter 6-digit OTP",
        fontSize = 14.sp,
        color = Color.Black,
        fontWeight = FontWeight.Normal
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = uiState.otp,
        onValueChange = { viewModel.updateOtp(it) },
        placeholder = { 
            Text(
                "123456",
                color = Color.Gray,
                fontSize = 14.sp
            ) 
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color(0xFFE91E63),
            cursorColor = Color(0xFFE91E63)
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun NewPasswordInputContent(uiState: ForgotPasswordUiState, viewModel: ForgotPasswordViewModel) {
    // New Password
    Text(
        text = "New Password",
        fontSize = 14.sp,
        color = Color.Black,
        fontWeight = FontWeight.Normal
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = uiState.newPassword,
        onValueChange = { viewModel.updateNewPassword(it) },
        placeholder = { 
            Text(
                "Min 8 chars",
                color = Color.Gray,
                fontSize = 14.sp
            ) 
        },
        visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                Icon(
                    imageVector = if (uiState.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = Color(0xFFE91E63)
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color(0xFFE91E63),
            cursorColor = Color(0xFFE91E63)
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Confirm Password
    Text(
        text = "Confirm Password",
        fontSize = 14.sp,
        color = Color.Black,
        fontWeight = FontWeight.Normal
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = uiState.confirmPassword,
        onValueChange = { viewModel.updateConfirmPassword(it) },
        placeholder = { 
            Text(
                "Retype password",
                color = Color.Gray,
                fontSize = 14.sp
            ) 
        },
        visualTransformation = if (uiState.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                Icon(
                    imageVector = if (uiState.confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = Color(0xFFE91E63)
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color(0xFFE91E63),
            cursorColor = Color(0xFFE91E63)
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

enum class ForgotPasswordStep {
    EMAIL,
    OTP,
    NEW_PASSWORD
}

data class ForgotPasswordUiState(
    val step: ForgotPasswordStep = ForgotPasswordStep.EMAIL,
    val email: String = "",
    val otp: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)

class ForgotPasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()
    
    private val client = com.womensafety.app.network.NetworkClient.instance
        
    private val baseUrl = "https://app.whosafeglobal.com/"
    
    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = "")
    }
    
    fun updateOtp(value: String) {
        if (value.length <= 6 && value.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(otp = value, errorMessage = "")
        }
    }
    
    fun updateNewPassword(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value, errorMessage = "")
    }
    
    fun updateConfirmPassword(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = "")
    }
    
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(passwordVisible = !_uiState.value.passwordVisible)
    }
    
    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(confirmPasswordVisible = !_uiState.value.confirmPasswordVisible)
    }
    
    /**
     * STEP 1: Send OTP
     */
    fun submitEmail() {
        val email = _uiState.value.email.trim()
        
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid email address")
            return
        }
        
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "", successMessage = "")
        
        viewModelScope.launch {
            try {
                // Determine API endpoint
                val result = performApiCall("forgotpassword", JSONObject().put("email", email))
                
                if (result.first == 1) {
                     _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "OTP sent successfully to $email",
                        step = ForgotPasswordStep.OTP
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.second.ifEmpty { "Failed to send OTP." }
                    )
                }
            } catch (e: Exception) {
                 handleError(e)
            }
        }
    }
    
    /**
     * STEP 2: Verify OTP
     * Endpoint: /verifymailotp (As requested)
     */
    fun verifyOtp() {
        val otp = _uiState.value.otp.trim()
        val email = _uiState.value.email.trim()
        
        if (otp.length != 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid 6-digit OTP")
            return
        }
        
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "", successMessage = "")
        
        viewModelScope.launch {
            try {
                val result = performApiCall("verifymailotp", 
                    JSONObject().put("email", email).put("otp", otp)
                )
                
                if (result.first == 1) {
                     _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "OTP verified successfully",
                        step = ForgotPasswordStep.NEW_PASSWORD
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.second.ifEmpty { "Invalid OTP." }
                    )
                }
            } catch (e: Exception) {
                 handleError(e)
            }
        }
    }
    
    /**
     * STEP 3: Reset Password
     * Endpoint: /resetpassword (As requested)
     */
    fun resetPassword(onSuccess: () -> Unit) {
        val password = _uiState.value.newPassword
        val confirm = _uiState.value.confirmPassword
        val email = _uiState.value.email.trim()
        
        if (password.length < 8) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 8 characters")
            return
        }
        
        if (password != confirm) {
           _uiState.value = _uiState.value.copy(errorMessage = "Passwords do not match")
            return
        }
        
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "", successMessage = "")
        
        viewModelScope.launch {
            try {
                val result = performApiCall("resetpassword", 
                    JSONObject().put("email", email).put("password", password)
                )
                
                if (result.first == 1) {
                     _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Password updated successfully!"
                    )
                    // Delay slightly to let user see success message
                    kotlinx.coroutines.delay(1000)
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.second.ifEmpty { "Failed to update password." }
                    )
                }
            } catch (e: Exception) {
                 handleError(e)
            }
        }
    }
    
    private suspend fun performApiCall(endpoint: String, jsonBody: JSONObject): Pair<Int, String> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val requestBody = jsonBody.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())
                
            val request = Request.Builder()
                .url("$baseUrl$endpoint")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build()
            
            android.util.Log.d("ForgotPassword", "Calling $endpoint with $jsonBody")
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            android.util.Log.d("ForgotPassword", "Response from $endpoint: $responseBody")
            
            if (responseBody != null) {
                try {
                    val jsonResponse = JSONObject(responseBody)
                    val status = jsonResponse.optInt("status", 0)
                    val description = jsonResponse.optString("description", "Unknown error")
                    
                    val message = jsonResponse.optString("message", "")
                    val finalMessage = if (description == "Unknown error" && message.isNotEmpty()) message else description
                    
                    Pair(status, finalMessage)
                } catch (e: Exception) {
                    Pair(0, "Invalid server response")
                }
            } else {
                Pair(0, "No response from server")
            }
        }
    }
    
    private fun handleError(e: Exception) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = "Network error: ${e.message ?: "Please check your connection"}"
        )
    }
}
