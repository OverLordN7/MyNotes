package com.overlord.mynotes.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
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
    val toggleNotification: (Boolean) -> Unit = {noteViewModel.setNotificationEnabled(it)}

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
                            toggleNotificationTimeHours = toggleNotificationTimeHours,
                            toggleNotificationTimeMinutes = toggleNotificationTimeMinutes,
                            scheduleNotification = {noteViewModel.scheduleNotificationWorker(context)}
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCard(
    isNotificationEnabled: Boolean,
    notificationTimeHours: Int,
    notificationTimeMinutes: Int,
    toggleNotification: (Boolean) ->Unit,
    toggleNotificationTimeHours: (Int) -> Unit,
    toggleNotificationTimeMinutes: (Int) -> Unit,
    scheduleNotification: () -> Unit,
    modifier: Modifier = Modifier
){

    var isExpanded by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    val formattedHours = if (notificationTimeHours < 10) "0$notificationTimeHours" else "$notificationTimeHours"
    val formattedMinutes = if (notificationTimeMinutes < 10) "0$notificationTimeMinutes" else "$notificationTimeMinutes"

    val notificationTimeMessage = "Current notification time $formattedHours:$formattedMinutes"


    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(if (isExpanded && isNotificationEnabled) 550.dp else 120.dp)
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
                    onCheckedChange = {toggleNotification(!isNotificationEnabled)},
                    modifier = Modifier.weight(1f)
                )
            }

            Row() {
                Text(
                    text = notificationTimeMessage,
                    modifier = Modifier.weight(2f)
                )
                Spacer(modifier = Modifier.weight(0.5f))

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }

            if (isNotificationEnabled && isExpanded) {
                Box(
                    modifier = modifier
                        .padding(4.dp)
                        .weight(3f)
                        .border(2.dp, Color.Black)
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ){
                    TimePicker(state = timePickerState)
                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = {
                        toggleNotificationTimeHours(timePickerState.hour)
                        toggleNotificationTimeMinutes(timePickerState.minute)
                        scheduleNotification()
                    }) {
                        Text(text = "Save Time")
                    }
                }
            }
        }
    }
}