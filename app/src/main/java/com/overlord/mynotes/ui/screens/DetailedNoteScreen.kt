package com.overlord.mynotes.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.graphicsLayer
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
import com.overlord.mynotes.ui.menu.MainAppBar
import com.overlord.mynotes.ui.menu.NoteModalDrawerSheet

private const val TAG = "DetailedNoteScreen"
@Composable
fun DetailedNoteScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    modifier: Modifier = Modifier,
) {
    val note: Note = if (noteViewModel.isNewNote){
        Note(title = "NewNote", description = "")
    }else{
        noteViewModel.currentNote
    }

    noteViewModel.currentNote = note

    val context = LocalContext.current

    //Drawer attributes
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    ModalNavigationDrawer(
        drawerContent = {
            NoteModalDrawerSheet(
                drawerState = drawerState,
                scope = scope,
                selectedItemIndex = selectedItemIndex,
                navController = navController
            )
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
                    note = note,
                    onSave = { modifiedNote ->
                             if (noteViewModel.isNewNote){
                                 noteViewModel.saveNote(modifiedNote)
                                 noteViewModel.isNewNote = false
                             } else{
                                 noteViewModel.updateNote(note)
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

    var title by remember { mutableStateOf(note.title) }
    var text by remember { mutableStateOf(note.description) }

    //Flag for Save Icon
    var isTitleModified by remember { mutableStateOf(false) }
    var isDescriptionModified by remember { mutableStateOf(false) }

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
                IconButton(onClick = {onShare(note)}) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }

                SaveIcon(
                    isUnsavedChanges = isTitleModified || isDescriptionModified,
                    onClick = { onSave(note) }
                )


//                IconButton(onClick = {onSave(note)}) {
//                    Icon(
//                        imageVector = Icons.Default.Save,
//                        contentDescription = null,
//                        modifier = Modifier.size(40.dp)
//                    )
//                }
            }
        }

        //Main Body
        Card(modifier = modifier.padding(4.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                //Title
                BasicTextField(
                    value = title ?: "",
                    onValueChange = { newTitle ->
                        title = newTitle
                        isTitleModified = true
                    },
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
                            isTitleModified = false
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
                                isTitleModified = false
                            }
                        }
                        .onKeyEvent {
                            if (it.key == Key.Back && it.type == KeyEventType.KeyDown) {
                                // Catching 'Back' Action
                                note.title = title
                                onSave(note)
                                isTitleModified = false
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                //Description
                BasicTextField(
                    value = text ?: "",
                    onValueChange = { newText ->
                        text = newText
                        isDescriptionModified = true
                    },
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
                            isDescriptionModified = false
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
                                isDescriptionModified = false
                            }
                        }
                        .onKeyEvent {
                            if (it.key == Key.Back && it.type == KeyEventType.KeyDown) {
                                // Catching 'Back' Action
                                note.description = text
                                onSave(note)
                                isDescriptionModified = false
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

@Composable
fun SaveIcon(isUnsavedChanges: Boolean, onClick: () -> Unit) {
    val shakeAnimation = rememberInfiniteTransition(label = "")
        .animateFloat(
            initialValue = 0f,
            targetValue = if (isUnsavedChanges) 5f else 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )

    if (isUnsavedChanges){
        CircularProgressIndicator(
            modifier = Modifier
                .size(40.dp)
                .padding(4.dp)
        )
    } else{
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .graphicsLayer(translationX = shakeAnimation.value)
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
    }


}