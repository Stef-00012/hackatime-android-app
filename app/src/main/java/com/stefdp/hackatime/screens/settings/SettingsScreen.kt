package com.stefdp.hackatime.screens.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.messaging.FirebaseMessaging
import com.stefdp.hackatime.DebugWrapper
import com.stefdp.hackatime.LocalUpdateUserStats
import com.stefdp.hackatime.R
import com.stefdp.hackatime.components.Button
import com.stefdp.hackatime.components.Notification
import com.stefdp.hackatime.components.OutlinedButton
import com.stefdp.hackatime.components.Switch
import com.stefdp.hackatime.network.backendapi.requests.sendPushNotificationToken
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.screens.settings.components.Container
import com.stefdp.hackatime.utils.SecureStorage
import com.stefdp.hackatime.utils.createBiometricPrompt
import com.stefdp.hackatime.utils.createPromptInfo
import com.stefdp.hackatime.utils.getBiometricStatus
import com.stefdp.hackatime.utils.hasNotificationsPermission
import com.stefdp.hackatime.utils.promptBiometricAuthentication
import com.stefdp.hackatime.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.verticalScrollWithScrollbar(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.setBiometricAuthenticationStatus(getBiometricStatus(context))
                    viewModel.setHasNotificationsPermissions(hasNotificationsPermission(context))
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(state.hasNotificationsPermissions) {
            if (!state.hasNotificationsPermissions) return@LaunchedEffect

            FirebaseMessaging.getInstance()
                .register()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.e("FCM", "FCM registration failed", task.exception)
                    }
                }
        }

        val enrolBiometricAuthenticationLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {}

        Container(
            modifier = Modifier.padding(
                start = 5.dp,
                end = 5.dp,
                top = 5.dp,
            )
        ) {
            val updateUserStats = LocalUpdateUserStats.current

            Text(
                text = stringResource(R.string.app_settings_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            LaunchedEffect(Unit) {
                viewModel.init(context)
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Switch(
                checked = state.shareApikey,
                onCheckedChange = {
                    viewModel.setShareApiKey(it)
                },
                label = stringResource(R.string.share_data_with_server_switch_label),
                description = stringResource(R.string.share_data_with_server_switch_description_settings),
                enabled = !state.isLoading
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Switch(
                checked = state.unlockWithBiometrics,
                enabled = !state.isLoading && (
                    state.biometricAuthenticationStatus == BiometricManager.BIOMETRIC_SUCCESS || (
                        state.biometricAuthenticationStatus == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                    )
                ),
                onCheckedChange = { checked ->
                    val biometricPrompt = createBiometricPrompt(
                        activity = activity,
                        onSuccess = {
                            coroutineScope.launch {
                                val secureStore = SecureStorage.getInstance(context)

                                viewModel.setUnlockWithBiometrics(checked)

                                secureStore.set("unlockWithBiometrics", state.unlockWithBiometrics.toString())
                            }
                        },
                        onError = { _, _ ->
                            Toast.makeText(
                                context,
                                context.getString(R.string.biometric_authentication_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                    )

                    val biometricPromptInfo = createPromptInfo(context)

                    promptBiometricAuthentication(
                        activity = activity,
                        prompt = biometricPrompt,
                        promptInfo = biometricPromptInfo,
                        onBiometricNotEnrolledError = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                    putExtra(
                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                                    )
                                }

                                enrolBiometricAuthenticationLauncher.launch(enrollIntent)
                            }
                        }
                    )
                },
                label = stringResource(R.string.unlock_with_biometrics_switch_label),
                description = AnnotatedString.fromHtml(
                    stringResource(
                        R.string.unlock_with_biometrics_switch_description,
                        if (state.biometricAuthenticationStatus == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                            stringResource(R.string.unlock_with_biometrics_switch_description_enroll_warning)
                        else ""
                    )
                )
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.saveSettings(
                        context = context,
                        updateUserStats = updateUserStats,
                        onUserError = {
                            navController.navigate(LoginScreen) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        },
                        onError = { error ->
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = stringResource(R.string.settings_saved_message),
                                )
                            }
                        }
                    )
                },
                enabled = !state.isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    AnimatedContent(
                        targetState = state.isLoading,
                        transitionSpec = {
                            (
                                fadeIn() + slideInVertically { height -> height }
                            ) togetherWith (
                                fadeOut() + slideOutVertically { height -> -height }
                            )
                        },
                        label = "UpdateButtonAnimation"
                    ) { isLoading ->
                        Row {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = LocalContentColor.current
                                )

                                Spacer(
                                    modifier = Modifier.width(16.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.save),
                                    contentDescription = stringResource(R.string.save_settings_content_description)
                                )

                                Spacer(
                                    modifier = Modifier.width(5.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.save_button),
                        fontWeight = FontWeight.Bold,
                        color = LocalContentColor.current
                    )
                }
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.logout(
                        context = context,
                        updateUserStats = updateUserStats,
                        onError = { error ->
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        onLogout = {
                            navController.navigate(LoginScreen) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    )
                },
                enabled = !state.isLoading
            ) {
                Icon(
                    painter = painterResource(R.drawable.logout),
                    contentDescription = stringResource(R.string.logout_content_description),
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = stringResource(R.string.logout_button),
                    fontWeight = FontWeight.Bold,
                    color = LocalContentColor.current
                )
            }
        }

        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            viewModel.setHasNotificationsPermissions(isGranted)

            Toast.makeText(
                context,
                if (isGranted) context.getString(R.string.notifications_permission_granted)
                else context.getString(R.string.notifications_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }

        Container(
            modifier = Modifier.padding(
                horizontal = 5.dp
            )
        ) {
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
                enabled = !state.isLoading && state.hasNotificationsPermissions,
                checked = state.motivationalNotificationsEnabled,
                onCheckedChange = {
                    viewModel.setMotivationalNotificationsEnabled(it)
                },
                label = stringResource(R.string.motivational_notifications_switch_label),
                description = stringResource(
                    R.string.motivational_notifications_switch_description,
                    if (!state.hasNotificationsPermissions)
                        stringResource(R.string.switch_description_notifications_permission_not_granted)
                    else ""
                ),
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )


            Switch(
                enabled = !state.isLoading && state.hasNotificationsPermissions,
                checked = state.goalsNotificationsEnabled,
                onCheckedChange = {
                    viewModel.setGoalsNotificationsEnabled(it)
                },
                label = stringResource(R.string.goals_notifications_switch_label),
                description = stringResource(
                    R.string.goals_notifications_switch_description,
                    if (!state.hasNotificationsPermissions)
                        stringResource(R.string.switch_description_notifications_permission_not_granted)
                    else ""
                )
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            if (!state.hasNotificationsPermissions) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
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
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.notifications),
                        contentDescription = stringResource(R.string.update_notifications_permission_content_description),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = stringResource(R.string.update_notifications_permission_button),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.hasNotificationsPermissions,
                colors = ButtonDefaults.buttonColors().copy(
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                ),
                onClick = {
                    viewModel.saveNotificationPreferences(
                        context = context,
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = stringResource(R.string.notifications_preferences_saved),
                                )
                            }
                        }
                    )
                }
            ) {
                AnimatedContent(
                    targetState = state.isLoading,
                    transitionSpec = {
                        (
                                fadeIn() + slideInVertically { height -> height }
                                ) togetherWith (
                                fadeOut() + slideOutVertically { height -> -height }
                                )
                    },
                    label = "UpdateButtonAnimation"
                ) { isLoading ->
                    Row {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = LocalContentColor.current
                            )

                            Spacer(
                                modifier = Modifier.width(16.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.save),
                                contentDescription = stringResource(R.string.save_notifications_preferences_content_description)
                            )

                            Spacer(
                                modifier = Modifier.width(5.dp)
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.save_button),
                    fontWeight = FontWeight.Bold,
                    color = LocalContentColor.current
                )
            }

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            val notificationsPermissionStatus = stringResource(
                if (state.hasNotificationsPermissions)
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

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val biometricPrompt = createBiometricPrompt(
                            activity = activity,
                            onSuccess = {
                                coroutineScope.launch {
                                    Toast.makeText(
                                        context,
                                        "Biometric Authentication Succeeded",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onError = { _, _ ->
                                Toast.makeText(
                                    context,
                                    "Biometric Authentication Failed (onError)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFailed = {
                                Toast.makeText(
                                    context,
                                    "Biometric Authentication Failed (onFailed)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                        val biometricPromptInfo = createPromptInfo(context)

                        promptBiometricAuthentication(
                            activity = activity,
                            prompt = biometricPrompt,
                            promptInfo = biometricPromptInfo,
                            onBiometricNotEnrolledError = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                        putExtra(
                                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                                        )
                                    }

                                    enrolBiometricAuthenticationLauncher.launch(enrollIntent)
                                }
                            }
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.fingerprint),
                        contentDescription = "Request Biometric Authentication"
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = "Request Biometric Authentication",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}