package com.womensafety.app

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import com.womensafety.app.data.models.EmergencyContact

/**
 * SOSManager - DEPRECATED SMS-BASED SOS
 * 
 * ⚠️ WARNING: SMS-based SOS has been replaced with API-based messaging.
 * This class is kept for backward compatibility only.
 * 
 * SOS alerts are now sent via:
 * - POST /sendemergencymessages (for personal contacts)
 * - POST /sendgroupmessage (for each user group)
 * 
 * See: SOSApiService for the new implementation.
 */
class SOSManager(private val context: Context) {

    private val smsManager: SmsManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION") SmsManager.getDefault()
        }
    }

    /**
     * @deprecated SMS-based SOS has been replaced with API-based messaging.
     * SOS alerts are now handled by SOSApiService.
     * This method is disabled and will not send SMS.
     */
    @Deprecated("Use SOSApiService.triggerSOS() instead. SMS-based SOS is no longer used.")
    fun sendSOSSAlerts(
            contacts: List<EmergencyContact>,
            userLocation: String = "",
            senderPhoneNumber: String = "",
            senderName: String = ""
    ): Boolean {
        Log.w("SOSManager", "⚠️ DEPRECATED: sendSOSSAlerts() called but SMS-based SOS is disabled.")
        Log.w("SOSManager", "⚠️ SOS alerts are now sent via API (SOSApiService).")
        Log.w("SOSManager", "⚠️ This method is kept for backward compatibility only and does nothing.")
        
        // SMS sending is disabled - return false to indicate no SMS was sent
        return false
    }

    /**
     * Send location SMS to all emergency contacts during SOS
     * This is an extra safety layer sent AFTER location is fetched
     * Format: "SOS ALERT!\nI need help. My current location:\n[Google Maps Link]"
     */
    fun sendLocationSMS(
            contacts: List<EmergencyContact>,
            locationLink: String
    ): Boolean {
        if (contacts.isEmpty()) {
            Log.e("SOSManager", "No contacts to send location SMS to")
            return false
        }

        if (locationLink.isEmpty()) {
            Log.w("SOSManager", "Location link is empty, skipping SMS")
            return false
        }

        var successCount = 0
        val message = "SOS ALERT!\nI need help. My current location:\n$locationLink"

        contacts.forEach { contact ->
            try {
                if (contact.phoneNumber.isBlank()) {
                    Log.w("SOSManager", "Skipping contact with empty phone number: ${contact.name}")
                    return@forEach
                }

                val parts = smsManager.divideMessage(message)
                smsManager.sendMultipartTextMessage(contact.phoneNumber, null, parts, null, null)
                successCount++
                Log.d("SOSManager", "Location SMS sent to ${contact.name}")
            } catch (e: SecurityException) {
                Log.e("SOSManager", "SMS permission denied for ${contact.name}", e)
            } catch (e: Exception) {
                Log.e("SOSManager", "Failed to send location SMS to ${contact.name}", e)
            }
        }

        Log.d("SOSManager", "Location SMS sent to $successCount/${contacts.size} contacts")
        return successCount > 0
    }

    fun sendLocationShare(
            contacts: List<EmergencyContact>,
            userLocation: String = "",
            senderPhoneNumber: String = ""
    ): Boolean {
        if (contacts.isEmpty()) {
            Log.e("SOSManager", "No contacts to send location to")
            return false
        }

        var successCount = 0

        contacts.forEach { contact ->
            try {
                if (contact.phoneNumber.isBlank()) {
                    Log.w("SOSManager", "Skipping contact: ${contact.name}")
                    return@forEach
                }

                val message = buildLocationOnlyMessage(contact.name, userLocation, senderPhoneNumber)
                val parts = smsManager.divideMessage(message)
                smsManager.sendMultipartTextMessage(contact.phoneNumber, null, parts, null, null)
                successCount++
                Log.d("SOSManager", "Location link sent to ${contact.name}")
            } catch (e: Exception) {
                Log.e("SOSManager", "Failed to send location to ${contact.name}", e)
            }
        }

        return successCount > 0
    }

    private fun buildSOSMessage(
            contactName: String,
            location: String,
            senderPhoneNumber: String,
            senderName: String
    ): String {
        val senderInfo = if (senderPhoneNumber.isNotEmpty()) senderPhoneNumber else "Unknown"
        val displayName = if (senderName.isNotEmpty()) senderName else senderInfo
        val locationLink = if (location.isNotEmpty()) location else "Location unavailable"

        return """URGENT SAFETY ALERT

This is an emergency message sent via the Who Safe Pendant.

▲ $displayName is in immediate danger and needs urgent help.

Current Location:
$locationLink

Please call immediately: $senderInfo
If unreachable, inform the nearest police station at once.

You received this alert because you are listed as a trusted emergency contact.

⏱ Time is critical. Immediate action can save a life.

— WHO SAFE Emergency Response System"""
    }
    private fun buildLocationOnlyMessage(
            contactName: String,
            location: String,
            senderPhoneNumber: String
    ): String {
        val senderInfo = if (senderPhoneNumber.isNotEmpty()) senderPhoneNumber else "Someone"
        
        return """📍 Live Location Shared by $senderInfo
        
My Current Location:
$location

Tap the link to see my location on Google Maps.

- Sent via Nirbhaya Safe"""
    }
}
