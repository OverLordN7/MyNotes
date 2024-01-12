package com.overlord.mynotes.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.overlord.mynotes.ui.menu.MainAppBar
import com.overlord.mynotes.ui.menu.NoteModalDrawerSheet


@Composable
fun SettingsScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    modifier: Modifier = Modifier
){

    //Drawer attributes
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    //Settings attributes
    val isDarkTheme by noteViewModel.isDarkThemeEnabled.collectAsState()
    val toggleTheme: (Boolean) -> Unit = { noteViewModel.setDarkThemeEnabled(it)}

    val isNotificationEnabled by noteViewModel.isNotificationEnabled.collectAsState()
    val toggleNotification: (Boolean) -> Unit = { noteViewModel.setNotificationEnabled(it) }

    val notificationTimeHours by noteViewModel.notificationTimeHours.collectAsState()
    val toggleNotificationTimeHours: (Int) -> Unit = { noteViewModel.setNotificationTimeHours(it)}

    val notificationTimeMinutes by noteViewModel.notificationTimeMinutes.collectAsState()
    val toggleNotificationTimeMinutes: (Int) -> Unit = { noteViewModel.setNotificationTimeMinutes(it)}


    val context = LocalContext.current

    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            mutableStateOf(true)
        }
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
        Scaffold (topBar = { MainAppBar(scope = scope, drawerState = drawerState)}) {
            Surface(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it),
                color = MaterialTheme.colorScheme.background,
            ) {
                LazyColumn() {
                    item{
                        DarkModeCard(isDarkTheme = isDarkTheme, toggleTheme = toggleTheme)
                    }
                    item {
                        NotificationCard(
                            isNotificationEnabled = isNotificationEnabled,
                            notificationTimeHours = notificationTimeHours,
                            notificationTimeMinutes = notificationTimeMinutes,
                            toggleNotification = toggleNotification,
                            onSubmit = {isEnabled,hours,minutes ->
                                toggleNotificationTimeHours(hours)
                                toggleNotificationTimeMinutes(minutes)
                                noteViewModel.setNotification(
                                    context = context,
                                    isNotificationsEnabled = isEnabled,
                                    hours = hours,
                                    minutes = minutes
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DarkModeCard(
    isDarkTheme: Boolean,
    toggleTheme: (Boolean) -> Unit,
    modifier: Modifier = Modifier
){
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ){
                Text(
                    text = "Appearance",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Dark theme mode:",
                    modifier = Modifier.weight(2f)
                )

                Spacer(modifier = Modifier.weight(2f))

                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = {toggleTheme(!isDarkTheme)},
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun NotificationCard(
    isNotificationEnabled: Boolean,
    notificationTimeHours: Int,
    notificationTimeMinutes: Int,
    toggleNotification: (Boolean) -> Unit,
    onSubmit: (Boolean, Int, Int) -> Unit,
    modifier: Modifier = Modifier
){
    val formattedHours = if (notificationTimeHours < 10) "0$notificationTimeHours" else "$notificationTimeHours"
    val formattedMinutes = if (notificationTimeMinutes < 10) "0$notificationTimeMinutes" else "$notificationTimeMinutes"

    val notificationTimeMessage = "Current notification time $formattedHours:$formattedMinutes"

    //Attribute for show TimeDialog
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value){
        TimeDialog(
            setShowDialog = {showDialog.value = it},
            onSubmit = {hours, minutes ->
                onSubmit(isNotificationEnabled,hours,minutes)
            }
        )
    }

    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ){
                Text(
                    text = "Notification",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Enable notification:",
                    modifier = Modifier.weight(2.1f)
                )

                Spacer(modifier = Modifier.weight(2f))

                Switch(
                    checked = isNotificationEnabled,
                    onCheckedChange = {
                        toggleNotification(it)
                        showDialog.value = it
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Text(text = notificationTimeMessage)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog(
    setShowDialog: (Boolean) -> Unit,
    onSubmit: (Int,Int) -> Unit,
    modifier: Modifier = Modifier
){
    val timeState = rememberTimePickerState(0,0,true)

    Dialog(onDismissRequest = {setShowDialog(false)}) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                Box(
                    modifier = modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    TimePicker(state = timeState)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {setShowDialog(false)}
                    ) {
                        Text(text = "Cancel")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                        onSubmit(timeState.hour,timeState.minute)
                        setShowDialog(false)
                    }) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }
}