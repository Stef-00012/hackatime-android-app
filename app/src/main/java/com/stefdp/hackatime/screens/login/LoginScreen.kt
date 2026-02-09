package com.stefdp.hackatime.screens.login

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.stefdp.hackatime.R
import com.stefdp.hackatime.components.TextInput
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserStats
import com.stefdp.hackatime.screens.HomeScreen
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme

@Composable
fun LoginScreen(
    navController: NavController,
    context: Context
) {
    LaunchedEffect(Unit) {
        Log.d("LoginScreen", "Checking if user is already logged in...")

        val userStatsRes = getCurrentUserStats(context)

        if (userStatsRes.isSuccess) {
            navController.navigate(HomeScreen) {
                popUpTo(HomeScreen) { inclusive = true }
            }

            Log.d("LoginScreen", "User is logged in as ${userStatsRes.getOrNull()?.username}")

            return@LaunchedEffect
        }

        Log.d("LoginScreen", "User is not logged in")
    }

//    Column(
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Login Screen1", color = Color.White)
//
//        Row {
//            Button(
//                onClick = {
//                    navController.navigate(HomeScreen) {
//                        popUpTo(HomeScreen) { inclusive = true }
//                    }
//                }
//            ) {
//                Text("Go to Home")
//            }
//        }
//
//        var apiKey by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
//
//        TextInput(
//            value = apiKey,
//            onValueChange = { apiKey = it },
//            label = "API Key",
//            isPassword = true
//        )
//    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(16.dp),
        ) {
            Column {
                var apiKey by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                TextInput(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    placeholder = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
                    label = "Hackatime API Key"
                )
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL or Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun LoginScreenPreview() {
    HackatimeStatsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp),
                            )
                            .padding(16.dp),
                    ) {
                        Column {
                            var apiKey by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

                            Text(
                                text = "Login",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            TextInput(
                                value = apiKey,
                                onValueChange = { apiKey = it },
                                placeholder = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
                                label = "Hackatime API Key"
                            )
                        }
                    }
                }
            }
        }
    }
}