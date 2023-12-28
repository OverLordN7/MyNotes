package com.overlord.mynotes.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.overlord.mynotes.model.Note
import com.overlord.mynotes.model.drawerButtons
import com.overlord.mynotes.ui.menu.MainAppBar
import kotlinx.coroutines.launch

@Composable
fun DetailedNoteScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    modifier: Modifier = Modifier,
) {
    val noteId = noteViewModel.currentId
    val note = noteViewModel.getNoteFromId(noteId)

    val context = LocalContext.current

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
    ){
        Scaffold (
            topBar = { MainAppBar(scope = scope, drawerState = drawerState) }
        ) {
            Surface(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it),
                color = MaterialTheme.colorScheme.background,
            ) {
                DetailedNoteView(
                    note = note!!,
                    onSave = { modifiedNote ->
                        if (noteViewModel.isPresent(modifiedNote.id)){
                            noteViewModel.updateNote(modifiedNote)
                        } else {
                            noteViewModel.saveNote(modifiedNote)
                        }
                    },
                    onBack = { navController.popBackStack() },
                    onShare = {noteViewModel.shareNote(note,context)}
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DetailedNoteView(
    note: Note,
    onSave: (Note) -> Unit,
    onBack: () -> Unit,
    onShare: (Note) -> Unit,
    modifier: Modifier = Modifier
) {

    //Keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    //Focus manager and Requester to clear focus when onDone keyboardAction happens
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    //Toast as TODO plug
    val context = LocalContext.current
    val toast = Toast.makeText(context, "In progress...", Toast.LENGTH_LONG)

    var title by remember { mutableStateOf(note.title) }
    var text by remember { mutableStateOf(note.description) }


    Column {
        //tool panel
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Row(modifier = Modifier.padding(start = 8.dp)) {
                IconButton(onClick = {
                    /*TODO draw functionality*/
                    toast.show()
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
                IconButton(onClick = {/*TODO trash*/}) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
                IconButton(onClick = {onShare(note)}) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        //Main Body
        Card(modifier = modifier.padding(4.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                //Title
                BasicTextField(
                    value = title ?: "",
                    onValueChange = { newTitle -> title = newTitle },
                    singleLine = true,
                    textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            note.title = title
                            onSave(note)
                            focusManager.moveFocus(FocusDirection.Next)
                        },
                    ),
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                // Save title when lose focus
                                note.title = title
                                onSave(note)
                            }
                        }
                        .onKeyEvent {
                            if (it.key == Key.Back && it.type == KeyEventType.KeyDown) {
                                // Catching 'Back' Action
                                note.title = title
                                onSave(note)
                                onBack()
                                return@onKeyEvent true
                            }
                            false
                        }
                )

                //Separate line
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                //Description
                BasicTextField(
                    value = text ?: "",
                    onValueChange = { newText -> text = newText },
                    singleLine = false,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            note.description = text
                            onSave(note)
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                // Save title when lose focus
                                note.description = text
                                onSave(note)
                            }
                        }
                        .onKeyEvent {
                            if (it.key == Key.Back && it.type == KeyEventType.KeyDown) {
                                // Catching 'Back' Action
                                note.description = text
                                onSave(note)
                                onBack()
                                return@onKeyEvent true
                            }
                            false
                        }
                )
            }
        }
    }
}

