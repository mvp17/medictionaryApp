package com.example.medictionary.snoozing

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.medictionary.services.ForegroundService
import java.util.*


class PlanarReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onReceive(context: Context, intent: Intent) {

        val extras = intent.extras!!
        val element = extras.getString("NOTIFICATION_NAME").toString()


        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        Log.d("stopping",element)

        context.stopService(Intent(context, ForegroundService::class.java))

        val intent = Intent(context, ExecutorReceiver::class.java).apply {
            putExtra("NOTIFICATION_NAME", element)
        }
        intent.action = element
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 5)
        val date: Date = calendar.time
        alarmManager!![AlarmManager.RTC_WAKEUP, date.time] = pendingIntent
        Log.d("Snoozing","Class planning")

    }
}
