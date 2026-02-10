package com.stefdp.hackatime.screens.login

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.stefdp.hackatime.components.Switch
import com.stefdp.hackatime.components.TextInput
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserStats
import com.stefdp.hackatime.screens.HomeScreen
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme
import com.stefdp.hackatime.utils.SecureStorage
import kotlinx.coroutines.launch

private suspend fun checkuserLoginStatus(
    context: Context,
    navController: NavHostController
) {
    Log.d("LoginScreen", "Checking if user is already logged in...")

    val userStatsRes = getCurrentUserStats(context)

    if (userStatsRes.isSuccess) {
        navController.navigate(HomeScreen) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }

        Log.d("LoginScreen", "User is logged in as ${userStatsRes.getOrNull()?.username}")

        return
    }

    Log.d("LoginScreen", "User is not logged in")

    return
}

@Composable
fun LoginScreen(
    navController: NavHostController,
    context: Context
) {
    LaunchedEffect(Unit) {
        checkuserLoginStatus(context, navController)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(16.dp),
        ) {
            Column {
                var apiKey by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
                var shareApiKeyChecked by rememberSaveable { mutableStateOf(false) }

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                TextInput(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    isPassword = true,
                    placeholder = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
                    label = "Hackatime API Key"
                )

                Switch(
                    modifier = Modifier.padding(top = 10.dp),
                    checked = shareApiKeyChecked,
                    onCheckedChange = { shareApiKeyChecked = !shareApiKeyChecked },
                    label = "Share API key with the app's server",
                    description = "If you don't share the API key, you won't be able to receive push notifications or use the goals feature"
                )

                val coroutineScope = rememberCoroutineScope()

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val secureStore = SecureStorage.getInstance(context)

                            secureStore.set("apiKey", apiKey.text)
                            secureStore.set("shareApiKey", if (shareApiKeyChecked) "true" else "false")

                            checkuserLoginStatus(context, navController)
                        }
                    }
                ) {
                    Text(
                        text = "Login",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                val noApiKeyText = buildAnnotatedString {
                    append("Don't have an API key? Get it ")

                    pushLink(
                        LinkAnnotation.Url(
                            url = "https://hackatime.hackclub.com/my/settings",
                           styles = TextLinkStyles(
                               style = SpanStyle(color = MaterialTheme.colorScheme.primary)
                           )
                        )
                    )
                    append("here")
                    pop()
                }

                Text(
                    text = noApiKeyText,
                )

                val privacyPolicyText = buildAnnotatedString {
                    pushLink(
                        LinkAnnotation.Url(
                            url = "https://hackatime.stefdp.com/privacy",
                            styles = TextLinkStyles(
                                style = SpanStyle(color = MaterialTheme.colorScheme.primary)
                            )
                        )
                    )
                    append("Privacy Policy")
                    pop()
                }

                Text(
                    text = privacyPolicyText,
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
                            .fillMaxWidth(0.9f)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp),
                            )
                            .padding(16.dp),
                    ) {
                        Column {
                            var apiKey by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
                            var shareApiKeyChecked by rememberSaveable { mutableStateOf(false) }

                            Text(
                                text = "Login",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            TextInput(
                                value = apiKey,
                                onValueChange = { apiKey = it },
                                isPassword = true,
                                placeholder = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
                                label = "Hackatime API Key"
                            )

                            Switch(
                                modifier = Modifier.padding(top = 10.dp),
                                checked = shareApiKeyChecked,
                                onCheckedChange = { shareApiKeyChecked = !shareApiKeyChecked },
                                label = "Share API key with the app's server",
                                description = "If you don't share the API key, you won't be able to receive push notifications or use the goals feature"
                            )

                            Button(
                                onClick = { /* TODO: Implement login logic */ }
                            ) {
                                Text(
                                    text = "Login",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                            val noApiKeyText = buildAnnotatedString {
                                append("Don't have an API key? Get it ")

                                pushLink(
                                    LinkAnnotation.Url(
                                        url = "https://hackatime.hackclub.com/my/settings",
                                        styles = TextLinkStyles(
                                            style = SpanStyle(color = MaterialTheme.colorScheme.primary)
                                        )
                                    )
                                )
                                append("here")
                                pop()
                            }

                            Text(
                                text = noApiKeyText,
                            )

                            val privacyPolicyText = buildAnnotatedString {
                                pushLink(
                                    LinkAnnotation.Url(
                                        url = "https://hackatime.stefdp.com/privacy",
                                        styles = TextLinkStyles(
                                            style = SpanStyle(color = MaterialTheme.colorScheme.primary)
                                        )
                                    )
                                )
                                append("Privacy Policy")
                                pop()
                            }

                            Text(
                                text = privacyPolicyText,
                            )
                        }
                    }
                }
            }
        }
    }
}