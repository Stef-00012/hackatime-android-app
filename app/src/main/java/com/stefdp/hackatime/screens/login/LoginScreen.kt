package com.stefdp.hackatime.screens.login

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.hackatime.LocalLoggedUser
import com.stefdp.hackatime.LocalUpdateUserStats
import com.stefdp.hackatime.components.Switch
import com.stefdp.hackatime.components.TextInput
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStats
import com.stefdp.hackatime.screens.HomeScreen
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.utils.SecureStorage
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    context: Context
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (LocalLoggedUser.current is UserStats && currentDestination?.route == LoginScreen::class.qualifiedName) {
        navController.navigate(HomeScreen) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
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
                val updateUserStats = LocalUpdateUserStats.current

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val secureStore = SecureStorage.getInstance(context)

                            secureStore.set("apiKey", apiKey.text)
                            secureStore.set("shareApiKey", if (shareApiKeyChecked) "true" else "false")

                            val userStats = updateUserStats()

                            if (userStats != null && currentDestination?.route == LoginScreen::class.qualifiedName) {
                                navController.navigate(HomeScreen) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
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