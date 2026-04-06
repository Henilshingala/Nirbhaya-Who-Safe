package com.womensafety.app.ui.screens

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.womensafety.app.data.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.json.JSONObject
import com.womensafety.app.network.NetworkClient

/**
 * GROUP LIST VIEWMODEL
 * 
 * Handles fetching and listing created groups
 */

data class GroupItem(
    val id: String,
    val name: String,
    val memberCount: Int,
    val memberIds: List<String> = emptyList(),
)

data class GroupListUiState(
    val groups: List<GroupItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

class GroupListViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userPrefs = UserPreferences.getInstance(application)
    
    private val _uiState = MutableStateFlow(GroupListUiState())
    val uiState: StateFlow<GroupListUiState> = _uiState.asStateFlow()
    
    private val client = NetworkClient.instance
        
    init {
        fetchGroups()
    }
    
    fun fetchGroups() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            
            val userId = userPrefs.getUserId() ?: ""
            if (userId.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "User not logged in"
                )
                return@launch
            }
            
            try {
                // Fetch groups from backend
                // API: GET https://app.whosafeglobal.com/getallgroups/{u_id}
                
                val groups = withContext(Dispatchers.IO) {
                    // Endpoint is path-based: /getallgroups/{u_id}
                    val url = "https://app.whosafeglobal.com/getallgroups/$userId"
                    Log.d("GroupList", "Fetching groups from: $url")
                        
                    val request = Request.Builder()
                        .url(url)
                        .get()
                        .build()
                        
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()
                    
                    Log.d("GroupList", "Response: $responseBody")
                    
                    val parsedGroups = mutableListOf<GroupItem>()
                    
                    if (responseBody != null) {
                        try {
                            if (responseBody.trim().startsWith("<")) {
                                Log.e("GroupList", "Received HTML instead of JSON. Endpoint might still be wrong or Auth required.")
                                throw Exception("Invalid server response (HTML)")
                            }
                            
                            val json = JSONObject(responseBody)
                            val status = json.optInt("status", -1)
                            
                            // Accept status 1 or if data exists
                            val dataArray = json.optJSONArray("data") ?: json.optJSONArray("groups")
                            
                            if (status == 1 || dataArray != null) {
                                if (dataArray != null) {
                                    for (i in 0 until dataArray.length()) {
                                        val item = dataArray.getJSONObject(i)
                                        // PARSING STRATEGY: Try multiple common field names
                                        // Group ID: g_id, group_id, id, groupId
                                        val id = item.optString("g_id").ifEmpty {
                                            item.optString("group_id").ifEmpty { 
                                                item.optString("id").ifEmpty { item.optString("groupId") } 
                                            }
                                        }
                                        
                                        // Group Name: name, group_name, groupName
                                        val name = item.optString("group_name").ifEmpty { 
                                            item.optString("name").ifEmpty { item.optString("groupName", "Unnamed Group") } 
                                        }
                                        
                                        // Members: members, contactIds, contacts
                                        val membersList = mutableListOf<String>()
                                        val membersArray = item.optJSONArray("members") 
                                            ?: item.optJSONArray("contactIds")
                                            ?: item.optJSONArray("contacts")
                                            
                                        if (membersArray != null) {
                                            for (j in 0 until membersArray.length()) {
                                                // Handle array of strings/ints or array of objects
                                                val memberObj = membersArray.optJSONObject(j)
                                                if (memberObj != null) {
                                                    // Use member id from object
                                                    val mId = memberObj.optString("c_id").ifEmpty { 
                                                        memberObj.optString("id").ifEmpty { memberObj.optString("contact_id") } 
                                                    }
                                                    if (mId.isNotEmpty()) membersList.add(mId)
                                                } else {
                                                    // Plain ID
                                                    membersList.add(membersArray.getString(j))
                                                }
                                            }
                                        }
                                        
                                        if (id.isNotEmpty()) {
                                            parsedGroups.add(GroupItem(id, name, membersList.size, membersList))
                                        }
                                    }
                                }
                            } else {
                                Log.w(
                                    "GroupList",
                                    "Status: $status. Description: ${json.optString("description")}"
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("GroupList", "Failed to parse groups JSON: ${e.message}")
                            throw e
                        }
                    }
                    parsedGroups
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    groups = groups
                )
                
            } catch (e: Exception) {
                Log.e("GroupList", "Error fetching groups", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load groups: ${e.message}"
                )
            }
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            try {
                // Determine user ID
                val userId = userPrefs.getUserId() ?: ""
                
                // API: DELETE GROUP
                // MUST USE DELETE
                val isSuccess = withContext(Dispatchers.IO) {
                    val url = "https://app.whosafeglobal.com/deletegroup/$groupId"
                    Log.d("GroupList", "Deleting group (DELETE): $url")
                    
                    val request = Request.Builder()
                        .url(url)
                        .delete()
                        .build()
                        
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string() ?: ""
                    Log.d("GroupList", "Delete response: $responseBody")
                    
                    try {
                        val json = JSONObject(responseBody)
                        val status = json.optInt("status", -1)
                        status == 1
                    } catch (e: Exception) {
                        Log.e("GroupList", "Failed to parse delete response", e)
                        false
                    }
                }

                if (isSuccess) {
                    Log.d("GroupList", "✅ Group deleted successfully. Auto-refreshing from backend...")
                    // CRITICAL: Immediately refresh from backend to ensure UI is in sync
                    // This prevents stale state and ensures user sees changes without app restart
                    fetchGroups()
                } else {
                    _uiState.value = _uiState.value.copy(errorMessage = "Failed to delete group. Please try again.")
                }
                
            } catch (e: Exception) {
                Log.e("GroupList", "Error deleting group", e)
                _uiState.value = _uiState.value.copy(errorMessage = "Failed to delete group: ${e.message}")
            }
        }
    }

    fun globalDeleteContact(backendId: Int?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (backendId == null) {
            onError("Invalid contact ID")
            return
        }
        
        viewModelScope.launch {
            try {
                // API: GLOBAL DELETE CONTACT path-based: /delete/{c_id}
                val isSuccess = withContext(Dispatchers.IO) {
                    val url = "https://app.whosafeglobal.com/delete/$backendId"
                    Log.d("GlobalDelete", "Executing GLOBAL contact deletion (DELETE): $url")
                    
                    val request = Request.Builder()
                        .url(url)
                        .delete()
                        .build()
                        
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string() ?: ""
                    Log.d("GlobalDelete", "Global delete response: $responseBody")
                    
                    try {
                        val json = JSONObject(responseBody)
                        val status = json.optInt("status", -1)
                        status == 1
                    } catch (e: Exception) {
                        Log.e("GlobalDelete", "Failed to parse global delete response", e)
                        false
                    }
                }

                if (isSuccess) {
                    // Update global list state
                    fetchGroups()
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError("Failed to delete contact globally")
                    }
                }
            } catch (e: Exception) {
                Log.e("GlobalDelete", "Error during global contact deletion", e)
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Failed to delete contact globally")
                }
            }
        }
    }
}
