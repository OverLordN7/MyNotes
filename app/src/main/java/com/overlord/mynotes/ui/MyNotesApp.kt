package com.overlord.mynotes.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.overlord.mynotes.navigation.Screen
import com.overlord.mynotes.ui.screens.DetailedNoteScreen
import com.overlord.mynotes.ui.screens.GPTScreen
import com.overlord.mynotes.ui.screens.NoteListScreen
import com.overlord.mynotes.ui.screens.NoteViewModel
import com.overlord.mynotes.ui.screens.SettingsScreen
import com.overlord.mynotes.ui.theme.MyNotesTheme

@Composable
fun MyNotesApp(){

    val navController = rememberNavController()

    val noteViewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory)

    //Dark theme attribute
    val isDarkTheme by noteViewModel.isDarkThemeEnabled.collectAsState()

    MyNotesTheme(darkTheme = isDarkTheme) {
        NavHost(navController = navController, startDestination = Screen.NotesScreen.route ){
            //Navigate to NotesScreen screen
            composable(route = Screen.NotesScreen.route){
                NoteListScreen(
                    navController = navController,
                    noteViewModel= noteViewModel,
                )
            }

            //Navigate to DetailedNotesScreen screen
            composable(route = Screen.DetailedNoteScreen.route){
                DetailedNoteScreen(
                    navController = navController,
                    noteViewModel = noteViewModel,
                )
            }
            //Navigate to GPT screen
            composable(route = Screen.GPTScreen.route){
                GPTScreen(
                    navController = navController,
                    noteViewModel = noteViewModel
                )
            }


            //Navigate to Settings screen
            composable(route = Screen.Settings.route){
                SettingsScreen(
                    navController = navController,
                    noteViewModel = noteViewModel,
                )
            }
        }
    }
}