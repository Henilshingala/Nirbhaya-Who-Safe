package com.womensafety.app.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * WhatsApp Message Sender
 * 
 * This class replicates the EXACT functionality from your backend's
 * contact.service.js (lines 648-788) for sending WhatsApp messages.
 * 
 * Backend implementation:
 * - Uses WTalk API (WhatsApp Business API)
 * - Sends via HTTP POST with Bearer token
 * - Uses approved WhatsApp templates
 * 
 * Android implementation:
 * - Makes the SAME HTTP POST request
 * - Uses Retrofit instead of axios
 * - Sends automatically without user interaction
 */
class WhatsAppMessageSender(
    private val wtalkApiUrl: String,
    private val wtalkBearerToken: String
) {
    
    private val apiService: WhatsAppApiService by lazy {
        WhatsAppApiClient.create(wtalkApiUrl, wtalkBearerToken)
    }
    
    /**
     * Sends a simple "hi" message to a phone number
     * 
     * IMPORTANT: This requires a WhatsApp Business API template named "simple_hi_message"
     * to be pre-approved in your WTalk account.
     * 
     * @param phoneNumber Phone number in format: +91XXXXXXXXXX
     * @return true if message was queued successfully, false otherwise
     */
    suspend fun sendHiMessage(phoneNumber: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Attempting to send WhatsApp message to: $phoneNumber")
            
            // Build request matching backend structure (lines 675-705)
            val request = WhatsAppMessageRequest(
                to = phoneNumber,
                recipientType = "individual",
                type = "template",
                template = WhatsAppTemplate(
                    language = WhatsAppLanguage(
                        policy = "deterministic",
                        code = "en_US"
                    ),
                    name = "simple_hi_message", // Must be pre-approved template
                    components = listOf(
                        WhatsAppComponent(
                            type = "body",
                            parameters = listOf(
                                WhatsAppParameter(
                                    type = "text",
                                    text = "hi"
                                )
                            )
                        )
                    )
                )
            )
            
            // Make API call (matches backend line 710)
            val response = apiService.sendMessage(request)
            
            // Check response (matches backend line 719)
            if (response.isSuccessful) {
                val body = response.body()
                val messageStatus = body?.message?.messageStatus
                
                Log.d(TAG, "WhatsApp API Response: $messageStatus")
                
                if (messageStatus == "queued") {
                    Result.success("Message sent successfully! Status: queued")
                } else {
                    Result.failure(Exception("Failed to send message. Status: $messageStatus"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "WhatsApp API Error: $errorBody")
                Result.failure(Exception("API Error: ${response.code()} - $errorBody"))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error sending WhatsApp message", e)
            Result.failure(e)
        }
    }
    
    /**
     * Alternative: Send using a text-only template (no parameters)
     * Use this if you have a template that just contains "hi" as static text
     */
    suspend fun sendHiMessageSimple(phoneNumber: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = WhatsAppMessageRequest(
                to = phoneNumber,
                recipientType = "individual",
                type = "template",
                template = WhatsAppTemplate(
                    language = WhatsAppLanguage(
                        policy = "deterministic",
                        code = "en_US"
                    ),
                    name = "hi_message", // Template with just "hi" text
                    components = emptyList() // No parameters needed
                )
            )
            
            val response = apiService.sendMessage(request)
            
            if (response.isSuccessful && response.body()?.message?.messageStatus == "queued") {
                Result.success("Message sent successfully!")
            } else {
                Result.failure(Exception("Failed to send message"))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error sending WhatsApp message", e)
            Result.failure(e)
        }
    }
    
    companion object {
        private const val TAG = "WhatsAppMessageSender"
    }
}
