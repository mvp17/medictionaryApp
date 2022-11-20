package com.example.medictionary.extra

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medictionary.services.ServiceTrigger

class StartUp : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val intent = Intent(context, ServiceTrigger::class.java)
            context.startService(intent)
        }
    }
}
