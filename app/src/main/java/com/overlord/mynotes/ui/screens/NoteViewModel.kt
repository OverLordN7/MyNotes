package com.overlord.mynotes.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "NoteViewModel"

sealed interface NotesUIState{
    data class Success(var notesList: List<Note>): NotesUIState
    data class Error(var errorMessage: Exception): NotesUIState
    object Loading: NotesUIState
}
class NoteViewModel(
    private val noteRepository: NoteRepository,
    application: MyNotesApplication,
): ViewModel() {

    var notesUIState: NotesUIState by mutableStateOf(NotesUIState.Loading)
    var currentNote: Note by mutableStateOf(Note(title = "Generated Title", description = ""))
    var isNewNote: Boolean by mutableStateOf(false)

    //Attributes of sharedPreferences
    private val sharedPrefFileName = "my_pref_file_name"
    private val themeKey = "theme_key"
    private val sharedPreferences = application.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE)

    private val _isDarkThemeEnabled = MutableStateFlow(isDarkThemeEnabled())
    val isDarkThemeEnabled: StateFlow<Boolean> = _isDarkThemeEnabled


    init { getNotes() }

    private fun isDarkThemeEnabled(): Boolean{
        return sharedPreferences.getBoolean(themeKey,false)
    }

    fun setDarkThemeEnabled(isEnabled: Boolean){
        _isDarkThemeEnabled.value = isEnabled
        sharedPreferences.edit().putBoolean(themeKey,isEnabled).apply()
    }

    private suspend fun getAllNotes(): List<Note>{
        return withContext(Dispatchers.IO){
            val unsortedNotes = noteRepository.getAllNotes()
            return@withContext unsortedNotes.sortedBy { it.creationTimeMillis }.reversed()
        }
    }

    private fun getNotes(){
        viewModelScope.launch {
            notesUIState = NotesUIState.Loading
            notesUIState = try{
                NotesUIState.Success(getAllNotes())
            }catch (e: Exception){
                NotesUIState.Error(e)
            }
        }
    }
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

    fun updateNote(note: Note){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                noteRepository.updateNote(note)
            }
        }
            //Refresh view
            getNotes()
    }

    fun shareNote(note: Note, context: Context){
        viewModelScope.launch {
            val message = "${note.title}\n${note.description}"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT,"share")
            intent.putExtra(Intent.EXTRA_TEXT,message)
            context.startActivity(Intent.createChooser(intent,"Share note with:"))
        }
    }

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MyNotesApplication)
                val noteRepository = application.container.noteRepository
                NoteViewModel(
                    noteRepository = noteRepository,
                    application = application
                )
            }
        }
    }
}