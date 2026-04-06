package com.womensafety.app.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log

/**
 * MIUI Device Detector & Helper
 * 
 * Detects Xiaomi/Redmi/POCO devices and provides utilities to:
 * - Open MIUI-specific system settings
 * - Verify background permissions
 * - Guide users through MIUI's restrictive permission system
 */
object MiuiDeviceDetector {
    
    private const val TAG = "MiuiDetector"
    
    /**
     * Check if the device is running MIUI/HyperOS
     */
    fun isMiuiDevice(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        
        val isXiaomiDevice = manufacturer == "xiaomi" || 
                            brand == "xiaomi" || 
                            brand == "redmi" || 
                            brand == "poco"
        
        // Also check for MIUI system properties
        val hasMiuiProperty = try {
            val miuiVersion = getSystemProperty("ro.miui.ui.version.name")
            !miuiVersion.isNullOrBlank()
        } catch (e: Exception) {
            false
        }
        
        val result = isXiaomiDevice || hasMiuiProperty
        Log.d(TAG, "Device check: manufacturer=$manufacturer, brand=$brand, isMIUI=$result")
        return result
    }
    
    /**
     * Get MIUI version if available
     */
    fun getMiuiVersion(): String? {
        return try {
            getSystemProperty("ro.miui.ui.version.name") 
                ?: getSystemProperty("ro.build.version.incremental")
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Open MIUI Auto-Start settings
     */
    fun openAutoStartSettings(context: Context): Boolean {
        // Method 1: Direct component to AutoStart activity (most reliable)
        try {
            val intent = Intent().apply {
                component = android.content.ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "✅ Opened Auto-Start via direct component")
            return true
        } catch (e: Exception) {
            Log.w(TAG, "Method 1 failed, trying method 2", e)
        }
        
        // Method 2: Try via action with package filter
        try {
            val intent = Intent("miui.intent.action.OP_AUTO_START").apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "✅ Opened Auto-Start via action")
            return true
        } catch (e: Exception) {
            Log.w(TAG, "Method 2 failed, trying method 3", e)
        }
        
        // Method 3: Open Security Center main and user will find Auto-start
        try {
            val intent = Intent().apply {
                component = android.content.ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.securitycenter.MainActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "⚠️ Opened Security Center (manual navigation required)")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "All Auto-Start methods failed, opening app settings", e)
            return openAppDetailsSettings(context)
        }
    }
    
    /**
     * Open MIUI Battery Saver settings for this app
     */
    fun openBatterySaverSettings(context: Context): Boolean {
        return try {
            val intent = Intent().apply {
                action = "miui.intent.action.POWER_HIDE_MODE_APP_LIST"
                putExtra("package_name", context.packageName)
                putExtra("package_label", getAppName(context))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "Opened MIUI battery saver settings")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Failed to open battery settings with MIUI intent", e)
            openBatteryOptimizationSettingsStandard(context)
        }
    }
    
    private fun openBatteryOptimizationSettingsStandard(context: Context): Boolean {
        return try {
            val intent = Intent().apply {
                action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "Opened standard battery optimization settings")
            true
        } catch (e: Exception) {
            Log.e(TAG, "All battery settings methods failed", e)
            false
        }
    }
    
    /**
     * Open MIUI Background Activity settings
     */
    fun openBackgroundActivitySettings(context: Context): Boolean {
        return try {
            val intent = Intent().apply {
                component = android.content.ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity"
                )
                putExtra("extra_pkgname", context.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "Opened MIUI background activity settings")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Failed to open background activity settings", e)
            openAppDetailsSettings(context)
        }
    }
    
    /**
     * Open app details/permissions page
     */
    fun openAppDetailsSettings(context: Context): Boolean {
        return try {
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "Opened app details settings")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open app details", e)
            false
        }
    }
    
    /**
     * Open notification settings
     */
    fun openNotificationSettings(context: Context): Boolean {
        return try {
            val intent = Intent().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                } else {
                    action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    putExtra("app_package", context.packageName)
                    putExtra("app_uid", context.applicationInfo.uid)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "Opened notification settings")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open notification settings", e)
            false
        }
    }
    
    /**
     * Check if app has battery optimization disabled (unrestricted)
     */
    fun isBatteryOptimizationDisabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true // Not applicable on older versions
        }
    }
    
    /**
     * Check if notifications are enabled
     */
    fun areNotificationsEnabled(context: Context): Boolean {
        return androidx.core.app.NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
    
    /**
     * Verify all critical MIUI permissions are set
     */
    fun areAllMiuiPermissionsGranted(context: Context): Boolean {
        val batteryOptimized = isBatteryOptimizationDisabled(context)
        val notificationsEnabled = areNotificationsEnabled(context)
        
        Log.d(TAG, "Permission status: battery=$batteryOptimized, notifications=$notificationsEnabled")
        
        // We can't programmatically check auto-start and background activity,
        // so we rely on user confirmation via the setup flow
        return batteryOptimized && notificationsEnabled
    }
    
    private fun getAppName(context: Context): String {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            context.packageName
        }
    }
    
    private fun getSystemProperty(key: String): String? {
        return try {
            val process = Runtime.getRuntime().exec("getprop $key")
            process.inputStream.bufferedReader().readLine()
        } catch (e: Exception) {
            null
        }
    }
}
