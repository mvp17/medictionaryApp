package com.example.medictionary.snoozing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.medictionary.services.ForegroundService

class ExecutorReceiver : BroadcastReceiver() {

    var element = "pills"
    override fun onReceive(context: Context, intent: Intent) {

        val extras : Bundle? = intent.extras
        element = extras?.getString("NOTIFICATION_NAME")!!

        val foregroundService : Intent = Intent(context, ForegroundService::class.java).apply {
            putExtra("element", element)
        }

        ContextCompat.startForegroundService(context, foregroundService)
    }
}
