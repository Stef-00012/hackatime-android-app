package com.stefdp.hackatime.screens.home

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.screens.SettingsScreen

@Composable
fun HomeScreen(
    navController: NavHostController,
    context: Context
) {
    Column {
        Text("Home Screen", color = Color.White)

        Button(
            onClick = {
                navController.navigate(LoginScreen) {
                    popUpTo(LoginScreen) { inclusive = true }
                }
            }
        ) {
            Text("Go back")
        }

        Button(
            onClick = {
                navController.navigate(SettingsScreen) {
                    popUpTo(SettingsScreen) { inclusive = true }
                }
            }
        ) {
            Text("Go to Settings")
        }
    }
}