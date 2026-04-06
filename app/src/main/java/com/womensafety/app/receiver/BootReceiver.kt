package com.womensafety.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/** Receiver that runs after device boot. Ensures SMS and Call receivers are enabled. */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
                        intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {

            Log.d("BootReceiver", "Device booted - emergency receivers are active")

            // Receivers are already enabled in manifest with directBootAware=true
            // This just logs that the system is ready
        }
    }
}
