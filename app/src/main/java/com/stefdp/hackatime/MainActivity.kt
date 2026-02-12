package com.stefdp.hackatime

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stefdp.hackatime.components.Header
import com.stefdp.hackatime.components.NavBar
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStats
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserStats
import com.stefdp.hackatime.screens.*
import com.stefdp.hackatime.screens.goals.GoalsScreen
import com.stefdp.hackatime.screens.home.HomeScreen
import com.stefdp.hackatime.screens.login.LoginScreen
import com.stefdp.hackatime.screens.projects.ProjectsScreen
import com.stefdp.hackatime.screens.settings.SettingsScreen
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme

val LocalLoggedUser = compositionLocalOf<UserStats?> { null }
val LocalUpdateUserStats = compositionLocalOf<suspend () -> Unit> { {} }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HackatimeStatsTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                var userStats by remember {
                    mutableStateOf<UserStats?>(null)
                }

                suspend fun updateUserStats() {
                    val tag = "MainActivity[updateUserStats]"

                    Log.d(tag, "Checking if user is already logged in...")

                    val userStatsRes = getCurrentUserStats(
                        context = context,
                        features = listOf(
                            Feature.PROJECTS,
                            Feature.LANGUAGES,
                            Feature.OPERATING_SYSTEMS,
                            Feature.MACHINES,
                            Feature.EDITORS
                        )
                    )

                    userStatsRes
                        .onSuccess { userStatsData ->
                            Log.d(tag, "User is logged in as ${userStatsData.username}")

                           userStats = userStatsData

                            return@updateUserStats
                        }
                        .onFailure {
                            Log.d(tag, "User is not logged in")

                            userStats = null

                            return@updateUserStats
                        }

                    return
                }

                // NOTE: This is just a test for a sidebar, i'll probably use navbar instead of this cuz it looks better
//                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    updateUserStats()
                }

                CompositionLocalProvider(
                    LocalLoggedUser provides userStats,
                    LocalUpdateUserStats provides ::updateUserStats
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            Header(
                                navController = navController
                                // NOTE: This is just a test for a sidebar, i'll probably use navbar instead of this cuz it looks better
//                            onMenuClick = {
//                                scope.launch {
//                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
//                                }
//                            }
                            )
                        },
                        bottomBar = {
                            NavBar(navController = navController)
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier.padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            AppNavigation(
                                navController = navController,
                                context = context
                            )
                        }

                        // NOTE: This is just a test for a sidebar, i'll probably use navbar instead of this cuz it looks better
//                    ModalNavigationDrawer(
//                        modifier = Modifier.padding(innerPadding),
//                        drawerState = drawerState,
//                        drawerContent = {
//                            Sidebar(
//                                onItemClick = { screen ->
//                                    scope.launch { drawerState.close() }
//                                    navController.navigate(screen)
//                                }
//                            )
//                        }
//                    ) {
//                        AppNavigation(navController = navController)
//                    }
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    context: Context
) {
    NavHost(
        navController = navController,
        startDestination = LoginScreen,
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
        composable<LoginScreen> {
            LoginScreen(
                navController = navController,
                context = context
            )
        }

        composable<HomeScreen> {
            HomeScreen(
                navController = navController,
                context = context
            )
        }

        composable<SettingsScreen> {
            SettingsScreen(
                navController = navController,
                context = context
            )
        }

        composable<ProjectsScreen> {
            ProjectsScreen(
                navController = navController,
                context = context
            )
        }

        composable<GoalsScreen> {
            GoalsScreen(
                navController = navController,
                context = context
            )
        }
    }
}

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            HackatimeStatsTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Text(
//                        text = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//
////                    Button(
////                        onClick = {
////                            val l = listOf(Feature.OPERATING_SYSTEMS, Feature.CATEGORIES, Feature.MACHINES)
////                            Log.d("test", l.joinToString(","))
////                        }
////                    ) {
////                        Text("Test log")
////                    }
//
//                    val scrollState = rememberScrollState()
//
//                    // tests tryna see how to make HTTP requests
//                    Column(
//                        modifier = Modifier
//                            .padding(innerPadding)
//                            .verticalScroll(scrollState)
//
//                    ) {
//                        Text(
//                            text = "Android",
//                        )
//
//                        var programs by remember { mutableStateOf("") }
//                        var programsCount by remember { mutableStateOf(0) }
//
//                        var programs1 by remember { mutableStateOf("") }
//                        var programsCount1 by remember { mutableStateOf(0) }
//
//                        val context = LocalContext.current
//
//                        LaunchedEffect(Unit) {
//                            val res = getYSWSPrograms()
//                            var secureStore = SecureStorage.getInstance(context)
//                            secureStore.set("apiKey", "TEST")
//                            Log.d("TEST", "API Key set to: ${secureStore.get("apiKey")}")
//
//                            res.onSuccess {
//                                val programsString = it.joinToString("\n- ")
//
////                                Log.d("Todo", "Programs:\n- $programsString")
//                                programs = programsString
//
//                                programsCount = it.size
//                            }.onFailure {
//                                Log.e("TEST", "Failed to fetch programs: ${it.message}")
//                            }
//                        }
//
//                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                val token = task.result
//                                Log.d("FCM", "Token: $token")
//                            } else {
//                                Log.e("FCM", "Failed to fetch token", task.exception)
//                            }
//                        }
//
//                        if (programs.isNotEmpty()) {
//                            Text("Boot programs:\n- $programs")
//                        }
//
//                        Text("Boot Programs Count: $programsCount")
//
//                        if (programs1.isNotEmpty()) {
//                            Text("Button programs:\n- $programs1")
//                        }
//
//                        Text("Button Programs Count: $programsCount1")
//
//                        Button(
//                            onClick = {
//                                lifecycleScope.launch {
//                                    val secureStorage = SecureStorage.getInstance(context)
//
//                                    val apiKey = secureStorage.get("apiKey")
//
//                                    if (apiKey != null) secureStorage.del("apiKey")
//                                    else secureStorage.set("apiKey", "TEST123")
//
//                                    val res = getYSWSPrograms()
//
//                                     res.onSuccess {
//                                        val programsString = it.joinToString("\n- ")
//
////                                Log.d("Todo", "Programs:\n- $programsString")
//                                        programs1 = programsString
//
//                                        programsCount1 = it.size
//                                    }.onFailure {
//                                        Log.e("TEST", "Failed to fetch programs: ${it.message}")
//                                    }
//                                }
//                            }
//                        ) {
//                            Text("Test API")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}