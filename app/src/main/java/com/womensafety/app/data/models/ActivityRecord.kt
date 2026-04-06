package com.womensafety.app.data.models

import androidx.annotation.Keep

@Keep
data class ActivityRecord(
    val timestamp: Long = 0L,
    val activityName: String = "",
    val detail: String? = null,
    val status: String? = null,
    val userId: String? = null,
    val userName: String? = null
)
