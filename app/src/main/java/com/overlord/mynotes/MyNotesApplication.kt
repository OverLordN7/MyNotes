package com.overlord.mynotes

import android.app.Application
import com.overlord.mynotes.data.AppContainer
import com.overlord.mynotes.data.DefaultAppContainer

class MyNotesApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}