package com.womensafety.app

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

class LocationHelper(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    data class LocationResult(val location: Location?, val isFresh: Boolean)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationResult(): LocationResult {
        Log.d("LocationHelper", "Getting current location result...")

        // Try to get the fresh high-accuracy location first
        val freshLocation = getFreshLocation()
        if (freshLocation != null) {
            Log.d(
                    "LocationHelper",
                    "✓ Fresh location obtained: (${freshLocation.latitude}, ${freshLocation.longitude})"
            )
            return LocationResult(freshLocation, true)
        }

        // Fallback to last known location if fresh location fails
        Log.d("LocationHelper", "Fresh location unavailable, trying last known location...")
        val lastKnown = getLastKnownLocation()
        if (lastKnown != null) {
            Log.d(
                    "LocationHelper",
                    "✓ Last known location obtained: (${lastKnown.latitude}, ${lastKnown.longitude})"
            )
            return LocationResult(lastKnown, false)
        } else {
            Log.w("LocationHelper", "⚠ No location available (fresh or last known)")
            return LocationResult(null, false)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? = getCurrentLocationResult().location

    @SuppressLint("MissingPermission")
    private suspend fun getFreshLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            try {
                Log.d("LocationHelper", "Requesting fresh high-accuracy location...")
                val cancellationTokenSource = CancellationTokenSource()

                fusedLocationClient
                        .getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                cancellationTokenSource.token
                        )
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                Log.d(
                                        "LocationHelper",
                                        "Fresh location success: (${location.latitude}, ${location.longitude})"
                                )
                            } else {
                                Log.w(
                                        "LocationHelper",
                                        "Fresh location request succeeded but returned null"
                                )
                            }
                            continuation.resume(location)
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                    "LocationHelper",
                                    "Fresh location request failed: ${e.message}",
                                    e
                            )
                            continuation.resume(null)
                        }

                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                    Log.d("LocationHelper", "Fresh location request cancelled")
                }
            } catch (e: Exception) {
                Log.e("LocationHelper", "Error in fresh location request: ${e.message}", e)
                continuation.resume(null)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? {
        return try {
            Log.d("LocationHelper", "Requesting last known location...")
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                Log.d(
                        "LocationHelper",
                        "Last known location found: (${location.latitude}, ${location.longitude})"
                )
            } else {
                Log.w("LocationHelper", "No last known location available")
            }
            location
        } catch (e: Exception) {
            Log.e("LocationHelper", "Error getting last known location: ${e.message}", e)
            null
        }
    }

    suspend fun getGoogleMapsLink(): String {
        Log.d("LocationHelper", "Generating Google Maps link...")
        val location = getCurrentLocation()
        return if (location != null) {
            val link =
                    "https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
            Log.d("LocationHelper", "✓ Maps link generated: $link")
            link
        } else {
            Log.w("LocationHelper", "✗ Could not generate Maps link - no location available")
            ""
        }
    }
}
