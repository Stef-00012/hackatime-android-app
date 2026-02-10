package com.stefdp.hackatime.screens.goals

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.stefdp.hackatime.R
import com.stefdp.hackatime.screens.HomeScreen
import com.stefdp.hackatime.screens.SettingsScreen

@Composable
fun GoalsScreen(
    navController: NavHostController,
    context: Context
) {
    Column {
        Text("Goals Screen", color = Color.White)

        Row {
            Button(
                onClick = {
                    navController.navigate(HomeScreen) {
                        popUpTo(HomeScreen) { inclusive = true }
                    }
                }
            ) {
                Text("Go to Home")
            }


        }
    }
}