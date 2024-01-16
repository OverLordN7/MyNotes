package com.overlord.mynotes.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.overlord.mynotes.R

class NotificationWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext,workerParams) {


    private val notificationDescriptions = listOf(
        "Draw your Ideas",
        "Keep the inspiration",
        "Write what you feel.",
        "Words — magic of thought.",
        "Record your dreams."
    )


    override fun doWork(): Result {
        //val noteNotification = NoteNotification(applicationContext)
        //val notificationManager = noteNotification.provideNotificationManager()
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(applicationContext,"Access to Notifications not Granted", Toast.LENGTH_LONG).show()
        }
        provideNotificationManager().notify(1,provideNotificationBuilder().build())

        //notificationManager.notify(1, noteNotification.provideNotificationBuilder().build() )

        return Result.success()
    }

    fun provideNotificationBuilder(): NotificationCompat.Builder{
        return NotificationCompat.Builder(applicationContext, "Primary Notification Channel")
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(notificationDescriptions.random())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    fun provideNotificationManager(): NotificationManagerCompat {
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val channel = NotificationChannel(
            "Primary Notification Channel",
            "Primary Channel",
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        notificationManager.createNotificationChannel(channel)
        return notificationManager
    }

    companion object {
        const val WORK_NAME = "NotificationWorker"
    }
}