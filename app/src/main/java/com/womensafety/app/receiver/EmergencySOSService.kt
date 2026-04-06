package com.womensafety.app.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.womensafety.app.BuildConfig
import com.womensafety.app.EmergencyAlertActivity
import com.womensafety.app.LocationHelper
import com.womensafety.app.R
import com.womensafety.app.data.UserPreferences
import com.womensafety.app.network.SOSApiService
import com.womensafety.app.logging.ActivityRecorder
import kotlinx.coroutines.*

/**
 * COMPREHENSIVE EMERGENCY SOS SERVICE
 * 
 * This service handles all SOS logic in the background:
 * 1. Plays a loud siren even if the phone is locked/silent (using EmergencyAlarmPlayer)
 * 2. Fetches location (Live GPS with fallback to Last Known)
 * 3. Sends SOS SMS to all emergency contacts
 * 4. Shows a high-priority notification that can be seen on lock screen
 * 
 * Works 100% in background on Android 12+ by using Foreground Service rules.
 */
class EmergencySOSService : Service() {

    private var sirenPlayer: EmergencyAlarmPlayer? = null
    private var isRunning = false
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val sosApiService = SOSApiService()

    companion object {
        private const val CHANNEL_ID = "EmergencySOSChannel"
        private const val NOTIFICATION_ID = 1001
        const val ACTION_STOP_SOS = "com.womensafety.app.action.STOP_SOS"
        const val ACTION_TRIGGER_SOS = "com.womensafety.app.action.TRIGGER_SOS"
        private const val TAG = "EmergencySOSService"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onStartCommand with action: $action")
        }

        if (action == ACTION_STOP_SOS) {
            stopSOS()
            return START_NOT_STICKY
        }

        if (!isRunning) {
            isRunning = true
            ActivityRecorder.record(
                activityName = "SOS Service Started", 
                detail = "Action: $action",
                context = this
            )
            startForegroundServiceWithNotification()
            triggerSOSFlow()
        }

        return START_STICKY
    }

    private fun startForegroundServiceWithNotification() {
        createNotificationChannel()

        val fullScreenIntent = Intent(this, EmergencyAlertActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, EmergencySOSService::class.java).apply { action = ACTION_STOP_SOS }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Notification must be high priority to show on lock screen
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🚨 EMERGENCY SOS ACTIVE 🚨")
            .setContentText("Emergency alerts are being sent. Tap to stop.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setFullScreenIntent(fullScreenPendingIntent, true) // KEY for lock screen
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP SOS", stopPendingIntent)
            .build()

        // Start foreground with proper service type (required on Android 14+ / API 34+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Emergency SOS Alerts"
            val descriptionText = "Critical alerts used during SOS triggers"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setSound(null, null) // We play siren manually
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun triggerSOSFlow() {
        serviceScope.launch {
            try {
                // 1. Play Siren immediately if enabled
                val prefs = getSharedPreferences("women_safety_prefs", Context.MODE_PRIVATE)
                val sirenEnabled = prefs.getBoolean("enable_sound", true)
                if (sirenEnabled) {
                    sirenPlayer = EmergencyAlarmPlayer(this@EmergencySOSService)
                    sirenPlayer?.startEmergencyAlert(durationMs = 60_000L) // 60 seconds
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Siren started")
                    }
                }

                // 2. Get User ID for API calls
                val userPrefs = UserPreferences.getInstance(this@EmergencySOSService)
                val userId = userPrefs.getUserId()
                
                if (userId.isNullOrEmpty()) {
                    Log.e(TAG, "User ID not found, cannot send SOS")
                    return@launch
                }

                // 3. Fetch Location for API message only (no Maps, no SMS)
                var locationLink: String? = null
                try {
                    val locationHelper = LocationHelper(this@EmergencySOSService)
                    locationLink = withTimeout(15000L) {
                        locationHelper.getGoogleMapsLink()
                    }
                    if (locationLink.isNullOrEmpty()) {
                        locationLink = null
                    }
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "Location fetch failed", e)
                    }
                    locationLink = null
                }

                // 4. Send SOS via API (sequential: sendemergencymessages -> groups)
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Triggering SOS via API")
                }
                sosApiService.triggerSOS(userId, locationLink)
                
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "SOS API flow completed")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error in SOS flow", e)
            }
        }
    }

    // Removed waitForLiveLocationAndResend - no longer needed with API-based SOS
    // Location updates are handled by the backend/WhatsApp system

    private fun stopSOS() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Stopping SOS service")
        }
        sirenPlayer?.stop()
        serviceScope.cancel()
        isRunning = false
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSOS()
    }
}
