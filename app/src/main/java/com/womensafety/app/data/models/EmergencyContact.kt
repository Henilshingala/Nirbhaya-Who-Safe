package com.womensafety.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val relationship: String = "",
    val isActive: Boolean = true,
    // CRITICAL: Backend ID (c_id) MUST be Int to match server expectations
    val backendId: Int? = null
)
