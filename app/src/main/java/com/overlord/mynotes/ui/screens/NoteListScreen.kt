package com.overlord.mynotes.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.overlord.mynotes.model.Note
import com.overlord.mynotes.model.drawerButtons
import com.overlord.mynotes.navigation.Screen
import com.overlord.mynotes.ui.menu.MainAppBar
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun NoteListScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    modifier: Modifier = Modifier
){
    val state: NotesUIState = noteViewModel.notesUIState

    //Drawer attributes
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                drawerButtons.forEachIndexed { index, drawerButton ->
                    NavigationDrawerItem(
                        label = { Text(text = drawerButton.title) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            navController.navigate(drawerButton.drawerOption)
                            selectedItemIndex = index
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(imageVector = drawerButton.icon, contentDescription = null)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        drawerState = drawerState
    ) {

        Scaffold (
            topBar = { MainAppBar(scope = scope, drawerState = drawerState) },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate(Screen.NewDetailedNoteScreen.route) }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                }
            }
        ) {
            Surface(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it),
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
                            },
                            onDelete = {noteToDelete ->
                                noteViewModel.deleteNote(noteToDelete)
                            }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun NoteGridView(
    notesList: List<Note>,
    onClick: (UUID) -> Unit,
    onDelete: (Note) -> Unit,
    modifier: Modifier = Modifier
){
    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(count = 2),
            modifier = modifier.padding(4.dp)
        ){
            items(
                items = notesList,
                key = { note -> note.id }
            ){ note ->
                Note(
                    note = note,
                    onClick = onClick,
                    onDelete = onDelete
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Note(
    note: Note,
    modifier: Modifier = Modifier,
    onClick: (UUID) -> Unit = {},
    onDelete: (Note) -> Unit,
){
    val showDeleteIcon = remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .height(200.dp)
            .padding(4.dp)
            .combinedClickable(
                onClick = { onClick(note.id) },
                onLongClick = { showDeleteIcon.value = !showDeleteIcon.value }
            )
    ) {
        Column {
            //Card Title
            Row {
                Box(
                    modifier = modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(4.dp)
                        .fillMaxWidth()
                ){
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = note.title ?: "empty title",
                            color = Color.White,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.weight(1.5f)
                        )

                        if (showDeleteIcon.value){
                            IconButton(
                                onClick = { onDelete(note) },
                                modifier = Modifier
                                    .weight(0.5f)
                                    .size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.White,
                                )
                            }
                        }
                    }
                }

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