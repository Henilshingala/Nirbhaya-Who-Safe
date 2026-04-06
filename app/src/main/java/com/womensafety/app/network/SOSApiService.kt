package com.womensafety.app.network

import com.womensafety.app.BuildConfig
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.womensafety.app.logging.ActivityRecorder

/**
 * SOS API Service
 * 
 * Handles API-based SOS messaging:
 * 1. Send emergency messages to all personal contacts (MANDATORY FIRST)
 * 2. Fetch all user groups
 * 3. Send emergency messages to each group (MANDATORY FOR ALL GROUPS)
 * 
 * Flow is sequential: sendemergencymessages -> getallgroups -> sendgroupmessage (for each)
 */
class SOSApiService {
    
    private val client = NetworkClient.instance
    private val baseUrl = "https://app.whosafeglobal.com"
    private val TAG = "SOSApiService"
    private val DEFAULT_MESSAGE = "Emergency SOS Alert"
    
    /**
     * Send emergency message to all personal contacts
     * POST https://app.whosafeglobal.com/sendemergencymessages
     * Body: { "u_id": USER_ID }
     */
    suspend fun sendEmergencyMessages(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = "$baseUrl/sendemergencymessages"
                ActivityRecorder.record("Request Sent to Server (Personal)", "URL: $url")
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Sending emergency messages to personal contacts")
                }
                
