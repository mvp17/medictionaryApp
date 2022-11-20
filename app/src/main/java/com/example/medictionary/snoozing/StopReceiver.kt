package com.example.medictionary.snoozing

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.medictionary.services.ForegroundService

class StopReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val extras = intent.extras!!
        val element = extras.getString("NOTIFICATION_NAME").toString()


        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        Log.d("stopping",element)

        context.stopService(Intent(context, ForegroundService::class.java))
    }

}
