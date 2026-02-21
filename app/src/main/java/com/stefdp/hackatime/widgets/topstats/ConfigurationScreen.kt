package com.stefdp.hackatime.widgets.topstats

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import com.stefdp.hackatime.components.Slider
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserStatsLast7Days
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserTodayData
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme
import com.stefdp.hackatime.utils.SecureStorage
import com.stefdp.hackatime.utils.formatMs
import com.stefdp.hackatime.utils.getTop
import com.stefdp.hackatime.widgets.cornerRadius
import kotlinx.coroutines.launch

class TopStatsWidgetConfigurationActivity : ComponentActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val resultValue = Intent().putExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            appWidgetId
        )

        setResult(RESULT_CANCELED, resultValue)

        setContent {
            HackatimeStatsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                        color = Color.Transparent
                    ) {
                        WidgetConfigScreen(
                            context = applicationContext,
                            onSaveConfig = { backgroundOpacity ->
                                saveWidgetConfiguration(
                                    context = applicationContext,
                                    backgroundOpacity = backgroundOpacity
                                )
                            },
                            finish = { finish() }
                        )
                    }
                }
            }
        }
    }

    private fun saveWidgetConfiguration(
        context: Context,
        backgroundOpacity: Float
    ) {
        lifecycleScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            secureStore.set(StringOpacityKey, backgroundOpacity.toString())

            val glanceAppWidgetManager = GlanceAppWidgetManager(applicationContext)
            val glanceId = glanceAppWidgetManager.getGlanceIdBy(appWidgetId)

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[OpacityKey] = backgroundOpacity
            }

            TopStatsWidget().update(applicationContext, glanceId)

            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)

            finish()
        }
    }
}

@Composable
fun WidgetConfigScreen(
    context: Context,
    onSaveConfig: (Float) -> Unit,
    finish: () -> Unit
) {
    var backgroundOpacity by remember { mutableFloatStateOf(1f) }
    var topProject by remember { mutableStateOf("Hackatime Stats") }
    var topLanguage by remember { mutableStateOf("Kotlin") }
    var topEditor by remember { mutableStateOf("Android Studio") }
    var topOperatingSystem by remember { mutableStateOf("Linux") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        backgroundOpacity = secureStore.get(StringOpacityKey)?.toFloatOrNull() ?: 1f

        val last7DaysStatsRes = getCurrentUserStatsLast7Days(
            context = context,
            features = listOf(
                Feature.PROJECTS,
                Feature.LANGUAGES,
                Feature.EDITORS,
                Feature.OPERATING_SYSTEMS
            )
        )

        last7DaysStatsRes.onSuccess {
            topProject = getTop(it.projects)?.name ?: "Unknown"
            topLanguage = getTop(it.languages)?.name ?: "Unknown"
            topEditor = getTop(it.editors)?.name ?: "Unknown"
            topOperatingSystem = getTop(it.operatingSystems)?.name ?: "Unknown"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            WidgetPreview(
                topProject = topProject,
                topLanguage = topLanguage,
                topEditor = topEditor,
                topOperatingSystem = topOperatingSystem,
                backgroundOpacity = backgroundOpacity,
                modifier = Modifier.clip(
                    RoundedCornerShape(cornerRadius)
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(
                    topStart = 40.dp,
                    topEnd = 40.dp
                ))
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            Text(
                text = "Widget Configuration",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Slider(
                value = backgroundOpacity,
                onValueChange = { backgroundOpacity = it },
                label = "Opacity:",
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Row {
                Button(
                    onClick = { finish() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(20.dp)
                )

                Button(
                    onClick = {
                        onSaveConfig(backgroundOpacity)
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Save",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE, backgroundColor = 0xFF3F008B,
)
@Composable
fun Preview() {
    val context = LocalContext.current

    HackatimeStatsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                color = Color.Transparent
            ) {
                WidgetConfigScreen(
                    context = context,
                    onSaveConfig = { backgroundOpacity ->
                        Log.d("WidgetConfigScreen", "Saving config with backgroundOpacity: $backgroundOpacity")
//                        saveWidgetConfiguration(
//                            context = applicationContext,
//                            backgroundOpacity = backgroundOpacity
//                        )
                    },
                    finish = { Log.d("WidgetConfigScreen", "Finish called") }
                )
            }
        }
    }
}

