package com.stefdp.hackatime.screens.login

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import com.stefdp.hackatime.components.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.hackatime.LocalLoggedUser
import com.stefdp.hackatime.LocalUpdateUserStats
import com.stefdp.hackatime.R
import com.stefdp.hackatime.components.Switch
import com.stefdp.hackatime.network.backendapi.requests.sendApiKey
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStats
import com.stefdp.hackatime.screens.HomeScreen
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.ui.theme.getButtonColors
import com.stefdp.hackatime.utils.SecureStorage
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import androidx.core.net.toUri
import kotlinx.coroutines.coroutineScope
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService

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

    val coroutineScope = rememberCoroutineScope()

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

                var errorMessage by remember { mutableStateOf<String?>(null) }

                var isLoading by remember { mutableStateOf(false) }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text = stringResource(R.string.login_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                Switch(
                    modifier = Modifier.padding(top = 10.dp),
                    checked = shareApiKeyChecked,
                    onCheckedChange = { shareApiKeyChecked = it },
                    label = stringResource(R.string.share_data_with_server_switch_label),
                    description = stringResource(R.string.share_data_with_server_switch_description_login),
                    enabled = !isLoading
                )

                val updateUserStats = LocalUpdateUserStats.current

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val intent = result.data
                    if (intent != null) {
                        val response = AuthorizationResponse.fromIntent(intent)
                        val ex = AuthorizationException.fromIntent(intent)

                        if (response != null) {
                            val authService = AuthorizationService(context)
                            authService.performTokenRequest(response.createTokenExchangeRequest()) { tokenResponse, authException ->
                                if (tokenResponse != null) {
                                    val accessToken = tokenResponse.accessToken

                                    coroutineScope.launch {
                                        val secureStorage = SecureStorage.getInstance(context)
                                        if (accessToken != null) secureStorage.set("access_token", accessToken)

                                        Log.d("Auth", "Access Token: $accessToken")
                                    }
                                } else {
                                    Log.e("Auth", "Token exchange failed", authException)
                                }
                            }
                        } else {
                            Log.e("Auth", "Authorization failed", ex)
                        }
                    }
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val clientId = "jE6VgCwPlOCZKoAHjGGwBASkpHAmVOMpKrp_OYzVKXg"
                            val redirectUri = "hackatime://oauth/callback".toUri()

                            val authorizationEndpoint = "https://hackatime.hackclub.com/oauth/authorize".toUri()
                            val tokenEndpoint = "https://hackatime.hackclub.com/oauth/token".toUri()

                            val serviceConfig = AuthorizationServiceConfiguration(
                                authorizationEndpoint,
                                tokenEndpoint
                            )

                            val authRequest = AuthorizationRequest.Builder(
                                serviceConfig,
                                clientId,
                                ResponseTypeValues.CODE,
                                redirectUri
                            )
                                .setScopes("profile", "read")
                                .build()

                            val authService = AuthorizationService(context)
                            val authIntent = authService.getAuthorizationRequestIntent(authRequest)
                            launcher.launch(authIntent)
                        }
                    },
                    enabled = !isLoading,
                    colors = getButtonColors()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = LocalContentColor.current,
                            )

                            Spacer(
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = stringResource(R.string.login_button),
                            fontWeight = FontWeight.Bold,
                            color = LocalContentColor.current,
                        )
                    }
                }

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