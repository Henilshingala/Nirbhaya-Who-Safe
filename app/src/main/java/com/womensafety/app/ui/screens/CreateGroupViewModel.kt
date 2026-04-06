package com.womensafety.app.ui.screens

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.womensafety.app.data.UserPreferences
import com.womensafety.app.data.database.AppDatabase
import com.womensafety.app.data.models.EmergencyContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import com.womensafety.app.network.NetworkClient

/**
 * CREATE GROUP VIEWMODEL
 * 
 * Handles group creation logic:
 * - Loads all guardian contacts from local DB
 * - Manages contact selection state
 * - Calls /creategroup API with selected contact IDs
 * - Uses database user ID (NOT mobile number)
 */

data class CreateGroupUiState(
    val contacts: List<EmergencyContact> = emptyList(),
    val selectedContactIds: Set<Int> = emptySet(), // Now storing Backend IDs (c_id) as Int
    val groupName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)

class CreateGroupViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val contactDao = database.contactDao()
    private val userPrefs = UserPreferences.getInstance(application)
    
    private val _uiState = MutableStateFlow(CreateGroupUiState())
    val uiState: StateFlow<CreateGroupUiState> = _uiState.asStateFlow()
    
    private val client = NetworkClient.instance
    
    init {
        loadContacts()
    }
    
    /**
     * Load all active guardian contacts from local database
     */
    private fun loadContacts() {
        viewModelScope.launch {
            try {
                // Observe Flow to automatically get updates when sync finishes
                contactDao.getAllActiveContacts().collect { contacts ->
                    // Filter out contacts without backend IDs to be safe
                    val validContacts = contacts.filter { it.backendId != null }
                    _uiState.value = _uiState.value.copy(contacts = validContacts)
                    Log.d("CreateGroup", "Loaded ${validContacts.size} valid contacts (with backend IDs)")
                    
                    // Log each contact's backend ID for debugging
                    validContacts.forEach { contact ->
                        Log.d("CreateGroup", "  - ${contact.name}: backendId=${contact.backendId}")
                    }
                }
            } catch (e: Exception) {
                Log.e("CreateGroup", "Error observing contacts", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load contacts: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Manually refresh contacts from database
     * Call this when returning to the screen or after contact creation
     */
    fun refreshContacts() {
        Log.d("CreateGroup", "Manual refresh triggered")
        // The Flow observation in loadContacts() will automatically update
        // when the database changes, but we can force a re-collection
        viewModelScope.launch {
            try {
                val contacts = contactDao.getAllActiveContacts().collect { contacts ->
                    val validContacts = contacts.filter { it.backendId != null }
                    _uiState.value = _uiState.value.copy(contacts = validContacts)
                    Log.d("CreateGroup", "Refreshed: ${validContacts.size} valid contacts")
                }
            } catch (e: Exception) {
                Log.e("CreateGroup", "Error refreshing contacts", e)
            }
        }
    }
    
    /**
     * Toggle contact selection
     * @param info Backend ID of the contact
     */
    fun toggleContactSelection(backendId: Int?) {
        if (backendId == null) return

        val currentSelected = _uiState.value.selectedContactIds.toMutableSet()
        if (currentSelected.contains(backendId)) {
            currentSelected.remove(backendId)
        } else {
            currentSelected.add(backendId)
        }
        _uiState.value = _uiState.value.copy(
            selectedContactIds = currentSelected,
            errorMessage = "" // Clear error when user interacts
        )
        Log.d("CreateGroup", "Selected backend IDs: $currentSelected")
    }
    
    /**
     * Update group name
     */
    fun updateGroupName(name: String) {
        _uiState.value = _uiState.value.copy(
            groupName = name,
            errorMessage = "" // Clear error when user types
        )
    }
    
    /**
     * Create group via backend API
     * 
     * API: POST https://app.whosafeglobal.com/creategroup
     * Payload:
     * {
     *   "name": "<group name>",
     *   "contactIds": [<c_id>, <c_id>],
     *   "u_id": "<database user id>"
     * }
     */
    fun createGroup(onSuccess: (groupName: String, members: List<EmergencyContact>) -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value
            
            // Validation
            if (currentState.groupName.isBlank()) {
                _uiState.value = currentState.copy(
                    errorMessage = "Please enter a group name"
                )
                return@launch
            }
            
            if (currentState.selectedContactIds.isEmpty()) {
                _uiState.value = currentState.copy(
                    errorMessage = "Please select at least one contact"
                )
                return@launch
            }
            
            // Get user ID from UserPreferences
            val userId = userPrefs.getUserId()
            if (userId.isNullOrEmpty()) {
                _uiState.value = currentState.copy(
                    errorMessage = "User not logged in. Please log in again."
                )
                return@launch
            }
            
            // Validate userId is not a phone number
            if (userId.length == 10 && userId.all { it.isDigit() }) {
                Log.e("CreateGroup", "❌ User ID appears to be a phone number: '$userId'")
                _uiState.value = currentState.copy(
                    errorMessage = "Invalid user session. Please log out and log in again."
                )
                return@launch
            }
            
            Log.d("CreateGroup", "Creating group with:")
            Log.d("CreateGroup", "  - Group name: '${currentState.groupName}'")
            Log.d("CreateGroup", "  - User ID: '$userId'")
            Log.d("CreateGroup", "  - Selected contact backend IDs: ${currentState.selectedContactIds}")
            
            // Log detailed contact information for debugging
            val selectedContacts = currentState.contacts.filter { 
                currentState.selectedContactIds.contains(it.backendId) 
            }
            selectedContacts.forEach { contact ->
                Log.d("CreateGroup", "  - Contact: ${contact.name}, Phone: ${contact.phoneNumber}, Backend ID: ${contact.backendId}")
            }
            
            _uiState.value = currentState.copy(isLoading = true, errorMessage = "")
            
            try {
                val result = withContext(Dispatchers.IO) {
                    // Build JSON payload
                    val jsonBody = JSONObject().apply {
                        put("name", currentState.groupName)
                        put("u_id", userId)
                        
                         // Add contactIds as array of INTEGERS (numeric)
                         val contactIdsArray = JSONArray()
                         currentState.selectedContactIds.forEach { numericId ->
                              contactIdsArray.put(numericId)
                         }
                         put("contactIds", contactIdsArray)
                     }
                    
                    val requestBody = jsonBody.toString()
                        .toRequestBody("application/json; charset=utf-8".toMediaType())
                    
                    Log.d("CreateGroup", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    Log.d("CreateGroup", "📤 SENDING REQUEST TO BACKEND")
                    Log.d("CreateGroup", "URL: https://app.whosafeglobal.com/creategroup")
                    Log.d("CreateGroup", "Payload: $jsonBody")
                    Log.d("CreateGroup", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    
                    val request = Request.Builder()
                        .url("https://app.whosafeglobal.com/creategroup")
                        .post(requestBody)
                        .addHeader("Content-Type", "application/json")
                        .build()
                    
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()
                    
                    Log.d("CreateGroup", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    Log.d("CreateGroup", "📥 BACKEND RESPONSE")
                    Log.d("CreateGroup", "Response code: ${response.code}")
                    Log.d("CreateGroup", "Response body: $responseBody")
                    Log.d("CreateGroup", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    
                    if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val status = jsonResponse.optInt("status", 0)
                        val description = jsonResponse.optString("description", "Unknown error")
                        
                        Pair(status, description)
                    } else {
                        Pair(0, "No response from server")
                    }
                }
                
                if (result.first == 1) {
                    Log.d("CreateGroup", "✅ Group created successfully")
                    
                    // CRITICAL: Auto-refresh handled by MainActivity calling GroupListViewModel
                    Log.d("CreateGroup", "✅ Group created. Triggering UI auto-refresh...")
                    
                    // Get selected members for UI feedback
                    val selectedMembers = currentState.contacts.filter { contact ->
                        currentState.selectedContactIds.contains(contact.backendId)
                    }
                    
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        successMessage = "Group created successfully!"
                    )
                    
                    // Pass group name and members to callback
                    onSuccess(currentState.groupName, selectedMembers)
                } else {
                    Log.e("CreateGroup", "❌ Group creation failed: ${result.second}")
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = result.second
                    )
                }
                
            } catch (e: Exception) {
                Log.e("CreateGroup", "❌ Exception during group creation", e)
                _uiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.message ?: "Please check your connection"}"
                )
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }
}
