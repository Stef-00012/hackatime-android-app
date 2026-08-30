package com.stefdp.hackatime

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.Feature
import com.google.firebase.messaging.FirebaseMessaging
import com.stefdp.hackatime.components.Header
import com.stefdp.hackatime.components.NavBar
import com.stefdp.hackatime.network.backendapi.models.NotificationCategory
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetWakatimeUserResponse
import com.stefdp.hackatime.screens.*
import com.stefdp.hackatime.screens.biometricauth.BiometricAuthScreen
import com.stefdp.hackatime.screens.goals.GoalsScreen
import com.stefdp.hackatime.screens.home.HomeScreen
import com.stefdp.hackatime.screens.loading.LoadingScreen
import com.stefdp.hackatime.screens.login.LoginScreen
import com.stefdp.hackatime.screens.projects.ProjectsScreen
import com.stefdp.hackatime.screens.settings.SettingsScreen
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme
import com.stefdp.hackatime.utils.NetworkMonitor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.onSuccess
import kotlin.time.Duration.Companion.milliseconds

val LocalLoggedUser = compositionLocalOf<GetWakatimeUserResponse.Data?> { null }
val LocalUpdateUserStats = compositionLocalOf<suspend (context: Context) -> Result<GetWakatimeUserResponse.Data>> {
    {
        Result.failure(
            Exception("Placeholder")
        )
    }
}

const val HACKATIME_OAUTH_CLIENT_ID = "jE6VgCwPlOCZKoAHjGGwBASkpHAmVOMpKrp_OYzVKXg"

class MainActivity : FragmentActivity() {
    private var isAppReady by mutableStateOf(false)

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            !isAppReady
        }

        lifecycleScope.launch {
            delay(100.milliseconds)
            isAppReady = true
        }

        val context = applicationContext

        enableEdgeToEdge()

        setContent {
            HackatimeStatsTheme {
                val activity = this@MainActivity

                val navController = rememberNavController()

                val state by viewModel.state.collectAsState()

                val networkMonitor = NetworkMonitor(context)

                val isConnected by networkMonitor.isConnected.collectAsState(initial = true)

                LaunchedEffect(isConnected) {
                    if (isConnected) {
                        viewModel.updateLoggedUser(context)
                    }
                }

                LaunchedEffect(Unit) {
                    FirebaseMessaging.getInstance()
                        .register()
                        .addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.e("FCM", "FCM registration failed", task.exception)
                            }
                        }
                }

                CompositionLocalProvider(
                    LocalLoggedUser provides state.loggedUser,
                    LocalUpdateUserStats provides viewModel::updateLoggedUser
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            Header(
                                navController = navController
                            )
                        },
                        bottomBar = {
                            NavBar(
                                navController = navController
                            )
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier.padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            AppNavigation(
                                navController = navController,
                                context = context,
                                activity = activity
                            )
                        }
                    }
                }

                if (!isConnected) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier.padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.wifi_off),
                                    contentDescription = stringResource(R.string.no_internet_connection_content_description),
                                    modifier = Modifier.size(50.dp)
                                )

                                Spacer(
                                    modifier = Modifier.height(20.dp)
                                )

                                Text(
                                    text = stringResource(R.string.no_internet_connection_title),
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val goalsChannel = NotificationChannel(
            NotificationCategory.GOALS.toString(),
            getString(R.string.notification_channel_goals_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = getString(R.string.notification_channel_goals_description)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 250, 250, 250)
        }

        val motivationalNotificationsChannel = NotificationChannel(
            NotificationCategory.MOTIVATIONAL_QUOTES.toString(),
            getString(R.string.notification_channel_motivational_notifications_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = getString(R.string.notification_channel_motivational_notifications_description)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 250, 250, 250)
        }


        val manager = getSystemService(NotificationManager::class.java)

        manager.createNotificationChannels(
            listOf(
                goalsChannel,
                motivationalNotificationsChannel
            )
        )
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    NavHost(
        navController = navController,
        startDestination = LoadingScreen,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400))
        },
        exitTransition = {
            fadeOut(tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400))
        }
    ) {
        composable<LoadingScreen> {
            LoadingScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<LoginScreen> {
            LoginScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<BiometricAuthScreen> {
            BiometricAuthScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<HomeScreen> {
            HomeScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<SettingsScreen> {
            SettingsScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<ProjectsScreen> {
            ProjectsScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<GoalsScreen> {
            GoalsScreen(
                navController = navController,
                context = context,
                activity = activity
            )
        }

        composable<LeaderboardScreen> {
            Text("WIP")
        }
    }
}