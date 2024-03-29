package com.overlord.mynotes.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.overlord.mynotes.model.Note
import com.overlord.mynotes.navigation.Screen
import com.overlord.mynotes.ui.menu.MainAppBar
import com.overlord.mynotes.ui.menu.NoteModalDrawerSheet
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "NoteListScreen"
@Composable
fun NoteListScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    modifier: Modifier = Modifier
){

    //State attribute for receiving list of notes
    val state: NotesUIState = noteViewModel.notesUIState

    //Drawer attributes
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    ModalNavigationDrawer(
        drawerContent = {
            NoteModalDrawerSheet(
                drawerState = drawerState,
                scope = scope,
                selectedItemIndex = selectedItemIndex,
                navController = navController,
                onItemSelected = {index -> noteViewModel.setSelectedIndex(index)}
            )
        },
        drawerState = drawerState
    ) {

        Scaffold (
            topBar = { MainAppBar(scope = scope, drawerState = drawerState) },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    scope.launch {
                        noteViewModel.isNewNote = true
                        navController.navigate(Screen.DetailedNoteScreen.route)
                    }
                }) {
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
                    is NotesUIState.Loading -> LoadingScreen()
                    is NotesUIState.Error ->{}
                    is NotesUIState.Success ->{
                        NoteGridView(
                            notesList = state.notesList,
                            onClick = {  note ->
                                noteViewModel.currentNote = note
                                noteViewModel.isNewNote = false
                                navController.navigate(Screen.DetailedNoteScreen.route)
                            },
                            onDeleteList = {list->
                                list.forEach { note->
                                    noteViewModel.deleteNote(note)
                                }
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
    onClick: (Note) -> Unit,
    onDeleteList: (List<Note>) -> Unit,
    modifier: Modifier = Modifier
){
    //Tool panel attribute
    val isToolPanelShown = remember { mutableStateOf(false) }
    val selectedNotes = remember { mutableStateListOf<Note>() }

    if (notesList.isEmpty()){
        EmptyScreen()
    } else{
        Column {
            if (isToolPanelShown.value){
                Row(modifier = Modifier
                    .padding(4.dp)) {
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)) {
                        IconButton(onClick = {
                            //Try to delete all notes which are selected
                            isToolPanelShown.value = false
                            onDeleteList(selectedNotes)
                            selectedNotes.clear()

                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }
            }

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
                        checkboxState = isToolPanelShown.value,
                        onClick = onClick,
                        onLongClick = {
                            //Show or hide tool panel
                            isToolPanelShown.value = !isToolPanelShown.value
                        },
                        onCheckBoxChange = {
                            if (selectedNotes.contains(it)){
                                selectedNotes.remove(it)
                            } else{
                                selectedNotes.add(it)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Note(
    note: Note,
    checkboxState: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Note) -> Unit = {},
    onLongClick: () -> Unit,
    onCheckBoxChange: (Note) -> Unit,
){
    val checkboxValue = remember { mutableStateOf(false) }

    val creationDate = remember {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        dateFormat.format(Date(note.creationTimeMillis))
    }

    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .height(200.dp)
            .padding(4.dp)
            .combinedClickable(
                onClick = { onClick(note) },
                onLongClick = onLongClick
            )
    ) {
        Column {
            //Card Title
            Row(modifier = Modifier.weight(1f)) {
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

                        if (checkboxState){
                            Checkbox(
                                checked = checkboxValue.value,
                                onCheckedChange = {
                                    checkboxValue.value = it
                                    onCheckBoxChange(note)
                                },
                                modifier = Modifier
                                    .size(20.dp)
                                    .weight(0.5f)
                            )
                        }
                    }
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .weight(5f)
            ) {
                Text(
                    text = note.description ?: "",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 5,
                )
            }
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .weight(1f)
            ) {
                Text(text = creationDate, fontStyle = FontStyle.Italic)
            }
        }
    }
}

@Composable
fun EmptyScreen(modifier: Modifier = Modifier){
    Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()){
        Text(
            text = "No notes to display\nTry '+' button, to add new Note",
            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
        CircularProgressIndicator(modifier = Modifier.size(150.dp))
    }
}