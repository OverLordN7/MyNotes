package com.overlord.mynotes.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.overlord.mynotes.navigation.Screen

data class DrawerItem<T>(
    val id: String,
    val drawerOption: T,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector
)

val drawerButtons = listOf(
    DrawerItem(
        id = "home",
        drawerOption = Screen.NotesScreen.route,
        title = "Home",
        contentDescription = "",
        icon = Icons.Default.Home
    ),
    DrawerItem(
        id = "settings",
        drawerOption = Screen.Settings.route,
        title = "Settings",
        contentDescription = "",
        icon = Icons.Default.Settings
    ),
)