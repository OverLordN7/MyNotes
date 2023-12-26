package com.overlord.mynotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.overlord.mynotes.model.Note
import java.util.UUID
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAllNotes(): List<Note>

    @Insert
    fun addNote(note: Note)

    @Query("DELETE FROM notes WHERE id = CAST(:noteId AS BLOB)")
    fun deleteNote(noteId: UUID)

    @Query("UPDATE notes SET title = :title, description= :description, isSelected= :isSelected  WHERE id = CAST(:noteId AS BLOB)")
    fun updateNote(noteId: UUID, title: String?,description: String?, isSelected: Boolean )

    @Query("SELECT * FROM notes WHERE id = CAST(:noteId AS BLOB)")
    fun getNote(noteId: UUID): Note
}