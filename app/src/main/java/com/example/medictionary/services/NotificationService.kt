package com.example.medictionary.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.medictionary.extra.DBHandler
import com.example.medictionary.models.AlarmModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NotificationService : JobService() {

    var list = mutableListOf<AlarmModel>()
    override fun onCreate() {
        super.onCreate()
        Log.d("TIMING", "onCreate")
    }

    override fun onStartJob(p0: JobParameters?): Boolean {

        Log.d("TIMING", "startCommand")
        val pref = applicationContext.getSharedPreferences("Pills", Context.MODE_PRIVATE)
        val someHandler = Handler(mainLooper)
        var hour: String
        someHandler.postDelayed(object : Runnable {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                Log.d("TIMING", "run")
                try {
                    val dbHelper = DBHandler(applicationContext)
                    list = dbHelper.getActiveAlarms() as MutableList<AlarmModel>
                } catch (e: Exception) {}

                hour = SimpleDateFormat("HH:mm", Locale.US).format(Date())

                for (element in list) {
                    Log.d("TIMING", hour)
                    Log.d("TIMING", element.time)
                    val times = generateTimes(element)
                    Log.d("TIMING", times.toString())
                    if (times.contains(hour) ) {

                        val name = pref.getString("notification_name","")
                        val time = pref.getString("notification_time","")
                        val hour = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
                        if(time != hour && name!=element.name){
                            val editor = pref.edit()
                            editor.putString("notification_name",element.name)
                            editor.putString("notification_time",hour)
                            editor.apply()
                            val foregroundService : Intent = Intent(baseContext, ForegroundService::class.java).apply {
                                putExtra("element", element.name)
                            }
                            ContextCompat.startForegroundService(baseContext, foregroundService)
                            break
                        }
                    }
                }

                someHandler.postDelayed(this, 60000)

            }
        }, 0)
        return true
    }



    private fun formatTimes(time: String): String {
        return if (time.length < 2) {
            "0$time"
        } else {
            time
        }
    }

    fun generateTimes(element: AlarmModel): ArrayList<String> {
        val calendar = Calendar.getInstance()
        val times = arrayListOf(element.time)
        calendar.set(Calendar.HOUR_OF_DAY, element.time.split(":")[0].toInt())
        calendar.set(Calendar.MINUTE, element.time.split(":")[1].toInt())
        for (i in 1 until element.totalDailyAmount.toInt()) {
            calendar.add(Calendar.HOUR, element.hoursPerDose.toInt())
            times.add(formatTimes(calendar.time.hours.toString()) + ":" + formatTimes(calendar.time.minutes.toString()))
        }
        return times
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }
}
