package com.overlord.mynotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DrawerValue
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.overlord.mynotes.model.Message
import com.overlord.mynotes.ui.menu.MainAppBar
import com.overlord.mynotes.ui.menu.NoteModalDrawerSheet

@Composable
fun GPTScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    modifier: Modifier = Modifier
){
    //Drawer attributes
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    //Dummies
    val fakeMessages = remember {
        listOf(
            Message("Hello, how can I help you?", isUserMessage = false),
            Message("Hi! I have a question about Android Compose.", isUserMessage = true),
            Message("Sure, feel free to ask!", isUserMessage = false),
            Message("How can I add a button to the screen?", isUserMessage = true),
            Message("You can use the `Button` composable in Android Compose.", isUserMessage = false),
            Message("Got it! Thanks for your help.", isUserMessage = true)
        )
    }

    ModalNavigationDrawer(
        drawerContent = {
            NoteModalDrawerSheet(
                drawerState = drawerState,
                scope = scope,
                selectedItemIndex = noteViewModel.selectedItemIndex.value,
                navController = navController,
                onItemSelected = {index -> noteViewModel.setSelectedIndex(index)}
            )
        },
        drawerState = drawerState,
    ){
        Scaffold (topBar = { MainAppBar(scope = scope, drawerState = drawerState) }) {
            Surface(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it),
                color = MaterialTheme.colorScheme.background,
            ){
                ChatScreen(
                    messagesList = fakeMessages,
                    onSendMessage = {},
                )
            }
        }
    }
}

@Composable
fun ChatScreen(
    messagesList: List<Message>,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat Messages
        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color.LightGray)
                .padding(8.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            LazyColumn {
                items(messagesList.size) { id ->
                    ChatMessage(messagesList[id])
                }
            }
        }

        // Input Field and Send Button
        var messageText by remember { mutableStateOf(TextFieldValue()) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            BasicTextField(
                value = messageText,
                onValueChange = {
                    messageText = it
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (messageText.text.isNotEmpty()) {
                            onSendMessage(messageText.text)
                            messageText = TextFieldValue()
                        }
                    }
                )
            )

            IconButton(
                onClick = {
                    if (messageText.text.isNotEmpty()) {
                        onSendMessage(messageText.text)
                        messageText = TextFieldValue()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message"
                )
            }
        }
    }
}

@Composable
fun ChatMessage(message: Message) {
    // You can customize the appearance of the chat messages here
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val backgroundColor = if (message.isUserMessage) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Gray
        }

        Text(
            text = message.text,
            modifier = Modifier
                .background(backgroundColor)
                .padding(8.dp)
                .clip(MaterialTheme.shapes.medium),
            color = if (message.isUserMessage) Color.White else Color.Black
        )
    }
}

