package com.overlord.mynotes.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.overlord.mynotes.R
import com.overlord.mynotes.model.drawerButtons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun NoteModalDrawerSheet (
    drawerState: DrawerState,
    scope: CoroutineScope,
    selectedItemIndex: Int,
    navController: NavController,
    onItemSelected: (Int) -> Unit,
){
    var selectedItemIndex1 by rememberSaveable { mutableIntStateOf(selectedItemIndex) }

    ModalDrawerSheet{
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        drawerButtons.forEachIndexed { index, drawerButton ->
            NavigationDrawerItem(
                label = { Text(text = drawerButton.title) },
                selected = index == selectedItemIndex1,
                onClick = {
                    navController.navigate(drawerButton.drawerOption)
                    onItemSelected(index)
                    selectedItemIndex1 = index
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
}