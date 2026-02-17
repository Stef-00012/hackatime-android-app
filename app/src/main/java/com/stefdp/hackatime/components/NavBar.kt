package com.stefdp.hackatime.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.hackatime.R
import com.stefdp.hackatime.screens.GoalsScreen
import com.stefdp.hackatime.screens.HomeScreen
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.screens.ProjectsScreen

@Composable
fun NavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (currentDestination?.route == LoginScreen::class.qualifiedName) return

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        val navBarItemColors = NavigationBarItemDefaults.colors(
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledIconColor = MaterialTheme.colorScheme.primary,
            disabledTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.home),
                    contentDescription = stringResource(R.string.home_button_text)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.home_button_text),
                    fontWeight = FontWeight.Bold
                )
            },
            selected = currentDestination?.route == HomeScreen::class.qualifiedName,
            enabled = currentDestination?.route != HomeScreen::class.qualifiedName,
            onClick = { navController.navigate(HomeScreen) },
            colors = navBarItemColors
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.dashboard),
                    contentDescription = stringResource(R.string.projects_button_text)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.projects_button_text),
                    fontWeight = FontWeight.Bold
                )
            },
            selected = currentDestination?.route == ProjectsScreen::class.qualifiedName,
            enabled = currentDestination?.route != ProjectsScreen::class.qualifiedName,
            onClick = { navController.navigate(ProjectsScreen) },
            colors = navBarItemColors
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.flag),
                    contentDescription = stringResource(R.string.goals_button_text)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.goals_button_text),
                    fontWeight = FontWeight.Bold
                )
            },
            selected = currentDestination?.route == GoalsScreen::class.qualifiedName,
            enabled = currentDestination?.route != GoalsScreen::class.qualifiedName,
            onClick = { navController.navigate(GoalsScreen) },
            colors = navBarItemColors
        )
    }
}