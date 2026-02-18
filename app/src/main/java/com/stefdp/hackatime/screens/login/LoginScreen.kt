package com.stefdp.hackatime.screens.login

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.hackatime.LocalLoggedUser
import com.stefdp.hackatime.LocalUpdateUserStats
import com.stefdp.hackatime.R
import com.stefdp.hackatime.components.Switch
import com.stefdp.hackatime.components.TextInput
import com.stefdp.hackatime.network.backendapi.requests.sendApiKey
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStats
import com.stefdp.hackatime.screens.HomeScreen
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.utils.SecureStorage
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (LocalLoggedUser.current is UserStats && currentDestination?.route == LoginScreen::class.qualifiedName) {
        navController.navigate(HomeScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
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
                    text = stringResource(R.string.login_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                TextInput(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    isPassword = true,
                    placeholder = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
                    label = stringResource(R.string.hackatime_api_key_input_label)
                )

                Switch(
                    modifier = Modifier.padding(top = 10.dp),
                    checked = shareApiKeyChecked,
                    onCheckedChange = { shareApiKeyChecked = it },
                    label = stringResource(R.string.share_api_key_with_server_switch_label),
                    description = stringResource(R.string.share_api_key_with_server_switch_description_login)
                )

                val coroutineScope = rememberCoroutineScope()
                val updateUserStats = LocalUpdateUserStats.current

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val secureStore = SecureStorage.getInstance(context)

                            secureStore.set("apiKey", apiKey.text)
                            secureStore.set("shareApiKey", shareApiKeyChecked.toString())

                            if (shareApiKeyChecked) {
                                val res = sendApiKey(
                                    context = context
                                )

                                if (!res) {
                                    secureStore.set("shareApiKey", "false")

                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.send_api_key_fail_message),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            val userStats = updateUserStats()

                            if (userStats != null && currentDestination?.route == LoginScreen::class.qualifiedName) {
                                navController.navigate(HomeScreen) {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.login_button),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                val noApiKeyText = buildAnnotatedString {
                    val rawString = stringResource(R.string.no_api_key_tooltip)
                    val linkTag = "[link]"
                    val linkEndTag = "[/link]"

                    val startIndex = rawString.indexOf(linkTag)
                    val endIndex = rawString.indexOf(linkEndTag)

                    if (startIndex != -1 && endIndex != -1) {
                        val cleanString = rawString.replace(linkTag, "").replace(linkEndTag, "")

                        append(cleanString)

                        addLink(
                            url = LinkAnnotation.Url(
                                url = "https://hackatime.hackclub.com/my/settings",
                                styles = TextLinkStyles(
                                    style = SpanStyle(color = MaterialTheme.colorScheme.primary)
                                )
                            ),
                            start = startIndex,
                            end = endIndex - linkTag.length
                        )
                    } else {
                        append(rawString)
                    }
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
                    append(stringResource(R.string.privacy_policy_tooltip))
                    pop()
                }

                Text(
                    text = privacyPolicyText,
                )
            }
        }
    }
}