package com.womensafety.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

/**
 * Helper class for managing battery optimization settings.
 * 
 * For emergency alert apps, it's critical to be exempt from battery optimization
 * to ensure the app can receive SMS and play alarms even when:
 * - Device is in Doze mode
 * - App is in background
 * - App has been killed by user
 */
object BatteryOptimizationHelper {
    
    private const val TAG = "BatteryOptimization"
    
    /**
     * Check if battery optimization is disabled for this app
     */
    fun isOptimizationDisabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false
        } else {
            true // Not applicable on older versions
        }
    }
    
    /**
     * Open battery optimization settings for this app
     */
    fun openBatteryOptimizationSettings(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open battery optimization settings", e)
            // Fallback: Open general battery optimization settings
            try {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                context.startActivity(intent)
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to open general battery optimization settings", e2)
            }
        }
    }
}

/**
 * Composable dialog to educate user about battery optimization
 */
@Composable
fun BatteryOptimizationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Battery Optimization required",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ) 
        },
        text = {
            Text(
                "To Ensure You Receive Emergency  Alerts Even  When The App Is Closed,Please Diable Battery Optimization For This App.\n\n" +
                "To Ensure You Receive Emergency  Alerts Even  When The App Is Closed,Please Diable Battery Optimization For This App.\n\n" +
                "Tap 'allow'  To Open Settings.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    BatteryOptimizationHelper.openBatteryOptimizationSettings(context)
                    onConfirm()
                }
            ) {
                Text(
                    "Allow",
                    color = androidx.compose.ui.graphics.Color(0xFFE91E63),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Later",
                    color = androidx.compose.ui.graphics.Color(0xFF666666)
                )
            }
        }
    )
}
