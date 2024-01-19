package com.overlord.mynotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.overlord.mynotes.model.Note
import java.util.UUID
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAllNotes(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNote(note: Note)

    @Query("INSERT INTO notes (id,title,description,isSelected) VALUES (CAST(:id AS BLOB),:title,:description,:isSelected)")
    fun addNoteAlternative(id: UUID, title: String?, description: String?,isSelected: Boolean)

    @Query("DELETE FROM notes WHERE id = CAST(:noteId AS BLOB)")
    fun deleteNote(noteId: UUID)

    @Query("UPDATE notes SET title = :title, description= :description, isSelected= :isSelected  WHERE id = CAST(:noteId AS BLOB)")
    fun updateNote(noteId: UUID, title: String?,description: String?, isSelected: Boolean )

    @Query("SELECT * FROM notes WHERE id = CAST(:noteId AS BLOB)")
    fun getNote(noteId: UUID): Note

    @Query("SELECT * FROM notes WHERE title LIKE :searchQuery OR description LIKE :searchQuery")
    suspend fun searchNotes(searchQuery: String): List<Note>
}