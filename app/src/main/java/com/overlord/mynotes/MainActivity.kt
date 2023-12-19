package com.overlord.mynotes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.overlord.mynotes.ui.theme.MyNotesTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyNotesTheme {
                // A surface container using the 'background' color from the theme
                NoteScreen()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(modifier: Modifier = Modifier) {

    var title by remember { mutableStateOf("note Title") }
    var text by remember { mutableStateOf("") }

    //Keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    //Focus manager and Requester to clear focus when onDone keyboardAction happens
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    //Toast as TODO plug
    val context = LocalContext.current
    val toast = Toast.makeText(context, "In progress...", Toast.LENGTH_LONG)

    Scaffold (
        topBar = { TopAppBar(title = {Text(stringResource(id = R.string.app_name))})
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().padding(it),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column{
                //tool panel
                Card(
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ){
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
                        IconButton(onClick = {
                                /*TODO add image to note*/
                                toast.show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        IconButton(onClick = {
                                /*TODO sharing function*/
                                toast.show()
                        }) {
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
                            value = title,
                            onValueChange = {newTitle -> title = newTitle},
                            singleLine = true,
                            textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        )

                        //Description
                        BasicTextField(
                            value = text,
                            onValueChange = { newText -> text = newText},
                            singleLine = false,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions (
                                onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            ),
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .focusRequester(focusRequester)
                        )
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyNotesTheme {
        NoteScreen()
    }
}