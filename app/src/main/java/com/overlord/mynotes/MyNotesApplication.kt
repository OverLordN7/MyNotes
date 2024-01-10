package com.overlord.mynotes

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.overlord.mynotes.data.AppContainer
import com.overlord.mynotes.data.DefaultAppContainer

class MyNotesApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "channed_id",
            "channel_name",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        container = DefaultAppContainer(this)
    }
}