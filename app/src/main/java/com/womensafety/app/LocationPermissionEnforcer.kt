package com.womensafety.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat

/**
 * Correctly handles permission vs location services state. Permission = User granted app access to
 * location Location Services = GPS/Network providers enabled on device
 */
object LocationPermissionEnforcer {
    private const val TAG = "LocationEnforcer"

    /**
     * Check if ACCESS_FINE_LOCATION permission is granted. This is SEPARATE from location services
     * being ON/OFF.
     */
    fun isLocationPermissionGranted(context: Context): Boolean {
        val hasPermission =
                ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            Log.d(TAG, "✅ Location permission IS granted")
        } else {
            Log.w(TAG, "⚠️ Location permission NOT granted")
        }

        return hasPermission
    }

    /**
     * Check if device location services (GPS/Network) are enabled. This is SEPARATE from permission
     * state.
     */
    fun areLocationServicesEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val anyEnabled = isGpsEnabled || isNetworkEnabled

        if (anyEnabled) {
            Log.d(
                    TAG,
                    "✅ Location services enabled - GPS: $isGpsEnabled, Network: $isNetworkEnabled"
            )
        } else {
            Log.w(
                    TAG,
                    "⚠️ Location services DISABLED (GPS: $isGpsEnabled, Network: $isNetworkEnabled)"
            )
        }

        return anyEnabled
    }

    /**
     * Open system Location Settings (NOT app permissions). Use this when GPS/Network is OFF, not
     * when permission is denied.
     */
    fun openLocationSettings(context: Context) {
        Log.d(TAG, "🔧 Opening system Location Settings")
        val intent =
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
        context.startActivity(intent)
    }

    /** Open app permission settings. Use this ONLY when permission is NOT granted. */
    fun openAppPermissionSettings(context: Context) {
        Log.d(TAG, "🔧 Opening app permission settings")
        val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
        context.startActivity(intent)
    }

    /** Get message for location services being OFF (GPS/Network disabled). */
    fun getLocationServicesOffMessage(): String {
        return """
            📍 LOCATION SERVICES DISABLED
            
            Your device's GPS/Location is turned OFF.
            
            This emergency app requires location to be enabled to:
            • Send your coordinates in SOS alerts
            • Help emergency responders find you
            
            Please turn ON Location/GPS in the next screen.
            
            The app will redirect you to Location Settings now.
        """.trimIndent()
    }
}
