package com.overlord.mynotes.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.overlord.mynotes.R
import kotlin.random.Random

class NoteNotification(private val context: Context) {

    private val notificationDescriptions = listOf(
        "Draw your Ideas",
        "Keep the inspiration",
        "Write what you feel.",
        "Words — magic of thought.",
        "Record your dreams."
    )

    fun provideNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "Primary Notification Channel")
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(notificationDescriptions.random())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    fun provideNotificationManager(): NotificationManagerCompat {
        val notificationManager = NotificationManagerCompat.from(context)
        val channel = NotificationChannel(
            "Primary Notification Channel",
            "Primary Channel",
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        notificationManager.createNotificationChannel(channel)
        return notificationManager
    }
}