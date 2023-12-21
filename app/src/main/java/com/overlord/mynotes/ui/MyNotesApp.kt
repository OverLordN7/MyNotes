package com.overlord.mynotes.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.overlord.mynotes.navigation.Screen
import com.overlord.mynotes.ui.screens.DetailedNoteScreen
import com.overlord.mynotes.ui.screens.NoteListScreen
import com.overlord.mynotes.ui.screens.NoteViewModel

@Composable
fun MyNotesApp(){

    val navController = rememberNavController()

    val noteViewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory)

    NavHost(navController = navController, startDestination = Screen.NotesScreen.route ){
        //Navigate to NotesScreen screen
        composable(route = Screen.NotesScreen.route){
            NoteListScreen(
                navController = navController,
                noteViewModel= noteViewModel,
            )
        }

        //Navigate to NotesScreen screen
        composable(route = Screen.DetailedNoteScreen.route){
            DetailedNoteScreen(
                navController = navController,
                noteViewModel = noteViewModel,
            )
        }

    }

}