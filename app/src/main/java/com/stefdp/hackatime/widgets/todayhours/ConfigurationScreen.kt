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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import com.stefdp.hackatime.R
import com.stefdp.hackatime.components.Button
import com.stefdp.hackatime.components.Slider
import com.stefdp.hackatime.network.hackatimeapi.requests.getWakatimeUserTodayData
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme
import com.stefdp.hackatime.utils.SecureStorage
import com.stefdp.hackatime.utils.formatMs
import com.stefdp.hackatime.widgets.cornerRadius
import kotlinx.coroutines.launch

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
                            appWidgetId = appWidgetId,
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

            secureStore.set("${StringOpacityKey}_$appWidgetId", backgroundOpacity.toString())

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
    appWidgetId: Int,
    onSaveConfig: (Float) -> Unit,
    finish: () -> Unit
) {
    var backgroundOpacity by remember { mutableFloatStateOf(1f) }
    var todayTime by remember { mutableStateOf("1h 10m") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        backgroundOpacity = secureStore.get("${StringOpacityKey}_$appWidgetId")?.toFloatOrNull() ?: 1f

        val todayData = getWakatimeUserTodayData(context)

        todayData.onSuccess {
            todayTime = formatMs(
                context = context,
                ms = it.grandTotal.totalSeconds * 1000,
                limit = 2
            )
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
                todayTime = todayTime,
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
                text = stringResource(R.string.widget_configuration),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Slider(
                value = backgroundOpacity,
                onValueChange = { backgroundOpacity = it },
                label = stringResource(R.string.widget_opacity),
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
                        text = stringResource(R.string.cancel_button),
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
                        text = stringResource(R.string.save_button),
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
                    appWidgetId = 1,
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

