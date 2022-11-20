package com.example.medictionary.services

import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi

class ServiceTrigger : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate() {
        super.onCreate()
        startJob()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startJob(){
        stopJob()
        val componentName = ComponentName(this, NotificationService::class.java)
        val info = JobInfo.Builder(123,componentName)
            .setPersisted(true)
            .setPeriodic(15*60*1000).build()
        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        if(scheduler.schedule(info) == JobScheduler.RESULT_SUCCESS){
            Log.d("scd","Job scheduled")
        } else {
            Log.d("scd","NOT")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun stopJob(){
        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancelAll()
        Log.d("scd","Job cancelled")
    }
}

