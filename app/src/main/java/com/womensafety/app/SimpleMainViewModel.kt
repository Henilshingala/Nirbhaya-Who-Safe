package com.womensafety.app

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.womensafety.app.BuildConfig
import com.womensafety.app.data.ContactRepository
import com.womensafety.app.data.SimpleOTPManager
import com.womensafety.app.data.UserPreferences
import com.womensafety.app.data.database.AppDatabase
import com.womensafety.app.data.models.EmergencyContact
import com.womensafety.app.network.NetworkClient
import com.womensafety.app.network.SOSApiService
import com.womensafety.app.logging.ActivityRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.Request
import org.json.JSONObject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

data class SimpleOTPState(
        val isActive: Boolean = false,
        val phoneNumber: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val resendCooldownSeconds: Long = 0
)

data class SimpleSettingsState(
        val sosDelay: Int = 5,
        val enableSound: Boolean = true,
        val enableVibration: Boolean = true,
        val userName: String = "",
        val userPhoneNumber: String = "",
        val userEmail: String = "",
        val iotSimNumber: String = "",
        val manualSosDelay: Int = 10,
        val userRole: String = "", // "sender" or "receiver"
        val miuiSetupCompleted: Boolean = false,
        val profileImageUri: String = ""
)

data class SOSState(
        val isActive: Boolean = false,
        val isInCooldown: Boolean = false,
        val cooldownUntil: Long = 0,
        val errorMessage: String? = null
)

class SimpleMainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = ContactRepository(database.contactDao())
    private val otpManager = SimpleOTPManager(application)
    private val sosManager = SOSManager(application)
    private val sosApiService = SOSApiService()
    private val prefs = application.getSharedPreferences("women_safety_prefs", Context.MODE_PRIVATE)

    private val _contacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val contacts: StateFlow<List<EmergencyContact>> = _contacts.asStateFlow()

    private val _otpState = MutableStateFlow(SimpleOTPState())
    val otpState: StateFlow<SimpleOTPState> = _otpState.asStateFlow()

    private val _settingsState = MutableStateFlow(SimpleSettingsState())
    val settingsState: StateFlow<SimpleSettingsState> = _settingsState.asStateFlow()

    private val _sosState = MutableStateFlow(SOSState())
    val sosState: StateFlow<SOSState> = _sosState.asStateFlow()

    private var pendingContact: PendingContact? = null
    private var cooldownTimerJob: kotlinx.coroutines.Job? = null

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()
    
    companion object {
        private const val SOS_COOLDOWN_SECONDS = 30L
    }
    
    // Cooldown timer job


    init {
        loadContacts()
        loadSettings()
        // Trigger sync from backend on initialization
        syncContactsFromBackend()
        // Start cooldown timer to update UI
        startSOSCooldownTimer()
    }
    
    private fun startSOSCooldownTimer() {
        cooldownTimerJob?.cancel()
        cooldownTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000L) // Update every second
                val currentState = _sosState.value
                val now = System.currentTimeMillis()
                
                if (currentState.isInCooldown && now < currentState.cooldownUntil) {
                    // Still in cooldown, keep state
                    val remainingSeconds = ((currentState.cooldownUntil - now) / 1000).toInt().coerceAtLeast(0)
                    _sosState.value = currentState.copy() // Trigger recomposition
                } else if (currentState.isInCooldown && now >= currentState.cooldownUntil) {
                    // Cooldown expired
                    _sosState.value = SOSState(isActive = false, isInCooldown = false)
                }
            }
        }
    }

    fun syncContactsFromBackend() {
        if (_isSyncing.value) return
        
        viewModelScope.launch(Dispatchers.IO) {
            _isSyncing.value = true
            try {
                val userPrefs = com.womensafety.app.data.UserPreferences.getInstance(getApplication())
                val userId = userPrefs.getUserId()
                
                if (userId.isNullOrEmpty()) {
                    if (BuildConfig.DEBUG) {
                        Log.d("SyncContacts", "User ID not found, skipping sync")
                    }
                    return@launch
                }
                
                // Fetch contacts from backend
                // API: GET https://app.whosafeglobal.com/getallcontacts/{u_id}
                val url = "https://app.whosafeglobal.com/getallcontacts/$userId"
                
                val request = Request.Builder().url(url).get().build()
                val response = NetworkClient.instance.newCall(request).execute()
                val responseBody = response.body?.string()
                
                if (!responseBody.isNullOrEmpty()) {
                    try {
                        val json = JSONObject(responseBody)
                        val status = json.optInt("status", -1)
                        // Support 'data' or 'contacts' array
                        val dataArray = json.optJSONArray("data") ?: json.optJSONArray("contacts")
                        
                        if ((status == 1 || status == 200) && dataArray != null && dataArray.length() > 0) {
                            // Clear existing local contacts to ensure sync with server
                            // NOTE: This assumes server is source of truth.
                            // To be safe, we only do this if we successfully parsed new contacts
                            if (BuildConfig.DEBUG) {
                                Log.d("SyncContacts", "Found ${dataArray.length()} contacts, syncing")
                            }
                            
                            // Collect parsed contacts first
                            val contactsToInsert = mutableListOf<EmergencyContact>()
                            
                            for (i in 0 until dataArray.length()) {
                                val item = dataArray.getJSONObject(i)
                                
                                // flexible parsing
                                val name = item.optString("name")
                                    .ifEmpty { item.optString("contact_name") }
                                    .ifEmpty { "Unknown" }
                                    
                                val phone = item.optString("phone")
                                    .ifEmpty { item.optString("mobile_no") }
                                    .ifEmpty { item.optString("phone_number") }
                                    .ifEmpty { item.optString("number") }
                                    
                                val relation = item.optString("relation")
                                    .ifEmpty { item.optString("relationship") }
                                    .ifEmpty { "" }
                                    
                                // Parse backend ID (c_id) - MUST BE INT
                                val backendId = if (item.has("c_id")) item.optInt("c_id", -1)
                                    else if (item.has("id")) item.optInt("id", -1)
                                    else if (item.has("contact_id")) item.optInt("contact_id", -1)
                                    else -1
                                    
                                if (phone.isNotEmpty() && backendId != -1) {
                                    contactsToInsert.add(
                                        EmergencyContact(
                                            name = name,
                                            phoneNumber = phone,
                                            relationship = relation,
                                            isActive = true,
                                            backendId = backendId // Store as Integer
                                        )
                                    )
                                }
                            }
                            
                            if (contactsToInsert.isNotEmpty()) {
                                // Clear and insert
                                repository.deleteAllContacts()
                                for (contact in contactsToInsert) {
                                    repository.insertContact(contact)
                                }
                                if (BuildConfig.DEBUG) {
                                    Log.d("SyncContacts", "Successfully synced ${contactsToInsert.size} contacts")
                                }
                            }
                        } else {
                            if (BuildConfig.DEBUG) {
                                Log.d("SyncContacts", "No contacts found or status not success")
                            }
                        }
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            Log.e("SyncContacts", "Error parsing contacts JSON", e)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SyncContacts", "Error syncing contacts", e)
            } finally {
                _isSyncing.value = false
            }
        }
    }

    // Validate that phone number is not already registered with a different role
    fun validatePhoneNumberForRole(phoneNumber: String, role: String): Pair<Boolean, String> {
        val cleanNumber = phoneNumber.filter { it.isDigit() }.takeLast(10)

        // Get all registered phone numbers from SharedPreferences
        val registeredSenders = prefs.getStringSet("registered_senders", setOf()) ?: setOf()
        val registeredReceivers = prefs.getStringSet("registered_receivers", setOf()) ?: setOf()

        return when {
            role == "sender" && registeredReceivers.contains(cleanNumber) -> {
                Pair(
                        false,
                        "This phone number is already registered as a receiver. Cannot use same number for sender."
                )
            }
            role == "receiver" && registeredSenders.contains(cleanNumber) -> {
                Pair(
                        false,
                        "This phone number is already registered as a sender. Cannot use same number for receiver."
                )
            }
            else -> Pair(true, "")
        }
    }

    // Register phone number with role
    fun registerPhoneNumberWithRole(phoneNumber: String, role: String) {
        val cleanNumber = phoneNumber.filter { it.isDigit() }.takeLast(10)

        if (role == "sender") {
            val senders =
                    prefs.getStringSet("registered_senders", mutableSetOf())?.toMutableSet()
                            ?: mutableSetOf()
            senders.add(cleanNumber)
            prefs.edit().putStringSet("registered_senders", senders).apply()
        } else if (role == "receiver") {
            val receivers =
                    prefs.getStringSet("registered_receivers", mutableSetOf())?.toMutableSet()
                            ?: mutableSetOf()
            receivers.add(cleanNumber)
            prefs.edit().putStringSet("registered_receivers", receivers).apply()
        }
    }

    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Clear local database contacts
                repository.deleteAllContacts()
                
                // Clear SharedPreferences
                prefs.edit().clear().apply()
                
                // Clear UserPreferences and SecurePreferences are handled in MainActivity context
                
                // Reset state
                _contacts.value = emptyList()
                _settingsState.value = SimpleSettingsState()
                _otpState.value = SimpleOTPState()
                _sosState.value = SOSState()
                
                Log.d("SimpleViewModel", "Local data cleared in ViewModel")
            } catch (e: Exception) {
                Log.e("SimpleViewModel", "Error clearing local data", e)
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val delay = prefs.getInt("sos_delay", 5)
                val sound = prefs.getBoolean("enable_sound", true)
                val vibration = prefs.getBoolean("enable_vibration", true)
                
                // Load from UserPreferences (single source of truth)
                val userPreferences = com.womensafety.app.data.UserPreferences.getInstance(getApplication())
                val userData = userPreferences.getUserData()
                
                // SINGLE SOURCE OF TRUTH: Read username from UserPreferences.fullName ONLY
                val name: String = userData?.fullName ?: ""
                android.util.Log.d("USERNAME_FLOW", "📖 READ: Loading username from UserPreferences: '$name'")
                
                // Phone and Email from UserPreferences
                val phone: String = userData?.mobileNumber ?: ""
                val email: String = userData?.email ?: ""
                
                // CRITICAL: Ensure name variable is current before creating state
                
                val iotSimNumber = com.womensafety.app.data.SecurePreferences.getIotSimNumber(getApplication())
                val manualSosDelay = com.womensafety.app.data.SecurePreferences.getManualSosDelay(getApplication())
                
                var role = com.womensafety.app.data.SecurePreferences.getUserRole(getApplication())
                
                // If role is completely empty (fresh install), default to sender.
                // Otherwise, respect the stored role even if it's 'receiver'.
                if (role.isEmpty()) {
                    role = "sender"
                    com.womensafety.app.data.SecurePreferences.setUserRole(getApplication(), "sender")
                }


                val savedUri = (userData?.profileImageUri ?: "").ifBlank { 
                    prefs.getString("profile_image_uri", "") ?: "" 
                }
                
                // Verify file exists if it's a local file URI
                val verifiedProfileUri = if (savedUri.startsWith("file://")) {
                    val filePath = android.net.Uri.parse(savedUri).path ?: ""
                    if (filePath.isNotEmpty() && java.io.File(filePath).exists()) savedUri else ""
                } else {
                    savedUri
                }

                _settingsState.value =
                        SimpleSettingsState(
                                sosDelay = delay,
                                enableSound = sound,
                                enableVibration = vibration,
                                userName = name,
                                userPhoneNumber = phone,
                                userEmail = email,
                                iotSimNumber = iotSimNumber,
                                manualSosDelay = manualSosDelay,
                                userRole = role,
                                miuiSetupCompleted = prefs.getBoolean("miui_setup_completed", false),
                                profileImageUri = verifiedProfileUri
                        )
            } catch (e: Exception) {
                Log.e("SimpleViewModel", "Error loading settings", e)
            }
        }
    }
    
    /**
     * Public method to refresh settings from storage
     * Call this after registration or when Profile screen is opened
     */
    fun refreshSettings() {
        loadSettings()
        // Also try to sync contacts again if needed
        syncContactsFromBackend()
    }

    fun updateProfileImage(uriString: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val context = getApplication<Application>()
            try {
                val uri = android.net.Uri.parse(uriString)
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = java.io.File(context.filesDir, "profile_picture.jpg")
                val outputStream = java.io.FileOutputStream(file)
                
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                
                // Use the local file path for persistence
                val localUri = android.net.Uri.fromFile(file).toString()
                
                // Save to both SharedPreferences and UserPreferences
                prefs.edit().putString("profile_image_uri", localUri).apply()
                val userPrefs = com.womensafety.app.data.UserPreferences.getInstance(context)
                userPrefs.updateProfileImageUri(localUri)
                
                _settingsState.value = _settingsState.value.copy(profileImageUri = localUri)
                Log.d("SimpleViewModel", "Profile image saved locally: $localUri")
            } catch (e: Exception) {
                Log.e("SimpleViewModel", "Error saving profile image", e)
                // Fallback to original URI if copy fails (though unsafe)
                prefs.edit().putString("profile_image_uri", uriString).apply()
                val userPrefs = com.womensafety.app.data.UserPreferences.getInstance(context)
                userPrefs.updateProfileImageUri(uriString)
                
                _settingsState.value = _settingsState.value.copy(profileImageUri = uriString)
            }
        }
    }

    private var contactsJob: kotlinx.coroutines.Job? = null

    private fun loadContacts() {
        // Cancel previous job to prevent leaks
        contactsJob?.cancel()
        contactsJob = viewModelScope.launch {
            try {
                repository.getAllActiveContacts().collect { contactList ->
                    _contacts.value = contactList
                }
            } catch (e: Exception) {
                android.util.Log.e("SimpleMainViewModel", "Error loading contacts", e)
            }
        }
    }



    /**
     * Create contact directly via backend API
     * Replaces OTP verification flow
     */
    fun createContactDirectly(
        name: String, 
        phone: String, 
        relationship: String, 
        email: String,
        location: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Determine user ID
                val userPrefs = com.womensafety.app.data.UserPreferences.getInstance(getApplication())
                val userId = userPrefs.getUserId() ?: ""
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("AddContactDebug", "Attempting create contact")
                }
                
                if (userId.isEmpty()) {
                    if (BuildConfig.DEBUG) {
                        android.util.Log.e("AddContactDebug", "User ID is empty")
                    }
                    onError("User not logged in or User ID missing")
                    return@launch
                }
                
                // Validate userId is not a phone number (common bug from login flow)
                if (userId.length == 10 && userId.all { it.isDigit() }) {
                    if (BuildConfig.DEBUG) {
                        android.util.Log.e("AddContactDebug", "User ID appears to be a phone number")
                    }
                    onError("Invalid user session detected. Please log out and log in again.")
                    return@launch
                }
                
                // Switch to IO for network
                val success = withContext(Dispatchers.IO) {
                    try {
                        val client = NetworkClient.instance
                        
                        val requestBodyBuilder = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("u_id", userId)
                            .addFormDataPart("name", name)
                            .addFormDataPart("phone", phone)
                            .addFormDataPart("email", email)
                            .addFormDataPart("location", location)
                            .addFormDataPart("relation", relationship)
                            
                        val request = Request.Builder()
                            .url("https://app.whosafeglobal.com/createcontact")
                            .post(requestBodyBuilder.build())
                            .build()

                        val response = client.newCall(request).execute()
                        val responseBody = response.body?.string()
                        
                        if (responseBody != null) {
                            val json = JSONObject(responseBody)
                            val status = json.optInt("status")
                            val description = json.optString("description", "Unknown error")
                            
                            if (status == 1) {
                                // Extract the backend ID (c_id) from response
                                // Response formats can vary:
                                // 1. data: { c_id: 123, ... }
                                // 2. contact: { c_id: 123, ... }
                                // 3. c_id: 123
                                
                                var backendId: Int? = null
                                val dataObj = json.optJSONObject("data") ?: json.optJSONObject("contact")
                                
                                if (dataObj != null) {
                                    val id = if (dataObj.has("c_id")) dataObj.optInt("c_id", -1)
                                             else if (dataObj.has("id")) dataObj.optInt("id", -1)
                                             else dataObj.optInt("contact_id", -1)
                                    if (id != -1) backendId = id
                                } else {
                                    // Try getting from root
                                    val id = if (json.has("c_id")) json.optInt("c_id", -1)
                                             else json.optInt("contact_id", -1)
                                    if (id != -1) backendId = id
                                }
                                
                                if (BuildConfig.DEBUG) {
                                    Log.d("AddContactDebug", "Extracted backend ID for new contact")
                                }
                                
                                // Successful - Add to local DB now with backendId
                                val newContact = EmergencyContact(
                                    name = name,
                                    phoneNumber = phone,
                                    relationship = relationship,
                                    isActive = true,
                                    backendId = backendId // SAVE THE ID!
                                )
                                repository.insertContact(newContact)
                                true // Return success
                            } else {
                                throw Exception(description)
                            }
                        } else {
                            throw Exception("Empty response from server")
                        }
                    } catch (e: Exception) {
                        throw e
                    }
                }
                
                if (success) {
                    // CRITICAL: Immediately sync from backend to get the latest contacts
                    // This ensures the new contact appears in Create Group screen without restart
                    if (BuildConfig.DEBUG) {
                        Log.d("AddContactDebug", "Contact created successfully, triggering sync")
                    }
                    syncContactsFromBackend()
                    onSuccess()
                }
                
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Failed to add contact"
                if (errorMsg.contains("already exist", ignoreCase = true)) {
                    if (BuildConfig.DEBUG) {
                        Log.d("AddContactDebug", "Contact exists on server, triggering sync")
                    }
                    syncContactsFromBackend()
                }
                onError(errorMsg)
            }
        }
    }

    fun startOTPVerification(name: String, phoneNumber: String, relationship: String) {
        val cleanNumber = phoneNumber.filter { it.isDigit() }.takeLast(10)
        val isDuplicate = _contacts.value.any { it.phoneNumber.filter { char -> char.isDigit() }.takeLast(10) == cleanNumber }

        if (isDuplicate) {
            _otpState.value = SimpleOTPState(
                isActive = true, // Keep it active so the error shows
                phoneNumber = phoneNumber,
                errorMessage = "This number is already registered in your emergency contacts."
            )
            return
        }

        pendingContact = PendingContact(name, phoneNumber, relationship)

        _otpState.value =
                SimpleOTPState(isActive = true, phoneNumber = phoneNumber, isLoading = true)

        viewModelScope.launch {
            val otp = otpManager.generateAndSendOTP(phoneNumber)

            if (otp.isNotEmpty()) {
                _otpState.value = _otpState.value.copy(isLoading = false, errorMessage = null)
                if (BuildConfig.DEBUG) {
                    Log.d("SimpleViewModel", "OTP sent successfully")
                }
                // Start cooldown timer
                startCooldownTimer()
            } else {
                _otpState.value =
                        _otpState.value.copy(
                                isLoading = false,
                                errorMessage =
                                        "Failed to send OTP. Please check your number and try again."
                        )
            }
        }
    }

    fun verifyOTP(enteredOTP: String) {
        val currentState = _otpState.value

        _otpState.value = currentState.copy(isLoading = true)

        viewModelScope.launch {
            if (otpManager.verifyOTP(enteredOTP, currentState.phoneNumber)) {
                if (BuildConfig.DEBUG) {
                    Log.d("SimpleViewModel", "OTP verified successfully")
                }

                // Add the contact
                pendingContact?.let { contact ->
                    addContact(contact.name, contact.phoneNumber, contact.relationship)
                }

                cancelOTPVerification()
            } else {
                if (BuildConfig.DEBUG) {
                    Log.d("SimpleViewModel", "OTP verification failed")
                }
                _otpState.value =
                        currentState.copy(
                                isLoading = false,
                                errorMessage = "Invalid OTP. Please try again."
                        )
            }
        }
    }

    fun resendOTP() {
        val phoneNumber = _otpState.value.phoneNumber
        val name = pendingContact?.name ?: ""
        val relationship = pendingContact?.relationship ?: ""

        if (BuildConfig.DEBUG) {
            Log.d("SimpleViewModel", "Resending OTP")
        }
        startOTPVerification(name, phoneNumber, relationship)
    }
    
    private fun startCooldownTimer() {
        // Cancel existing timer
        cooldownTimerJob?.cancel()
        
        cooldownTimerJob = viewModelScope.launch {
            while (_otpState.value.isActive) {
                val remaining = otpManager.getRemainingCooldownSeconds()
                _otpState.value = _otpState.value.copy(resendCooldownSeconds = remaining)
                
                if (remaining <= 0) {
                    break
                }
                
                kotlinx.coroutines.delay(1000) // Update every second
            }
        }
    }

    fun cancelOTPVerification() {
        cooldownTimerJob?.cancel()
        _otpState.value = SimpleOTPState()
        pendingContact = null
    }

    private suspend fun addContact(name: String, phone: String, relationship: String) {
        val contact =
                EmergencyContact(name = name, phoneNumber = phone, relationship = relationship)
        repository.insertContact(contact)
    }

    fun updateContact(contact: EmergencyContact) {
        viewModelScope.launch { repository.updateContact(contact) }
    }

    /**
     * Delete contact globally - from backend and local database
     * This will remove the contact from all groups they belong to
     */
    fun globalDeleteContact(contact: EmergencyContact) {
        val backendId = contact.backendId
        if (backendId == null) {
            // If no backend ID, just delete locally
            viewModelScope.launch { repository.globalDeleteContact(contact) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (BuildConfig.DEBUG) {
                    Log.d("GlobalDelete", "Attempting GLOBAL deletion for contact")
                }
                
                val url = "https://app.whosafeglobal.com/delete/$backendId"
                val request = Request.Builder()
                    .url(url)
                    .delete()
                    .build()
                
                val response = NetworkClient.instance.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                
                val json = JSONObject(responseBody)
                val status = json.optInt("status", -1)
                
                if (status == 1) {
                    if (BuildConfig.DEBUG) {
                        Log.d("GlobalDelete", "Backend deletion successful")
                    }
                    repository.globalDeleteContact(contact)
                    // CRITICAL: Force refresh from backend to ensure consistent state (e.g. groups updated)
                    syncContactsFromBackend()
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.e("GlobalDelete", "Backend deletion failed")
                    }
                }
            } catch (e: Exception) {
                Log.e("GlobalDelete", "Error during global deletion", e)
                // Fallback: Still try to delete locally if it's a persistent error, 
                // but user says backend is correct so we assume success if properly called.
            }
        }
    }

    fun triggerSOS(): Boolean {
        // Check if SOS is already active or in cooldown
        val currentSOSState = _sosState.value
        val now = System.currentTimeMillis()
        
        if (currentSOSState.isActive) {
            if (BuildConfig.DEBUG) {
                Log.d("SimpleViewModel", "SOS already active, ignoring")
            }
            return false
        }
        
        if (currentSOSState.isInCooldown && now < currentSOSState.cooldownUntil) {
            val remainingSeconds = ((currentSOSState.cooldownUntil - now) / 1000).toInt()
            _sosState.value = currentSOSState.copy(
                errorMessage = "Please wait $remainingSeconds seconds before triggering SOS again"
            )
            if (BuildConfig.DEBUG) {
                Log.d("SimpleViewModel", "SOS in cooldown, remaining: $remainingSeconds seconds")
            }
            return false
        }
        
        // Set SOS as active immediately
        _sosState.value = SOSState(isActive = true)
        
        // Record Activity
        ActivityRecorder.record(
            activityName = "Trigger Hit (Manual Button)", 
            detail = "User clicked SOS button",
            context = getApplication()
        )
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val settings = _settingsState.value
                val userPrefs = UserPreferences.getInstance(getApplication())
                val userId = userPrefs.getUserId()
                
                if (userId.isNullOrEmpty()) {
                    _sosState.value = SOSState(
                        isActive = false,
                        errorMessage = "User not logged in. Please log in and try again."
                    )
                    Log.e("SimpleViewModel", "User ID not found, cannot send SOS")
                    return@launch
                }
                
                if (settings.manualSosDelay > 0) {
                    kotlinx.coroutines.delay(settings.manualSosDelay * 1000L)
                }

                // Fetch location for API message only (no Maps, no SMS)
                var locationLink: String? = null
                try {
                    val locationHelper = LocationHelper(getApplication())
                    locationLink = kotlinx.coroutines.withTimeout(10000L) {
                        locationHelper.getGoogleMapsLink()
                    }
                    if (locationLink.isNullOrEmpty()) {
                        locationLink = null
                    }
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        Log.e("SimpleViewModel", "Location fetch failed", e)
                    }
                    locationLink = null
                }

                // Trigger SOS via API (sequential flow: sendemergencymessages -> groups)
                val success = sosApiService.triggerSOS(userId, locationLink)
                
                if (BuildConfig.DEBUG) {
                    Log.d("SimpleViewModel", "SOS API flow completed: $success")
                }
                
                // Set cooldown and reset state
                val cooldownUntil = now + (SOS_COOLDOWN_SECONDS * 1000)
                _sosState.value = SOSState(
                    isActive = false,
                    isInCooldown = true,
                    cooldownUntil = cooldownUntil
                )
                
                // Clear cooldown after timeout
                launch {
                    kotlinx.coroutines.delay(SOS_COOLDOWN_SECONDS * 1000)
                    _sosState.value = SOSState(isActive = false, isInCooldown = false)
                }
                
            } catch (e: Exception) {
                Log.e("SimpleViewModel", "Error triggering SOS", e)
                _sosState.value = SOSState(
                    isActive = false,
                    errorMessage = "Failed to send SOS. Please try again."
                )
            }
        }
        return true
    }

    fun shareLocation(): Boolean {
        val currentContacts = _contacts.value
        return if (currentContacts.isNotEmpty()) {
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val settings = _settingsState.value
                    
                    // Fetch live location link
                    Log.d("SimpleViewModel", "Starting location fetch for sharing...")
                    val locationHelper = LocationHelper(getApplication())
                    val locationLink = try {
                        kotlinx.coroutines.withTimeout(10000L) {
                            locationHelper.getGoogleMapsLink()
                        }
                    } catch (e: Exception) {
                        Log.e("SimpleViewModel", "Failed to fetch location for sharing", e)
                        ""
                    }

                    if (locationLink.isNotEmpty()) {
                        if (BuildConfig.DEBUG) {
                            Log.d("SimpleViewModel", "Sending location share")
                        }
                        val success = sosManager.sendLocationShare(
                            contacts = currentContacts,
                            userLocation = locationLink,
                            senderPhoneNumber = settings.userPhoneNumber
                        )
                        Log.d("SimpleViewModel", "Location share success: $success")
                    } else {
                        Log.w("SimpleViewModel", "Cannot share: Location unavailable")
                    }
                } catch (e: Exception) {
                    Log.e("SimpleViewModel", "Error sharing location", e)
                }
            }
            true
        } else {
            false
        }
    }

    // Sender device must remain silent; keep this as a no-op for clarity
    private fun startLocalEmergencyAlert() {}

    // Settings functions
    fun updateSosDelay(delay: Int) {
        prefs.edit().putInt("sos_delay", delay).apply()
        _settingsState.value = _settingsState.value.copy(sosDelay = delay)
        Log.d("SimpleViewModel", "SOS delay updated to: $delay seconds")
    }

    fun updateSoundSetting(enabled: Boolean) {
        prefs.edit().putBoolean("enable_sound", enabled).apply()
        _settingsState.value = _settingsState.value.copy(enableSound = enabled)
        Log.d("SimpleViewModel", "Sound setting updated: $enabled")
    }

    fun updateVibrationSetting(enabled: Boolean) {
        prefs.edit().putBoolean("enable_vibration", enabled).apply()
        _settingsState.value = _settingsState.value.copy(enableVibration = enabled)
        Log.d("SimpleViewModel", "Vibration setting updated: $enabled")
    }

    fun updateUserName(name: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            com.womensafety.app.data.SecurePreferences.setUserName(getApplication(), name)
            // Also update UserPreferences if registration is complete
            val userPreferences = com.womensafety.app.data.UserPreferences.getInstance(getApplication())
            if (userPreferences.isRegistrationComplete()) {
                userPreferences.updateFullName(name)
            }
            // Update state on main thread for immediate UI update
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                _settingsState.value = _settingsState.value.copy(userName = name)
            }
            Log.d("SimpleViewModel", "User name updated (encrypted)")
        }
    }

    fun updateUserPhoneNumber(number: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            com.womensafety.app.data.SecurePreferences.setUserPhoneNumber(getApplication(), number)
            // Also update UserPreferences if registration is complete
            val userPreferences = com.womensafety.app.data.UserPreferences.getInstance(getApplication())
            if (userPreferences.isRegistrationComplete()) {
                userPreferences.updateMobileNumber(number)
            }
            // Update state on main thread for immediate UI update
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                _settingsState.value = _settingsState.value.copy(userPhoneNumber = number)
            }
            Log.d("SimpleViewModel", "User phone number updated (encrypted)")
        }
    }

    fun updateUserEmail(email: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            // Update UserPreferences if registration is complete
            val userPreferences = com.womensafety.app.data.UserPreferences.getInstance(getApplication())
            if (userPreferences.isRegistrationComplete()) {
                userPreferences.updateEmail(email)
            }
            // Update state on main thread for immediate UI update
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                _settingsState.value = _settingsState.value.copy(userEmail = email)
            }
            Log.d("SimpleViewModel", "User email updated")
        }
    }

    fun updateIotSimNumber(number: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            com.womensafety.app.data.SecurePreferences.setIotSimNumber(getApplication(), number)
            // Update state on main thread for immediate UI update
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                _settingsState.value = _settingsState.value.copy(iotSimNumber = number)
            }
            Log.d("SimpleViewModel", "IoT SIM number updated (encrypted): '$number'")
        }
    }

    fun updateManualSosDelay(delay: Int) {
        com.womensafety.app.data.SecurePreferences.setManualSosDelay(getApplication(), delay)
        _settingsState.value = _settingsState.value.copy(manualSosDelay = delay)
        Log.d("SimpleViewModel", "Manual SOS delay updated to: $delay seconds (encrypted)")
    }

    fun setUserRole(role: String) {
        com.womensafety.app.data.SecurePreferences.setUserRole(getApplication(), role)
        _settingsState.value = _settingsState.value.copy(userRole = role)
        Log.d("SimpleViewModel", "User role set to: $role (encrypted)")
    }

    fun isOnboardingComplete(): Boolean {
        val role = _settingsState.value.userRole
        val phone = _settingsState.value.userPhoneNumber

        if (role.isEmpty()) return false
        if (phone.isEmpty()) return false

        return if (role == "sender") {
            _settingsState.value.iotSimNumber.isNotEmpty()
        } else {
            true
        }
    }

    fun markMiuiSetupCompleted() {
        prefs.edit().putBoolean("miui_setup_completed", true).apply()
        _settingsState.value = _settingsState.value.copy(miuiSetupCompleted = true)
        Log.d("SimpleViewModel", "MIUI setup marked as completed")
    }
    
    /**
     * CLEAR ALL APP DATA
     * 
     * This function deletes EVERYTHING:
     * - All user registration data
     * - All emergency contacts
     * - All settings
     * - All preferences
     * - Profile images
     * 
     * After calling this, the app will be like a fresh install
     */
    fun clearAllAppData() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                Log.d("SimpleViewModel", "🗑️ Starting complete data deletion...")
                
                // 1. Clear UserPreferences (registration data)
                val userPreferences = com.womensafety.app.data.UserPreferences.getInstance(getApplication())
                userPreferences.clearAllData()
                Log.d("SimpleViewModel", "✅ Cleared registration data")
                
                // 2. Clear all emergency contacts from database
                repository.deleteAllContacts()
                Log.d("SimpleViewModel", "✅ Deleted all emergency contacts")
                
                // 3. Clear SecurePreferences
                com.womensafety.app.data.SecurePreferences.clearAll(getApplication())
                Log.d("SimpleViewModel", "✅ Cleared secure preferences")
                
                // 4. Clear regular SharedPreferences
                prefs.edit().clear().apply()
                Log.d("SimpleViewModel", "✅ Cleared app preferences")
                
                // 5. Delete profile image file
                try {
                    val profileImageFile = java.io.File(getApplication<android.app.Application>().filesDir, "profile_picture.jpg")
                    if (profileImageFile.exists()) {
                        profileImageFile.delete()
                        Log.d("SimpleViewModel", "✅ Deleted profile image")
                    }
                } catch (e: Exception) {
                    Log.e("SimpleViewModel", "Error deleting profile image", e)
                }
                
                // 6. Reset state to default
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    _settingsState.value = SimpleSettingsState()
                    _contacts.value = emptyList()
                    _otpState.value = SimpleOTPState()
                }
                
                Log.d("SimpleViewModel", "🎉 ALL DATA CLEARED SUCCESSFULLY!")
                Log.d("SimpleViewModel", "App is now in fresh install state")
                
            } catch (e: Exception) {
                Log.e("SimpleViewModel", "❌ Error clearing app data", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Properly cancel all coroutines to prevent memory leaks
        contactsJob?.cancel()
    }

    data class PendingContact(val name: String, val phoneNumber: String, val relationship: String)
}

class SimpleMainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SimpleMainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return SimpleMainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
