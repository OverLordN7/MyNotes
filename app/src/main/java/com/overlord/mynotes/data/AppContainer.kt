package com.overlord.mynotes.data

import android.content.Context

interface AppContainer {
    val noteRepository: NoteRepository
}

class DefaultAppContainer(private val applicationContext: Context): AppContainer{
    private val database by lazy {
        NoteDatabase.getInstance(applicationContext)
    }

    private val noteDao by lazy { database.noteDao() }

    override val noteRepository: NoteRepository by lazy {
        DefaultNoteRepository(noteDao)
    }
}