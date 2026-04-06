package com.womensafety.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.provider.CallLog
import android.telephony.TelephonyManager
import android.util.Log
import com.womensafety.app.BuildConfig
import com.womensafety.app.LocationHelper
import com.womensafety.app.SOSManager
import com.womensafety.app.data.ContactRepository
import com.womensafety.app.data.SecurePreferences
import com.womensafety.app.data.database.AppDatabase
import com.womensafety.app.logging.ActivityRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Simple broadcast receiver that detects incoming calls from IoT SIM. Uses READ_CALL_LOG permission
 * to reliably get caller number. Works in background, with screen off, even if app is killed.
 */
class IoTCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            return
        }

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        if (BuildConfig.DEBUG) {
            Log.d("IoTCallReceiver", "Phone state changed")
        }

        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
            // Priority 1: Get number directly from intent (Instant)
            val incomingNumberFromIntent = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            
            if (BuildConfig.DEBUG) {
                Log.d("IoTCallReceiver", "Incoming call detected")
            }

            // Use goAsync for background processing
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var incomingNumber = incomingNumberFromIntent

                    // Priority 2: Fallback to CallLog if intent number is missing (e.g. on some Android versions)
                    if (incomingNumber.isNullOrBlank()) {
                        if (BuildConfig.DEBUG) {
                            Log.d("IoTCallReceiver", "Number missing in intent, checking call log fallback")
                        }
                        delay(1000) // Small delay to let system write to call log
                        incomingNumber = getLatestIncomingNumber(context)
                    }

                    if (!incomingNumber.isNullOrBlank()) {
                        if (BuildConfig.DEBUG) {
                            Log.d("IoTCallReceiver", "Caller number resolved")
                        }
                        checkAndTriggerSOS(context, incomingNumber)
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.w("IoTCallReceiver", "Could not retrieve caller number")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("IoTCallReceiver", "Error processing call", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    /** Get the most recent INCOMING call number from call log (Strictly within last 15 seconds) */
    private fun getLatestIncomingNumber(context: Context): String? {
        val now = System.currentTimeMillis()
        val fifteenSecondsAgo = now - 15000

        return try {
            val projection = arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE)
            val selection = "${CallLog.Calls.TYPE} = ? AND ${CallLog.Calls.DATE} > ?"
            val selectionArgs = arrayOf(
                CallLog.Calls.INCOMING_TYPE.toString(),
                fifteenSecondsAgo.toString()
            )
            
            val cursor: Cursor? =
                    context.contentResolver.query(
                            CallLog.Calls.CONTENT_URI,
                            projection,
                            selection,
                            selectionArgs,
                            "${CallLog.Calls.DATE} DESC"
                    )
            
            cursor?.use {
                if (it.moveToFirst()) {
                    val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                    if (numberIndex >= 0) {
                        val number = it.getString(numberIndex)
                        Log.d("IoTCallReceiver", "Found recent incoming call in log: $number")
                        return number
                    }
                }
            }
            Log.d("IoTCallReceiver", "No recent incoming calls found in log for the last 15s")
            null
        } catch (e: Exception) {
            Log.e("IoTCallReceiver", "Failed to read call log", e)
            null
        }
    }

    private suspend fun checkAndTriggerSOS(context: Context, incomingNumber: String) {
        try {
            // Get configured IoT SIM number
            val iotSimNumber = SecurePreferences.getIotSimNumber(context).trim()

            if (iotSimNumber.isEmpty()) {
                Log.w("IoTCallReceiver", "⚠️ IoT SIM number not configured")
                return
            }

            val normalizedIncoming = normalizePhoneNumber(incomingNumber)
            val normalizedIoT = normalizePhoneNumber(iotSimNumber)

            Log.d("IoTCallReceiver", "Comparing:")
            Log.d("IoTCallReceiver", "  Incoming: $normalizedIncoming")
            Log.d("IoTCallReceiver", "  IoT SIM:  $normalizedIoT")

                if (normalizedIncoming.endsWith(normalizedIoT) ||
                            normalizedIoT.endsWith(normalizedIncoming)
                ) {
                    Log.d("IoTCallReceiver", "")
                    Log.d("IoTCallReceiver", "✅✅✅ MATCH! IoT DEVICE CALLING ✅✅✅")
                    Log.d("IoTCallReceiver", "")
                    Log.d("IoTCallReceiver", "🚨 STARTING EMERGENCY SOS SERVICE 🚨")
                    
                    // Record Activity
                    ActivityRecorder.record(
                        activityName = "Trigger Hit (Call)", 
                        detail = "Incoming call from IoT Device: $normalizedIncoming",
                        context = context
                    )

                    // Start the comprehensive SOS service
                    try {
                        val sosIntent = Intent(context, EmergencySOSService::class.java).apply {
                            action = EmergencySOSService.ACTION_TRIGGER_SOS
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(sosIntent)
                        } else {
                            context.startService(sosIntent)
                        }
                    } catch (e: Exception) {
                        Log.e("IoTCallReceiver", "Failed to start EmergencySOSService", e)
                        // Fallback: If service fails (e.g. Android 12 foreground restriction), 
                        // we can try starting it via a high-priority notification or alarm.
                    }
                } else {
                    Log.d("IoTCallReceiver", "❌ Not from IoT device - ignoring")
                }
            } catch (e: Exception) {
                Log.e("IoTCallReceiver", "Error in checkAndTriggerSOS", e)
            }
        }

        private fun normalizePhoneNumber(number: String): String {
            // Remove all non-digits and take last 10 digits for comparison
            return number.replace(Regex("[^0-9]"), "").takeLast(10)
        }
    }
