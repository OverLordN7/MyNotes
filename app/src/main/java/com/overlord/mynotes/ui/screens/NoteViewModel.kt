package com.overlord.mynotes.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.overlord.mynotes.MyNotesApplication
import com.overlord.mynotes.data.NoteRepository
import com.overlord.mynotes.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

private const val TAG = "NoteViewModel"

sealed interface NotesUIState{
    data class Success(var notesList: List<Note>): NotesUIState
    data class Error(var errorMessage: Exception): NotesUIState
    object Loading: NotesUIState
}
class NoteViewModel(private val noteRepository: NoteRepository): ViewModel() {

    var notesUIState: NotesUIState by mutableStateOf(NotesUIState.Loading)

    var currentId: UUID by mutableStateOf(UUID.randomUUID())

    private var noteList: List<Note> by mutableStateOf(emptyList())


    init { getNotes() }

    private suspend fun getAllNotes(): List<Note>{
        return withContext(Dispatchers.IO){
            noteRepository.getAllNotes()
        }
    }

    fun getNotes(){
        viewModelScope.launch {
            notesUIState = NotesUIState.Loading
            notesUIState = try{
                //make a copy of notes
                noteList = getAllNotes()
                NotesUIState.Success(getAllNotes())
            }catch (e: Exception){
                NotesUIState.Error(e)
            }
        }
    }
    fun getNoteFromId(noteId: UUID?): Note? { return noteList.find { it.id == noteId } }
    fun saveNote(note: Note){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                noteRepository.insertNote(note)
            }
            //Refresh view
            getNotes()
        }
    }

    fun deleteNote(note: Note){
        viewModelScope.launch {
            withContext(Dispatchers.IO){ noteRepository.deleteNote(note) }
            //Refresh view
            getNotes()
        }
    }

    fun isPresent(id: UUID): Boolean{
        return noteList.any { it.id == id }
    }

    fun updateNote(note: Note){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                noteRepository.updateNote(note)
            }
        }
            //Refresh view
            getNotes()
    }

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MyNotesApplication)
                val noteRepository = application.container.noteRepository
                NoteViewModel(noteRepository)
            }
        }
    }
}