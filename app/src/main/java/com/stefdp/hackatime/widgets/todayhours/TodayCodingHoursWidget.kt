package com.stefdp.hackatime.widgets.todayhours

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.fillMaxSize
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.hackatimeapi.requests.getWakatimeUserTodayData
import com.stefdp.hackatime.ui.theme.DarkWidgetBackground
import com.stefdp.hackatime.ui.theme.HackatimeStatsWidgetTheme
import com.stefdp.hackatime.ui.theme.LightWidgetBackground
import com.stefdp.hackatime.utils.formatMs
import com.stefdp.hackatime.widgets.CELL_HEIGHT
import com.stefdp.hackatime.widgets.CELL_WIDTH
import androidx.glance.layout.Alignment as GlanceAlignment
import androidx.glance.layout.Column as GlanceColumn
import androidx.glance.preview.Preview as GlancePreview
import androidx.glance.text.FontWeight as GlanceFontWeight
import com.stefdp.hackatime.widgets.components.Text as GlanceText

const val StringOpacityKey = "todayCodingHours_backgroundOpacity"
val OpacityKey = floatPreferencesKey(StringOpacityKey)

open class TodayCodingHoursWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        var todayTime = context.getString(R.string.unknown)
        val todayDataRes = getWakatimeUserTodayData(context)

        todayDataRes.onSuccess {
            todayTime = formatMs(
                context = context,
                ms = it.grandTotal.totalSeconds * 1000,
                limit = 2
            )
        }

        provideContent {
            val prefs = currentState<Preferences>()
            val backgroundOpacity = prefs[OpacityKey] ?: 1f

            WidgetContent(
                context = context,
                todayTime = todayTime,
                backgroundOpacity = backgroundOpacity
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@GlancePreview(
    widthDp = CELL_WIDTH * 2,
    heightDp = CELL_HEIGHT,
)
@Composable
private fun WidgetContent(
    context: Context,
    todayTime: String = context.getString(R.string.unknown),
    backgroundOpacity: Float = 1f
) {
    HackatimeStatsWidgetTheme {
        val dynamicBackground = ColorProvider(
            day = LightWidgetBackground.copy(alpha = backgroundOpacity),
            night = DarkWidgetBackground.copy(alpha = backgroundOpacity)
        )

        GlanceColumn(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(dynamicBackground),
            horizontalAlignment = GlanceAlignment.CenterHorizontally,
            verticalAlignment = GlanceAlignment.CenterVertically
        ) {
            GlanceText(
                text = context.getString(R.string.todays_hours),
                color = GlanceTheme.colors.primary,
                fontWeight = GlanceFontWeight.Bold,
                fontSize = 20.sp
            )

            GlanceText(
                text = todayTime,
                fontSize = 32.sp
            )
        }
    }
}

@Composable
fun WidgetPreview(
    modifier: Modifier = Modifier,
    todayTime: String = stringResource(R.string.unknown),
    backgroundOpacity: Float = 1f
) {
    Column(
        modifier = modifier
            .width((CELL_WIDTH * 2).dp)
            .height((CELL_HEIGHT).dp)
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = backgroundOpacity)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.todays_hours),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Text(
            text = todayTime,
            fontSize = 32.sp
        )
    }
}