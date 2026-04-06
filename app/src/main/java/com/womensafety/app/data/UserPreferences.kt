package com.womensafety.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * UserPreferences - Manages persistent user data and registration state
 * 
 * This class handles:
 * - One-time registration flag (persists across app restarts)
 * - User profile data storage (auto-filled in Profile screen)
 * - Automatic data clearing when app data is cleared or app is reinstalled
 * 
 * Uses EncryptedSharedPreferences for secure storage
 */
class UserPreferences(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    companion object {
        private const val PREFS_NAME = "user_preferences_secure"
        
        // Registration flag
        private const val KEY_IS_REGISTERED = "is_registered"
        
        // User data keys
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_MOBILE_NUMBER = "mobile_number"
        private const val KEY_EMAIL = "email"
        private const val KEY_PROFILE_IMAGE_URI = "profile_image_uri"
        
        @Volatile
        private var instance: UserPreferences? = null
        
        fun getInstance(context: Context): UserPreferences {
            return instance ?: synchronized(this) {
                instance ?: UserPreferences(context.applicationContext).also { instance = it }
            }
        }
    }
    
    /**
     * Check if user has completed registration
     * This determines if Signup screen should be shown
     */
    fun isRegistrationComplete(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_REGISTERED, false)
    }
    
    /**
     * Mark registration as complete
     * Called after successful registration API response
     */
    fun setRegistrationComplete(isComplete: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_REGISTERED, isComplete).apply()
    }
    
    /**
     * Save user data after successful registration
     * This data will auto-fill the Profile screen
     */
    fun saveUserData(userData: UserData) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_ID, userData.userId)
            putString(KEY_FULL_NAME, userData.fullName)
            putString(KEY_MOBILE_NUMBER, userData.mobileNumber)
            putString(KEY_EMAIL, userData.email)
            if (userData.profileImageUri != null) {
                putString(KEY_PROFILE_IMAGE_URI, userData.profileImageUri)
            }
            apply()
        }
    }
    
    /**
     * Get stored user data
     * Returns null if no data is stored
     */
    fun getUserData(): UserData? {

        
        val userId = sharedPreferences.getString(KEY_USER_ID, null)
        val fullName = sharedPreferences.getString(KEY_FULL_NAME, null)
        val mobileNumber = sharedPreferences.getString(KEY_MOBILE_NUMBER, null)
        val email = sharedPreferences.getString(KEY_EMAIL, null)
        
        // If essential data is missing, return null
        if (userId == null || fullName == null || mobileNumber == null) {
            return null
        }
        
        return UserData(
            userId = userId,
            fullName = fullName,
            mobileNumber = mobileNumber,
            email = email ?: "",
            profileImageUri = sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, null)
        )
    }
    
    /**
     * Update individual user fields
     * Used when user edits their profile
     */
    fun updateFullName(fullName: String) {
        sharedPreferences.edit().putString(KEY_FULL_NAME, fullName).apply()
    }
    
    fun updateMobileNumber(mobileNumber: String) {
        sharedPreferences.edit().putString(KEY_MOBILE_NUMBER, mobileNumber).apply()
    }
    
    fun updateEmail(email: String) {
        sharedPreferences.edit().putString(KEY_EMAIL, email).apply()
    }
    
    fun updateProfileImageUri(uri: String) {
        sharedPreferences.edit().putString(KEY_PROFILE_IMAGE_URI, uri).apply()
    }
    
    /**
     * Clear all user data
     * Used for logout or testing
     * Note: This will cause the Signup screen to appear again
     */
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }
    
    /**
     * Get individual fields for backward compatibility
     */
    fun getFullName(): String {
        return sharedPreferences.getString(KEY_FULL_NAME, "") ?: ""
    }
    
    fun getMobileNumber(): String {
        return sharedPreferences.getString(KEY_MOBILE_NUMBER, "") ?: ""
    }
    
    fun getEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "") ?: ""
    }
    
    fun getProfileImageUri(): String {
        return sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, "") ?: ""
    }
    
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
}

/**
 * Data class representing user information
 */
data class UserData(
    val userId: String,
    val fullName: String,
    val mobileNumber: String,
    val email: String,
    val profileImageUri: String? = null
)
