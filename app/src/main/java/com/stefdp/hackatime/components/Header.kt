package com.stefdp.hackatime.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import com.stefdp.hackatime.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.hackatime.LocalLoggedUser
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.screens.SettingsScreen

@Composable
fun Header(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (currentDestination?.route == LoginScreen::class.qualifiedName) return

    Surface(
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth().height(90.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp).statusBarsPadding()
        ) {
            // NOTE: This is just a test for a sidebar, i'll probably use navbar instead of this cuz it looks better
//            IconButton(onClick = onMenuClick) {
//                Icon(painter = painterResource(R.drawable.menu_icon), contentDescription = "Open Sidebar")
//            }
//            Spacer(modifier = Modifier.width(16.dp))

            val username = LocalLoggedUser.current?.username

            Text(
                text = username ?: "Unknown Username",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val isInSettings = currentDestination?.route == SettingsScreen::class.qualifiedName

                IconButton(
                    enabled = !isInSettings,
                    modifier = Modifier.border(
                        width = 2.dp,
                        shape = RoundedCornerShape(CornerSize(10.dp)),
                        color = if (isInSettings) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary
                    ),
                    onClick = {
                        navController.navigate(SettingsScreen) {
                            popUpTo(SettingsScreen) { inclusive = true }
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.settings),
                        contentDescription = "Settings",
                        tint = if (isInSettings) MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onTertiary,
                    )
                }
            }
        }
    }
}