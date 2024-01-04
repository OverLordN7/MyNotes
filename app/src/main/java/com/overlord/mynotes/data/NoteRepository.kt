package com.overlord.mynotes.data

import android.util.Log
import com.overlord.mynotes.model.Note
import java.util.UUID

interface NoteRepository {
    suspend fun getAllNotes(): List<Note>

    suspend fun insertNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(note: Note)

    suspend fun getNote(noteId: UUID): Note
}

class DefaultNoteRepository(
    private val noteDao: NoteDao,
): NoteRepository{
    override suspend fun getAllNotes(): List<Note> {
        return noteDao.getAllNotes()
    }

    override suspend fun insertNote(note: Note) {
        noteDao.addNote(note)
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(
            noteId = note.id,
            title = note.title,
            description = note.description,
            isSelected = note.isSelected
        )
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.id)
    }

    override suspend fun getNote(noteId: UUID): Note {
        return noteDao.getNote(noteId)
    }
}