package com.stefdp.hackatime.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.stefdp.hackatime.screens.AppScreen
import com.stefdp.hackatime.screens.HomeScreen
import com.stefdp.hackatime.screens.SettingsScreen

// NOTE: This is just a test for a sidebar, i'll probably use navbar instead of this cuz it looks better

@Composable
fun Sidebar(onItemClick: (AppScreen) -> Unit) {
    ModalDrawerSheet {
//        Spacer(Modifier.height(12.dp))
//        Text("Sidebar Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
//        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text("Home") },
            selected = false,
            onClick = { onItemClick(HomeScreen) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Settings") },
            selected = false,
            onClick = { onItemClick(SettingsScreen) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}