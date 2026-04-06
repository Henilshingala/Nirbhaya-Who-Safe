package com.womensafety.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.womensafety.app.LocationHelper
import com.womensafety.app.SOSManager
import com.womensafety.app.data.ContactRepository
import com.womensafety.app.data.database.AppDatabase
import com.womensafety.app.logging.ActivityRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SOSBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("SOSReceiver", "SOS broadcast received")

        when (intent.action) {
            "com.womensafety.app.SOS_TRIGGER" -> {
                ActivityRecorder.record(
                    activityName = "Trigger Hit (Broadcast)", 
                    detail = "Received SOS_TRIGGER broadcast",
                    context = context
                )
                handleSOSTrigger(context)
            }
        }
    }

    private fun handleSOSTrigger(context: Context) {
        try {
            Log.d("SOSReceiver", "Forwarding SOS trigger to EmergencySOSService")
            val sosIntent = Intent(context, EmergencySOSService::class.java).apply {
                action = EmergencySOSService.ACTION_TRIGGER_SOS
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(sosIntent)
            } else {
                context.startService(sosIntent)
            }
        } catch (e: Exception) {
            Log.e("SOSReceiver", "Failed to start EmergencySOSService from receiver", e)
        }
    }
}
