package com.stefdp.hackatime

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.requests.getYSWSPrograms
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme
import com.stefdp.hackatime.utils.SecureStorage
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HackatimeStatsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )

//                    Button(
//                        onClick = {
//                            val l = listOf(Feature.OPERATING_SYSTEMS, Feature.CATEGORIES, Feature.MACHINES)
//                            Log.d("test", l.joinToString(","))
//                        }
//                    ) {
//                        Text("Test log")
//                    }

                    val scrollState = rememberScrollState()

                    // tests tryna see how to make HTTP requests
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .verticalScroll(scrollState)

                    ) {
                        Greeting(
                            name = "Android",
                        )

                        var programs by remember { mutableStateOf("") }
                        var programsCount by remember { mutableStateOf(0) }

                        var programs1 by remember { mutableStateOf("") }
                        var programsCount1 by remember { mutableStateOf(0) }

                        val context = LocalContext.current

                        LaunchedEffect(Unit) {
                            val res = getYSWSPrograms(context)
                            var secureStore = SecureStorage.getInstance(context)
                            secureStore.set("apiKey", "TEST")
                            Log.d("TEST", "API Key set to: ${secureStore.get("apiKey")}")

                            res.onSuccess {
                                val programsString = it.joinToString("\n- ")

//                                Log.d("Todo", "Programs:\n- $programsString")
                                programs = programsString

                                programsCount = it.size
                            }.onFailure {
                                Log.e("TEST", "Failed to fetch programs: ${it.message}")
                            }
                        }

                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result
                                Log.d("FCM", "Token: $token")
                            } else {
                                Log.e("FCM", "Failed to fetch token", task.exception)
                            }
                        }

                        if (programs.isNotEmpty()) {
                            Text("Boot programs:\n- $programs")
                        }

                        Text("Boot Programs Count: $programsCount")

                        if (programs1.isNotEmpty()) {
                            Text("Button programs:\n- $programs1")
                        }

                        Text("Button Programs Count: $programsCount1")

                        Button(
                            onClick = {
                                lifecycleScope.launch {
                                    val secureStorage = SecureStorage.getInstance(context)

                                    val apiKey = secureStorage.get("apiKey")

                                    if (apiKey != null) secureStorage.del("apiKey")
                                    else secureStorage.set("apiKey", "TEST123")

                                    val res = getYSWSPrograms(context)

                                     res.onSuccess {
                                        val programsString = it.joinToString("\n- ")

//                                Log.d("Todo", "Programs:\n- $programsString")
                                        programs1 = programsString

                                        programsCount1 = it.size
                                    }.onFailure {
                                        Log.e("TEST", "Failed to fetch programs: ${it.message}")
                                    }
                                }
                            }
                        ) {
                            Text("Test API")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HackatimeStatsTheme {
        Greeting("Android")
    }
}