package com.womensafety.app.logging

import android.content.Context
import android.util.Log
import com.womensafety.app.data.UserPreferences
import com.womensafety.app.data.models.ActivityRecord
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * ActivityRecorder - Handles logging of app activities to a remote Firebase database.
 * Now updated to use the full Firebase Realtime Database SDK.
 */
object ActivityRecorder {
    private const val TAG = "ActivityRecorder"
    private val scope = CoroutineScope(Dispatchers.IO)
    private var appContext: Context? = null
    
    // Explicit Database URL provided by user
    private const val DB_URL = "https://nirbhaya-who-safe-default-rtdb.firebaseio.com/"
    
    // Lazy initialize Firebase Database reference
    private val databaseRef by lazy {
        FirebaseDatabase.getInstance(DB_URL).getReference("activities")
    }

    /**
     * Initialize with application context
     */
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * Records an activity to Firebase and locally.
     */
    fun record(activityName: String, detail: String? = null, status: String? = null, context: Context? = null) {
        val currentContext = context?.applicationContext ?: appContext ?: return
        if (appContext == null && context != null) {
            appContext = context.applicationContext
        }
        
        val timestamp = System.currentTimeMillis()
        val userPrefs = UserPreferences.getInstance(currentContext)
        val userId = userPrefs.getUserId() ?: "unknown_user"
        val fullName = userPrefs.getFullName()
        
        val record = ActivityRecord(
            timestamp = timestamp,
            activityName = activityName,
            detail = detail,
            status = status ?: "INFO",
            userId = userId,
            userName = fullName
        )
        
        // 1. Log to Logcat for development
        Log.i(TAG, "Recording Activity: $activityName | User: $userId ($fullName) | Status: ${status ?: "INFO"}")

        // 2. Log to physical file
        writeToFile(currentContext, record)

        // 3. Upload to Firebase using SDK
        scope.launch {
            try {
                // Path: /activities/{userId}/{timestamp}
                databaseRef.child(userId).child(timestamp.toString()).setValue(record)
                    .addOnSuccessListener {
                        Log.d(TAG, "Successfully uploaded activity to Firebase SDK")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to upload activity to Firebase SDK", e)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error in Firebase SDK upload", e)
            }
        }
    }

    /**
     * Helper to write record to a local file
     */
    private fun writeToFile(context: Context, record: ActivityRecord) {
        scope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val dateStr = sdf.format(Date(record.timestamp))
                val logEntry = "[$dateStr] ${record.activityName} | Status: ${record.status ?: "N/A"} | Detail: ${record.detail ?: "None"}\n"
                
                context.openFileOutput("activity_logs.txt", Context.MODE_APPEND).use {
                    it.write(logEntry.toByteArray())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to write to log file", e)
            }
        }
    }
}
