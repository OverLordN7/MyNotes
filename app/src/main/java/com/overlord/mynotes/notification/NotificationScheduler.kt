package com.overlord.mynotes.notification

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {
    @SuppressLint("InvalidPeriodicWorkRequestInterval")
    fun scheduleDailyNotification(){
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            //NotificationWorker::class.java,24,TimeUnit.HOURS
            NotificationWorker::class.java,90,TimeUnit.SECONDS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_notification_work",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
}