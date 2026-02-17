package com.stefdp.hackatime.screens.settings

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.stefdp.hackatime.DebugWrapper
import com.stefdp.hackatime.LocalUpdateUserStats
import com.stefdp.hackatime.R
import com.stefdp.hackatime.components.Switch
import com.stefdp.hackatime.components.TextInput
import com.stefdp.hackatime.network.backendapi.models.NotificationCategory
import com.stefdp.hackatime.network.backendapi.requests.deleteUser
import com.stefdp.hackatime.network.backendapi.requests.getUser
import com.stefdp.hackatime.network.backendapi.requests.getUserNotificationCategories
import com.stefdp.hackatime.network.backendapi.requests.sendApiKey
import com.stefdp.hackatime.network.backendapi.requests.updateUserNotificationCategories
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.screens.settings.components.Container
import com.stefdp.hackatime.utils.SecureStorage
import com.stefdp.hackatime.utils.hasNotificationsPermission
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavHostController,
    context: Context
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Container(
            modifier = Modifier.padding(
                start = 5.dp,
                end = 5.dp,
                top = 5.dp,
                bottom = 2.5.dp
            )
        ) {
            var apiKey by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
            var shareApikey by rememberSaveable { mutableStateOf(false) }
            var unlockWithBiometrics by rememberSaveable { mutableStateOf(false) }

            var isAPiOnServer by remember { mutableStateOf(false) }

            val updateUserStats = LocalUpdateUserStats.current

            Text(
                text = stringResource(R.string.app_settings_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            LaunchedEffect(Unit) {
                val secureStore = SecureStorage.getInstance(context)

                apiKey = TextFieldValue(secureStore.get("apiKey") ?: "")
                shareApikey = secureStore.get("shareApiKey")?.toBoolean() ?: false
                unlockWithBiometrics = secureStore.get("unlockWithBiometrics")?.toBoolean() ?: false

                isAPiOnServer = getUser(
                    context = context
                )
            }

            TextInput(
                isPassword = true,
                value = apiKey,
                onValueChange = { apiKey = it },
                label = stringResource(R.string.hackatime_api_key_input_label)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Switch(
                checked = shareApikey,
                onCheckedChange = { shareApikey = it },
                label = stringResource(R.string.share_api_key_with_server_switch_label),
                description = stringResource(R.string.share_api_key_with_server_switch_description_settings)
//                description = "If you don't share the API key, you won't be able to receive push notifications or use the goals feature.\nDisabling this toggle will also delete your API key from the server"
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // TODO: implement actual biometric authentication
            Switch(
                checked = unlockWithBiometrics,
                onCheckedChange = { unlockWithBiometrics = it },
                label = stringResource(R.string.unlock_with_biometrics_switch_label),
                description = stringResource(R.string.unlock_with_biometrics_switch_description)
            )

            val coroutineScope = rememberCoroutineScope()

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        val secureStore = SecureStorage.getInstance(context)

                        secureStore.set("apiKey", apiKey.text)
                        secureStore.set("shareApiKey", shareApikey.toString())
                        secureStore.set("unlockWithBiometrics", unlockWithBiometrics.toString())

                        val newUser = updateUserStats()

                        if (newUser == null) {
                            navController.navigate(LoginScreen) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }

                        if (!shareApikey && isAPiOnServer) {
                            val res = deleteUser(context)

                            if (!res) {
                                secureStore.set("shareApiKey", "true")

                                Toast.makeText(
                                    context,
                                    context.getString(R.string.delete_api_key_fail_message),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (shareApikey && !isAPiOnServer) {
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

                        Toast.makeText(
                            context,
                            context.getString(R.string.settings_saved_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.save),
                    contentDescription = stringResource(R.string.save_settings_content_description)
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = stringResource(R.string.save_button),
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(
                    color = MaterialTheme.colorScheme.primary,
                    width = 2.dp
                ),
                onClick = {
                    coroutineScope.launch {
                        val secureStore = SecureStorage.getInstance(context)

                        secureStore.set("apiKey", "")

                        updateUserStats()

                        navController.navigate(LoginScreen) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.logout),
                    contentDescription = stringResource(R.string.logout_content_description)
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = stringResource(R.string.logout_button),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        var hasNotificationsPermissions by rememberSaveable { mutableStateOf(false) }

        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasNotificationsPermissions = isGranted

            Toast.makeText(
                context,
                if (isGranted) context.getString(R.string.notifications_permission_granted)
                else context.getString(R.string.notifications_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }

        Container(
            modifier = Modifier.padding(
                vertical = 2.5.dp,
                horizontal = 5.dp
            )
        ) {
            var motivationalNotificationsEnabled by rememberSaveable { mutableStateOf(false) }
            var goalsNotificationsEnabled by rememberSaveable { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                hasNotificationsPermissions = hasNotificationsPermission(context)

                val notificationCategories = getUserNotificationCategories(context)

                motivationalNotificationsEnabled = notificationCategories[NotificationCategory.MOTIVATIONAL_QUOTES] == true
                goalsNotificationsEnabled = notificationCategories[NotificationCategory.GOALS] == true
            }

            Text(
                text = stringResource(R.string.notifications_settings_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Switch(
                enabled = hasNotificationsPermissions,
                checked = motivationalNotificationsEnabled,
                onCheckedChange = { motivationalNotificationsEnabled = it },
                label = stringResource(R.string.motivational_notifications_switch_label),
                description = stringResource(
                    R.string.motivational_notifications_switch_description,
                    if (!hasNotificationsPermissions)
                        stringResource(R.string.switch_description_notifications_permission_not_granted)
                    else ""
                )
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )


            Switch(
                enabled = hasNotificationsPermissions,
                checked = goalsNotificationsEnabled,
                onCheckedChange = { goalsNotificationsEnabled = it },
                label = stringResource(R.string.goals_notifications_switch_label),
                description = stringResource(
                    R.string.goals_notifications_switch_description,
                    if (!hasNotificationsPermissions)
                        stringResource(R.string.switch_description_notifications_permission_not_granted)
                    else ""
                )
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            if (!hasNotificationsPermissions) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(
                        color = MaterialTheme.colorScheme.primary,
                        width = 2.dp
                    ),
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.notifications_permission_always_granted),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.notifications),
                        contentDescription = stringResource(R.string.update_notifications_permission_content_description)
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = stringResource(R.string.update_notifications_permission_button),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            val coroutineScope = rememberCoroutineScope()

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = hasNotificationsPermissions,
                colors = ButtonDefaults.buttonColors().copy(
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                ),
                onClick = {
                    coroutineScope.launch {
                        val newNotificationCategories = updateUserNotificationCategories(
                            context = context,
                            categories = mapOf(
                                NotificationCategory.MOTIVATIONAL_QUOTES to motivationalNotificationsEnabled,
                                NotificationCategory.GOALS to goalsNotificationsEnabled
                            )
                        )

                        motivationalNotificationsEnabled = newNotificationCategories[NotificationCategory.MOTIVATIONAL_QUOTES] == true
                        goalsNotificationsEnabled = newNotificationCategories[NotificationCategory.GOALS] == true

                        Toast.makeText(
                            context,
                            context.getString(R.string.notifications_preferences_saved),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.save),
                    contentDescription = stringResource(R.string.save_notifications_preferences_content_description)
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = stringResource(R.string.save_button),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            val notificationsPermissionStatus = stringResource(
                if (hasNotificationsPermissions)
                    R.string.notifications_permission_status_granted
                else
                    R.string.notifications_permission_status_denied
            )

            val notificationsPermissionsStatusString = AnnotatedString.fromHtml(
                stringResource(
                    R.string.notifications_permission_status,
                    notificationsPermissionStatus
                )
            )

            Text(
                text = notificationsPermissionsStatusString
            )
        }

        DebugWrapper {
            Container(
                modifier = Modifier.padding(
                    start = 5.dp,
                    end = 5.dp,
                    top = 2.5.dp,
                    bottom = 5.dp
                )
            ) {
                Text(
                    text = "Developer Settings",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            Toast.makeText(
                                context,
                                "Notifications Permission is automatically granted on this version of Android",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.notifications),
                        contentDescription = "Update Notifications Permission"
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = "Update Notifications Permission",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}