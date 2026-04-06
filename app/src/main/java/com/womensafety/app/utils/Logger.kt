package com.womensafety.app.utils

import android.util.Log
import com.womensafety.app.BuildConfig

/**
 * Centralized logging utility with environment-based control
 * 
 * Debug builds: Full verbose logging
 * Release builds: Errors only
 */
object Logger {
    
    private const val TAG_PREFIX = "WomenSafety"
    
    /**
     * Debug level - Only in debug builds
     */
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("$TAG_PREFIX:$tag", message)
        }
    }
    
    /**
     * Info level - Only in debug builds
     */
    fun i(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.i("$TAG_PREFIX:$tag", message)
        }
    }
    
    /**
     * Warning level - Always logged
     */
    fun w(tag: String, message: String) {
        Log.w("$TAG_PREFIX:$tag", message)
    }
    
    /**
     * Error level - Always logged
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e("$TAG_PREFIX:$tag", message, throwable)
        } else {
            Log.e("$TAG_PREFIX:$tag", message)
        }
    }
    
    /**
     * Verbose API logging - Only in debug builds
     */
    fun api(tag: String, block: () -> String) {
        if (BuildConfig.DEBUG) {
            Log.d("$TAG_PREFIX:API:$tag", block())
        }
    }
    
    /**
     * Box-style formatted logging for important events
     */
    fun box(tag: String, title: String, vararg lines: String) {
        if (BuildConfig.DEBUG) {
            val fullTag = "$TAG_PREFIX:$tag"
            Log.d(fullTag, "╔════════════════════════════════════════════════╗")
            Log.d(fullTag, "║ $title")
            Log.d(fullTag, "╠════════════════════════════════════════════════╣")
            lines.forEach { line ->
                Log.d(fullTag, "║ $line")
            }
            Log.d(fullTag, "╚════════════════════════════════════════════════╝")
        }
    }
}