                val requestBody = JSONObject().apply {
                    put("u_id", userId)
                }.toString()
                
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
                    .addHeader("Content-Type", "application/json")
                    .build()
                
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                
                if (response.isSuccessful && responseBody != null) {
                    try {
                        val json = JSONObject(responseBody)
                        val status = json.optInt("status", -1)
                        val success = status == 1 || response.isSuccessful
                        if (BuildConfig.DEBUG) {
                            if (success) {
                                ActivityRecorder.record("Server Response Received (Personal)", "Status: SUCCESS", "SUCCESS")
                                Log.d(TAG, "Emergency messages sent successfully")
                            } else {
                                ActivityRecorder.record("Server Response Received (Personal)", "Status: $status", "FAILURE")
                                Log.w(TAG, "Emergency messages API returned status: $status")
                            }
                        }
                        success
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Failed to parse response", e)
                        }
                        response.isSuccessful
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Failed to send emergency messages. HTTP ${response.code}")
                    }
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending emergency messages", e)
                false
            }
        }
    }
    
    /**
     * Send emergency message to a specific group with retry
     * POST https://app.whosafeglobal.com/sendgroupmessage
     * Body: { "g_id": GROUP_ID, "u_id": USER_ID, "message": MESSAGE }
     */
    private suspend fun sendGroupMessageInternal(groupId: String, userId: String, message: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = "$baseUrl/sendgroupmessage"
                val finalMessage = message.ifEmpty { DEFAULT_MESSAGE }
                
                val requestBody = JSONObject().apply {
                    put("g_id", groupId)
                    put("u_id", userId)
                    put("message", finalMessage)
                }.toString()
                
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
                    .addHeader("Content-Type", "application/json")
                    .build()
                
                ActivityRecorder.record("Request Sent to Server (Group)", "Group ID: $groupId")
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                
                if (response.isSuccessful && responseBody != null) {
                    try {
                        val json = JSONObject(responseBody)
                        val status = json.optInt("status", -1)
                        val success = status == 1 || response.isSuccessful
                        if (success) {
                            ActivityRecorder.record("Server Response Received (Group)", "Group ID: $groupId | Status: SUCCESS", "SUCCESS")
                        } else {
                            ActivityRecorder.record("Server Response Received (Group)", "Group ID: $groupId | Status: $status", "FAILURE")
                        }
                        if (!success && BuildConfig.DEBUG) {
                            Log.w(TAG, "Group message API returned status: $status")
                        }
                        success
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Failed to parse group message response", e)
                        }
                        response.isSuccessful
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Failed to send group message. HTTP ${response.code}")
                    }
                    false
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Error sending group message", e)
                }
                false
            }
        }
    }
    
    /**
     * Send group message with single retry on failure
     */
    suspend fun sendGroupMessage(groupId: String, userId: String, message: String = DEFAULT_MESSAGE): Boolean {
        val success = sendGroupMessageInternal(groupId, userId, message)
        if (!success) {
            // Single retry
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Retrying group message")
            }
            return sendGroupMessageInternal(groupId, userId, message)
        }
        return success
    }
    
    /**
     * Fetch all groups for a user
     * GET https://app.whosafeglobal.com/getallgroups/{u_id}
     * Returns list of group IDs
     */
    suspend fun fetchUserGroups(userId: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "$baseUrl/getallgroups/$userId"
                
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
                
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                
                val groupIds = mutableListOf<String>()
                
                if (response.isSuccessful && responseBody != null) {
                    try {
                        if (responseBody.trim().startsWith("<")) {
                            if (BuildConfig.DEBUG) {
                                Log.e(TAG, "Received HTML instead of JSON")
                            }
                            return@withContext emptyList()
                        }
                        
                        val json = JSONObject(responseBody)
                        val status = json.optInt("status", -1)
                        val dataArray = json.optJSONArray("data") ?: json.optJSONArray("groups")
                        
                        if ((status == 1 || dataArray != null) && dataArray != null) {
                            for (i in 0 until dataArray.length()) {
                                val item = dataArray.getJSONObject(i)
                                val id = item.optString("g_id").ifEmpty {
                                    item.optString("group_id").ifEmpty {
                                        item.optString("id").ifEmpty {
                                            item.optString("groupId")
                                        }
                                    }
                                }
                                if (id.isNotEmpty()) {
                                    groupIds.add(id)
                                }
                            }
                        }
                        
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Found ${groupIds.size} groups")
                        }
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Failed to parse groups response", e)
                        }
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Failed to fetch groups. HTTP ${response.code}")
                    }
                }
                
                groupIds
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching user groups", e)
                emptyList()
            }
        }
    }
    
    /**
     * Trigger complete SOS flow (SEQUENTIAL):
     * 1. Send emergency messages to all personal contacts (MANDATORY FIRST)
     * 2. Fetch all user groups
     * 3. Send emergency messages to each group (MANDATORY FOR ALL GROUPS)
     * 
     * If location is provided, it will be appended to the message.
     */
    suspend fun triggerSOS(userId: String, locationLink: String? = null): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Step 1: Send to personal contacts (MANDATORY FIRST)
                val personalSuccess = sendEmergencyMessages(userId)
                if (!personalSuccess && BuildConfig.DEBUG) {
                    Log.w(TAG, "Failed to send to personal contacts, continuing with groups")
                }
                
                // Step 2: Fetch all groups
                val groups = fetchUserGroups(userId)
                
                if (groups.isEmpty()) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "No groups found, SOS flow complete")
                    }
                    return@withContext personalSuccess
                }
                
                // Step 3: Send to ALL groups (MANDATORY FOR EACH)
                val message = if (locationLink != null && locationLink.isNotEmpty()) {
                    "$DEFAULT_MESSAGE\nLocation: $locationLink"
                } else {
                    DEFAULT_MESSAGE
                }
                
                var groupSuccessCount = 0
                groups.forEach { groupId ->
                    try {
                        if (sendGroupMessage(groupId, userId, message)) {
                            groupSuccessCount++
                        }
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Error sending to group", e)
                        }
                        // Continue with other groups even if one fails
                    }
                }
                
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Sent to $groupSuccessCount/${groups.size} groups")
                }
                
                ActivityRecorder.record("SOS Flow Completed", "Personal Succeeded: $personalSuccess | Groups Succeeded: $groupSuccessCount")
                
                // Return true if at least personal contacts or any group succeeded
                personalSuccess || groupSuccessCount > 0
            } catch (e: Exception) {
                Log.e(TAG, "Error in triggerSOS", e)
                false
            }
        }
    }
}
