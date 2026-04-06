package com.womensafety.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.womensafety.app.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.womensafety.app.network.NetworkClient

/**
 * NEW OTP VERIFICATION SCREEN
 * 
 * Shown after successful registration to verify mobile number
 * Integrates with backend API: POST /verifymobileotp
 */
@Composable
fun NewOTPVerificationScreen(
    mobileNumber: String,
    isLoginFlow: Boolean = false,
    onVerificationSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: NewOTPVerificationViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = NewOTPVerificationViewModelFactory(context, mobileNumber, isLoginFlow)
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
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title
            Text(
                text = "Verify OTP",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle
            Text(
                text = "Enter the OTP sent to",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mobile number display
            Text(
                text = "+91 $mobileNumber",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFE91E63),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // OTP Input
            Text(
                text = "Enter OTP",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.otp,
                onValueChange = { viewModel.updateOTP(it) },
                placeholder = { 
                    Text(
                        "Enter 6-digit OTP",
                        color = Color.Gray,
                        fontSize = 14.sp
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White.copy(alpha = 0.6f),
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedBorderColor = Color(0xFFE91E63),
                    cursorColor = Color(0xFFE91E63),
                    errorBorderColor = Color(0xFFE91E63),
                    errorContainerColor = Color.White.copy(alpha = 0.8f)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = uiState.errorMessage.isNotEmpty()
            )
            
            // Error message
            if (uiState.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Verify Button
            Button(
                onClick = { 
                    viewModel.verifyOTP {
                        onVerificationSuccess()
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
                        text = "VERIFY OTP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Resend OTP (optional - can be implemented later)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Didn't receive OTP?  ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Resend",
                    fontSize = 14.sp,
                    color = if (uiState.isLoading) Color.Gray else Color(0xFFE91E63),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(enabled = !uiState.isLoading) { 
                        viewModel.resendOTP()
                    }
                )
            }
            
            // Success message for Resend OTP
            if (uiState.successMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.successMessage,
                    color = Color(0xFF4CAF50),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * UI State for OTP Verification Screen
 */
data class NewOTPVerificationUiState(
    val otp: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)

/**
 * ViewModel for OTP Verification Screen
 * Handles backend API integration for /verifymobileotp
 */
class NewOTPVerificationViewModel(
    private val context: android.content.Context,
    private val mobileNumber: String,
    private val isLoginFlow: Boolean
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewOTPVerificationUiState())
    val uiState: StateFlow<NewOTPVerificationUiState> = _uiState.asStateFlow()

    private val client = NetworkClient.instance

    private val baseUrl = "https://app.whosafeglobal.com/"

    fun updateOTP(value: String) {
        // Only allow digits, max 6 characters
        val filtered = value.filter { it.isDigit() }.take(6)
        _uiState.value = _uiState.value.copy(otp = filtered, errorMessage = "")
    }

    /**
     * Verify OTP via backend API
     * POST /verifymobileotp
     *
     * Request body:
     * {
     *   "mo_no": "9876543210",
     *   "otp": "123456"
     * }
     *
     * Response:
     * {
     *   "status": 1 or 0,
     *   "description": "message"
     * }
     */
    fun verifyOTP(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Validate OTP
            val otp = _uiState.value.otp
            if (otp.isBlank()) {
                _uiState.value = _uiState.value.copy(errorMessage = "Please enter OTP")
                return@launch
            }

            if (otp.length != 6) {
                _uiState.value = _uiState.value.copy(errorMessage = "OTP must be 6 digits")
                return@launch
            }

            // Set loading state
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")

            try {
                // STEP 1: Verify OTP with backend
                val otpVerifyResult =
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        val jsonBody = JSONObject().apply {
                            put("mo_no", mobileNumber)
                            put("otp", otp)
                        }

                        val requestBody = jsonBody.toString()
                            .toRequestBody("application/json; charset=utf-8".toMediaType())

                        val request = Request.Builder()
                            .url("${baseUrl}verifymobileotp")
                            .post(requestBody)
                            .addHeader("Content-Type", "application/json")
                            .build()

                        val response = client.newCall(request).execute()
                        val responseBody = response.body?.string()

                        if (BuildConfig.DEBUG) {
                            android.util.Log.d("OTP_VERIFY", "OTP Verification Response received")
                        }

                        if (responseBody != null) {
                            val jsonResponse = JSONObject(responseBody)
                            val status = jsonResponse.optInt("status", 0)
                            val description = jsonResponse.optString("description", "Unknown error")
                            
                            // Return the whole JSON object to extract data later
                            Triple(status, description, jsonResponse)
                        } else {
                            Triple(0, "No response from server", null)
                        }
                    }

                // Check OTP verification result
                if (otpVerifyResult.first == 1) {
                    if (BuildConfig.DEBUG) {
                        android.util.Log.d("OTP_VERIFY", "OTP verified successfully")
                    }

                    if (isLoginFlow) {
                        // Extract and save user details if available
                        val jsonResponse = otpVerifyResult.third
                        if (jsonResponse != null) {
                            try {
                                val userPrefs = com.womensafety.app.data.UserPreferences.getInstance(context)
                                val currentData = userPrefs.getUserData()
                                
                                android.util.Log.d("LOGIN_DEBUG", "Starting user data extraction from response: $jsonResponse")
                                
                                // Check for data object, array, or root level fields
                                var dataObj: JSONObject? = jsonResponse.optJSONObject("data")
                                if (dataObj == null) {
                                    val dataArray = jsonResponse.optJSONArray("data")
                                    if (dataArray != null && dataArray.length() > 0) {
                                        dataObj = dataArray.optJSONObject(0)
                                    }
                                }
                                if (dataObj == null) {
                                    dataObj = jsonResponse
                                }
                                
                                android.util.Log.d("LOGIN_DEBUG", "Using data object: $dataObj")
                                
                                // CRITICAL FIX: Backend sends u_id in data.dataValues.u_id, NOT data.u_id
                                // First, check if there's a nested dataValues object
                                var userId = ""
                                val dataValuesObj = dataObj.optJSONObject("dataValues")
                                
                                android.util.Log.d("LOGIN_DEBUG", "Extracting user_id from backend response...")
                                android.util.Log.d("LOGIN_DEBUG", "  - Full data object: $dataObj")
                                android.util.Log.d("LOGIN_DEBUG", "  - dataValues object: $dataValuesObj")
                                
                                if (dataValuesObj != null) {
                                    // Backend sends: data.dataValues.u_id = 130
                                    userId = dataValuesObj.optString("u_id")
                                    if (userId.isEmpty()) userId = dataValuesObj.optString("user_id")
                                    if (userId.isEmpty()) userId = dataValuesObj.optString("id")
                                    android.util.Log.d("LOGIN_DEBUG", "  - Extracted from dataValues.u_id: '$userId'")
                                } else {
                                    // Fallback: try direct fields (for backward compatibility)
                                    userId = dataObj.optString("u_id")
                                    if (userId.isEmpty()) userId = dataObj.optString("user_id")
                                    if (userId.isEmpty()) userId = dataObj.optString("id")
                                    android.util.Log.d("LOGIN_DEBUG", "  - Extracted from data.u_id: '$userId'")
                                }
                                
                                // ❌ HARD ERROR if userId is still empty - DO NOT FALLBACK TO MOBILE NUMBER
                                if (userId.isEmpty()) {
                                    android.util.Log.e("LOGIN_DEBUG", "❌ CRITICAL ERROR: Backend did not return u_id!")
                                    android.util.Log.e("LOGIN_DEBUG", "❌ Expected path: data.dataValues.u_id")
                                    android.util.Log.e("LOGIN_DEBUG", "❌ Cannot proceed with login - user ID is required")
                                    throw Exception("Login failed: Backend did not return user ID. Please contact support.")
                                }
                                
                                // Validate userId is a database ID, not a phone number
                                if (userId.length == 10 && userId.all { it.isDigit() }) {
                                    android.util.Log.e("LOGIN_DEBUG", "❌ CRITICAL: userId looks like a phone number: '$userId'")
                                    android.util.Log.e("LOGIN_DEBUG", "❌ This will cause /createcontact to fail")
                                    throw Exception("Login failed: Invalid user ID format. Please contact support.")
                                }
                                
                                android.util.Log.d("LOGIN_DEBUG", "✅ Successfully extracted database user ID: '$userId'")
                                
                                // Extract full_name directly from response for Login flow
                                var fullName = dataObj.optString("full_name")
                                if (fullName.isEmpty()) fullName = dataObj.optString("name")
                                if (fullName.isEmpty()) fullName = dataObj.optString("fullName")
                                
                                // If not found in response, try fallback to local storage (only valid for same-device login)
                                if (fullName.isEmpty()) {
                                    fullName = currentData?.fullName ?: ""
                                }
                                
                                android.util.Log.d("USERNAME_FLOW", "✅ Extracted username: '$fullName'")
                                
                                // Extract email (can be updated from backend)
                                var email = dataObj.optString("email")
                                if (email.isEmpty()) email = dataObj.optString("Email")
                                if (email.isEmpty()) email = currentData?.email ?: ""
                                
                                val updatedUserData = com.womensafety.app.data.UserData(
                                    userId = userId,
                                    fullName = fullName,  // Always from signup, never modified
                                    mobileNumber = mobileNumber,
                                    email = email,
                                    profileImageUri = currentData?.profileImageUri
                                )
                                
                                // Save to UserPreferences
                                userPrefs.saveUserData(updatedUserData)
                                userPrefs.setRegistrationComplete(true)
                                
                                android.util.Log.d("USERNAME_FLOW", "✅ PERSISTED: Username preserved from signup: '$fullName'")
                                
                                // Verify it was saved correctly
                                val verifyData = userPrefs.getUserData()
                                android.util.Log.d("USERNAME_FLOW", "🔍 VERIFY: Re-read from UserPreferences: '${verifyData?.fullName}'")
                            } catch (e: Exception) {
                                android.util.Log.e("USERNAME_FLOW", "❌ Error parsing user data from login response", e)
                            }
                        }
                        
                        // For login flow, we are done
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        onSuccess()
                        return@launch
                    }

                    // STEP 2: Only for registration flow - Complete registration by calling /userregister
                    val userPrefs = com.womensafety.app.data.UserPreferences.getInstance(context)
                    val userData = userPrefs.getUserData()

                    if (userData == null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Registration data not found. Please start again."
                        )
                        return@launch
                    }

                    // Get the temporarily saved password
                    val tempPrefs = context.getSharedPreferences(
                        "temp_registration",
                        android.content.Context.MODE_PRIVATE
                    )
                    val password = tempPrefs.getString("pending_password", null)

                    if (password == null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Password not found. Please start registration again."
                        )
                        return@launch
                    }

                    // Call /userregister to create the account
                    val registrationResult =
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                            val jsonBody = JSONObject().apply {
                                put("full_name", userData.fullName)
                                put("mo_no", userData.mobileNumber)
                                put("email", userData.email)
                                put("password", password)
                            }

                            val requestBody = jsonBody.toString()
                                .toRequestBody("application/json; charset=utf-8".toMediaType())

                            val request = Request.Builder()
                                .url("${baseUrl}userregister")
                                .post(requestBody)
                                .addHeader("Content-Type", "application/json")
                                .build()

                            val response = client.newCall(request).execute()
                            val responseBody = response.body?.string()

                            android.util.Log.d(
                                "REGISTRATION",
                                "User Registration Response: $responseBody"
                            )

                            if (responseBody != null) {
                                val jsonResponse = JSONObject(responseBody)
                                val status = jsonResponse.optInt("status", 0)
                                val description =
                                    jsonResponse.optString("description", "Unknown error")

                                // Extract user ID - check dataValues first (same as login flow)
                                android.util.Log.d("REGISTRATION", "Extracting user ID from registration response...")
                                var userId: String? = null
                                
                                // First check root level
                                if (jsonResponse.has("user_id")) userId = jsonResponse.optString("user_id")
                                else if (jsonResponse.has("id")) userId = jsonResponse.optString("id")
                                else if (jsonResponse.has("u_id")) userId = jsonResponse.optString("u_id")
                                else if (jsonResponse.has("userId")) userId = jsonResponse.optString("userId")

                                // Then check data object
                                if (userId.isNullOrEmpty() && jsonResponse.has("data")) {
                                    val dataObj = jsonResponse.optJSONObject("data")
                                    if (dataObj != null) {
                                        // Check dataValues first (backend may use same structure)
                                        val dataValuesObj = dataObj.optJSONObject("dataValues")
                                        if (dataValuesObj != null) {
                                            userId = dataValuesObj.optString("u_id")
                                            if (userId.isNullOrEmpty()) userId = dataValuesObj.optString("user_id")
                                            if (userId.isNullOrEmpty()) userId = dataValuesObj.optString("id")
                                            if (BuildConfig.DEBUG) {
                                                android.util.Log.d("REGISTRATION", "Extracted from data.dataValues.u_id")
                                            }
                                        } else {
                                            // Fallback to direct data fields
                                            if (dataObj.has("user_id")) userId = dataObj.optString("user_id")
                                            else if (dataObj.has("id")) userId = dataObj.optString("id")
                                            else if (dataObj.has("u_id")) userId = dataObj.optString("u_id")
                                            else if (dataObj.has("userId")) userId = dataObj.optString("userId")
                                            if (BuildConfig.DEBUG) {
                                                android.util.Log.d("REGISTRATION", "Extracted from data.u_id")
                                            }
                                        }
                                    }
                                }
                                
                                if (BuildConfig.DEBUG) {
                                    android.util.Log.d("REGISTRATION", "Final extracted userId")
                                }

                                Triple(status, description, userId)
                            } else {
                                Triple(0, "No response from server", null)
                            }
                        }

                    if (registrationResult.first == 1) {
                        android.util.Log.d("REGISTRATION", "✅ User registered successfully")

                        // ❌ DO NOT FALLBACK TO MOBILE NUMBER
                        if (registrationResult.third.isNullOrEmpty()) {
                            android.util.Log.e("REGISTRATION", "❌ Backend returned success but no user ID!")
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "Registration incomplete: No user ID received. Please try again."
                            )
                            return@launch
                        }

                        // Update user data with real user ID from backend
                        val userId = registrationResult.third
                        if (userId != null && userId.isNotEmpty()) {
                            val updatedUserData = userData.copy(userId = userId)
                            userPrefs.saveUserData(updatedUserData)
                            if (com.womensafety.app.BuildConfig.DEBUG) {
                                android.util.Log.d("REGISTRATION", "Saved user ID")
                            }
                        } else {
                            android.util.Log.e("REGISTRATION", "User ID is null or empty")
                        }

                        // Mark registration as complete
                        userPrefs.setRegistrationComplete(true)

                        // Clear temporary password
                        tempPrefs.edit().remove("pending_password").apply()

                        _uiState.value = _uiState.value.copy(isLoading = false)
                        onSuccess()
                    } else {
                        // Registration failed
                        android.util.Log.e(
                            "REGISTRATION",
                            "❌ Registration failed: ${registrationResult.second}"
                        )
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Registration failed: ${registrationResult.second}"
                        )
                    }
                } else {
                    // OTP verification failed
                    android.util.Log.e(
                        "OTP_VERIFY",
                        "❌ OTP verification failed: ${otpVerifyResult.second}"
                    )
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = otpVerifyResult.second
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("OTP_VERIFY", "❌ Exception during verification", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.message ?: "Please check your connection"}"
                )
            }
        }
    }

    fun resendOTP() {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(isLoading = true, errorMessage = "", successMessage = "")

                try {
                    // Get email from UserPreferences (needed for the API)
                    val userPrefs = com.womensafety.app.data.UserPreferences.getInstance(context)
                    val userData = userPrefs.getUserData()
                    val email = userData?.email ?: ""

                    val otpResult =
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                            val jsonBody = JSONObject().apply {
                                put("mo_no", mobileNumber)
                                put("email", email)
                            }

                            val requestBodyString = jsonBody.toString()

                            com.womensafety.app.utils.Logger.box(
                                "OTP_RESEND",
                                "RESENDING OTP",
                                "URL: ${baseUrl}verifymobilenumber",
                                "Mobile: $mobileNumber",
                                "Email: $email",
                                "Body: $requestBodyString"
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
                                "OTP_RESEND",
                                "RESEND RESPONSE",
                                "Status: ${response.code}",
                                "Body: $responseBody"
                            )

                            if (responseBody != null) {
                                val jsonResponse = JSONObject(responseBody)
                                val status = jsonResponse.optInt("status", 0)
                                val description =
                                    jsonResponse.optString("description", "Unknown error")
                                Pair(status, description)
                            } else {
                                Pair(0, "No response from server")
                            }
                        }

                    if (otpResult.first == 1) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "OTP resent successfully to +91 $mobileNumber"
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Failed to resend OTP: ${otpResult.second}"
                        )
                    }
                } catch (e: Exception) {
                    com.womensafety.app.utils.Logger.e("OTP_RESEND", "Exception during resend", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${e.message}"
                    )
                }
            }
        }
    }

/**
 * Factory for creating NewOTPVerificationViewModel with dependencies
 */
class NewOTPVerificationViewModelFactory(
    private val context: android.content.Context,
    private val mobileNumber: String,
    private val isLoginFlow: Boolean
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewOTPVerificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewOTPVerificationViewModel(context, mobileNumber, isLoginFlow) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}