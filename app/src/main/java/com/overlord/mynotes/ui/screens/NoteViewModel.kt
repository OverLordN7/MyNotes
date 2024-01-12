package com.overlord.mynotes.ui.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.overlord.mynotes.MyNotesApplication
import com.overlord.mynotes.data.NoteRepository
import com.overlord.mynotes.model.Note
import com.overlord.mynotes.notification.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

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
    private val notifyKey = "notify_key"
    private val notificationTimeKeyHours = "notification_time_key_hours"
    private val notificationTimeKeyMinutes = "notification_time_key_minutes"
    private val sharedPreferences = application.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE)

    private val _isDarkThemeEnabled = MutableStateFlow(isDarkThemeEnabled())
    val isDarkThemeEnabled: StateFlow<Boolean> = _isDarkThemeEnabled

    private val _isNotificationEnabled = MutableStateFlow(isNotificationEnabled())
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled

    private val _notificationTimeHours = MutableStateFlow(getNotificationTimeHours())
    val notificationTimeHours: StateFlow<Int> = _notificationTimeHours

    private val _notificationTimeMinutes = MutableStateFlow(getNotificationTimeMinutes())
    val notificationTimeMinutes: StateFlow<Int> = _notificationTimeMinutes

    //Attribute for switching ModalNavigationDrawer
    private val _selectedItemIndex = mutableIntStateOf(0)
    val selectedItemIndex: State<Int> = _selectedItemIndex


    init { getNotes() }


    //Settings methods

    private fun isDarkThemeEnabled(): Boolean{
        return sharedPreferences.getBoolean(themeKey,false)
    }

    fun setDarkThemeEnabled(isEnabled: Boolean){
        _isDarkThemeEnabled.value = isEnabled
        sharedPreferences.edit().putBoolean(themeKey,isEnabled).apply()
    }

    private fun isNotificationEnabled():Boolean{
        return sharedPreferences.getBoolean(notifyKey, false)
    }

    fun setNotificationEnabled(isEnabled: Boolean){
        _isNotificationEnabled.value = isEnabled
        sharedPreferences.edit().putBoolean(notifyKey, isEnabled).apply()
    }

    private fun getNotificationTimeHours(): Int{
        return sharedPreferences.getInt(notificationTimeKeyHours,0)
    }

    fun setNotificationTimeHours(hours: Int){
        _notificationTimeHours.value = hours
        sharedPreferences.edit().putInt(notificationTimeKeyHours,hours).apply()
    }

    private fun getNotificationTimeMinutes(): Int{
        return sharedPreferences.getInt(notificationTimeKeyMinutes,0)
    }

    fun setNotificationTimeMinutes(minutes: Int){
        _notificationTimeMinutes.value = minutes
        sharedPreferences.edit().putInt(notificationTimeKeyMinutes,minutes).apply()
    }


    fun cancelNotification(context: Context){
        WorkManager.getInstance(context).cancelUniqueWork(NotificationWorker.WORK_NAME)
    }

    fun setNotification(
        context: Context,
        isNotificationsEnabled: Boolean = isNotificationEnabled.value,
        hours: Int = notificationTimeHours.value,
        minutes: Int = notificationTimeMinutes.value
    ){
        if (isNotificationsEnabled){
            //Notifications are turned on
            Log.d(TAG, "Scheduling notification work...")
            scheduleNotificationWorker(context, hours, minutes)
        } else{
            //Notifications are turned off
            Log.d(TAG, "Cancel notification work...")
            cancelNotification(context)
        }
    }



    private fun scheduleNotificationWorker(
        context: Context,
        hours: Int = notificationTimeHours.value,
        minutes: Int = notificationTimeMinutes.value
    ){
        val currentTimeMillis = System.currentTimeMillis()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeMillis
        calendar.set(Calendar.HOUR_OF_DAY,hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND,0)

        //if current time is gone, shift on 1 day more
        if (calendar.timeInMillis <= currentTimeMillis){
            calendar.add(Calendar.DAY_OF_YEAR,1)
        }

        val initialDelay = calendar.timeInMillis - currentTimeMillis

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        )
            //.setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NotificationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }


    //Methods for Drawer
    fun setSelectedIndex(index: Int){
        _selectedItemIndex.intValue = index
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
                    application = application,
                )
            }
        }
    }
}