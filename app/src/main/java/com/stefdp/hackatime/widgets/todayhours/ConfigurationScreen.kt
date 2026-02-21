package com.stefdp.hackatime.widgets.todayhours

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
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserTodayData
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme
import com.stefdp.hackatime.utils.SecureStorage
import com.stefdp.hackatime.utils.formatMs
import kotlinx.coroutines.launch
import com.stefdp.hackatime.widgets.todayhours.WidgetContentCompose as TodayHoursWidgetPreview

class TodayHoursWidgetConfigurationActivity : ComponentActivity() {

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

            secureStore.set("todayCodingHours_backgroundOpacity", backgroundOpacity.toString())

            val glanceAppWidgetManager = GlanceAppWidgetManager(applicationContext)
            val glanceId = glanceAppWidgetManager.getGlanceIdBy(appWidgetId)

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[OpacityKey] = backgroundOpacity
            }

            TodayCodingHoursWidget().update(applicationContext, glanceId)

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
    var todayTime by remember { mutableStateOf("1h 10m") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        backgroundOpacity = secureStore.get("todayCodingHours_backgroundOpacity")?.toFloatOrNull() ?: 1f

        var todayData = getCurrentUserTodayData(context)

        todayData.onSuccess {
            todayTime = formatMs(
                ms = it.grandTotal.totalSeconds * 1000,
                limit = 2
            )
        }
    }

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        backgroundOpacity = secureStore.get("todayCodingHours_backgroundOpacity")?.toFloatOrNull() ?: 1f
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            TodayHoursWidgetPreview(
                todayTime = todayTime,
                backgroundOpacity = backgroundOpacity,
                modifier = Modifier.clip(
                    RoundedCornerShape(20.dp)
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

