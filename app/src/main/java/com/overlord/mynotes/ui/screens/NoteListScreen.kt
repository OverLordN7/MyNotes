package com.overlord.mynotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.overlord.mynotes.model.Note
import com.overlord.mynotes.navigation.Screen
import com.overlord.mynotes.ui.menu.MainAppBar
import java.util.UUID

@Composable
fun NoteListScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    modifier: Modifier = Modifier
){

    val state: NotesUIState = noteViewModel.notesUIState

    Scaffold (
        topBar = { MainAppBar(onNavigationClick = {}) }
    ) {
        Surface(
            modifier = modifier.fillMaxSize().padding(it),
            color = MaterialTheme.colorScheme.background,
        ) {
            when(state){
                is NotesUIState.Loading ->{}
                is NotesUIState.Error ->{}
                is NotesUIState.Success ->{
                    NoteGridView(
                        notesList = state.notesList,
                        onClick = {  noteId ->
                        noteViewModel.currentId = noteId
                        navController.navigate(Screen.DetailedNoteScreen.route)
                        }
                    )
                }
            }

        }
    }

}
@Composable
fun NoteGridView(
    notesList: List<Note>,
    onClick: (UUID) -> Unit,
    modifier: Modifier = Modifier){
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 3),
        modifier = modifier.padding(4.dp)
    ){
        items(count = notesList.size){id->
            Note(notesList[id], onClick = onClick)
        }
    }
}

@Composable
fun Note(
    note: Note,
    modifier: Modifier = Modifier,
    onClick: (UUID) -> Unit = {}
){
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .height(200.dp)
            .padding(4.dp)
            .clickable { onClick(note.id) }
    ) {
        Column {
            //Card Title
            Box(modifier = modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(4.dp)
                .fillMaxWidth()
            ){
                Text(
                    text = note.title ?: "empty title",
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
            ) {
                Text(
                    text = note.description ?: "",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 6,
                )
            }
        }
    }
}