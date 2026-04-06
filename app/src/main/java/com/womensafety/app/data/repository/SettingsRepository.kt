package com.womensafety.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.womensafety.app.data.SecurePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_settings")

/**
 * Unified Settings Repository using DataStore for non-sensitive settings
 * and EncryptedSharedPreferences for sensitive data (via SecurePreferences)
 */
class SettingsRepository(private val context: Context) {
    companion object {
        private val CANCEL_DELAY_KEY = intPreferencesKey("cancel_delay_seconds")
        private val REPEAT_ALERT_COUNT_KEY = intPreferencesKey("repeat_alert_count")
        private val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("onboarding_complete")
        private val SMS_PERMISSION_KEY = booleanPreferencesKey("sms_permission_granted")
        private val ENABLE_SOUND_KEY = booleanPreferencesKey("enable_sound")
        private val ENABLE_VIBRATION_KEY = booleanPreferencesKey("enable_vibration")
    }

    // Non-sensitive settings from DataStore
    val cancelDelaySeconds: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[CANCEL_DELAY_KEY] ?: 10
    }

    val repeatAlertCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[REPEAT_ALERT_COUNT_KEY] ?: 0
    }

    val onboardingComplete: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETE_KEY] ?: false
    }

    val smsPermissionGranted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SMS_PERMISSION_KEY] ?: false
    }

    val enableSound: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ENABLE_SOUND_KEY] ?: true
    }

    val enableVibration: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ENABLE_VIBRATION_KEY] ?: true
    }

    // Sensitive data from SecurePreferences
    fun getUserPhoneNumber(): String = SecurePreferences.getUserPhoneNumber(context)
    fun getIotSimNumber(): String = SecurePreferences.getIotSimNumber(context)
    fun getUserRole(): String = SecurePreferences.getUserRole(context)
    fun getManualSosDelay(): Int = SecurePreferences.getManualSosDelay(context)

    suspend fun setCancelDelay(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[CANCEL_DELAY_KEY] = seconds
        }
    }

    suspend fun setRepeatAlertCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[REPEAT_ALERT_COUNT_KEY] = count
        }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETE_KEY] = complete
        }
    }

    suspend fun setSmsPermissionGranted(granted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SMS_PERMISSION_KEY] = granted
        }
    }

    suspend fun setEnableSound(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ENABLE_SOUND_KEY] = enabled
        }
    }

    suspend fun setEnableVibration(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ENABLE_VIBRATION_KEY] = enabled
        }
    }

    fun setUserPhoneNumber(phoneNumber: String) {
        SecurePreferences.setUserPhoneNumber(context, phoneNumber)
    }

    fun setIotSimNumber(iotNumber: String) {
        SecurePreferences.setIotSimNumber(context, iotNumber)
    }

    fun setUserRole(role: String) {
        SecurePreferences.setUserRole(context, role)
    }

    fun setManualSosDelay(delaySeconds: Int) {
        SecurePreferences.setManualSosDelay(context, delaySeconds)
    }
}

