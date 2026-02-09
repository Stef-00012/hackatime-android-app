package com.stefdp.hackatime.screens.settings

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.stefdp.hackatime.screens.LoginScreen

@Composable
fun SettingsScreen(
    navController: NavController,
    context: Context
) {
    Column {
        Text("Settings Screen", color = Color.White)

        Button(
            onClick = {
                navController.navigate(LoginScreen) {
                    popUpTo(LoginScreen) { inclusive = true }
                }
            }
        ) {
            Text("Go back")
        }
    }
}