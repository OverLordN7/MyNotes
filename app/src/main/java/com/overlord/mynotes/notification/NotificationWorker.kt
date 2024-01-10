package com.overlord.mynotes.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext,workerParams) {
    override fun doWork(): Result {
        val noteNotification = NoteNotification(applicationContext)
        val notificationManager = noteNotification.provideNotificationManager()
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(applicationContext,"Access to Notifications not Granted", Toast.LENGTH_LONG).show()
        }
        notificationManager.notify(1, noteNotification.provideNotificationBuilder().build() )

        return Result.success()
    }

    companion object {
        const val WORK_NAME = "NotificationWorker"
    }
}