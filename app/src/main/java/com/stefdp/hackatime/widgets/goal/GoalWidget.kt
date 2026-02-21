package com.stefdp.hackatime.widgets.goal

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.stefdp.hackatime.ui.theme.HackatimeStatsWidgetTheme
import com.stefdp.hackatime.utils.SecureStorage
import com.stefdp.hackatime.widgets.CELL_HEIGHT
import com.stefdp.hackatime.widgets.CELL_WIDTH

// TODO: create actual widget & its config screen

class GoalWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val secureStore = SecureStorage.getInstance(context)

        provideContent {
            HackatimeStatsWidgetTheme {
                WidgetContent()
            }
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(
    widthDp = CELL_WIDTH * 2,
    heightDp = CELL_HEIGHT,
)
@Composable
private  fun WidgetContent() {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Today's Hours",
            style = TextStyle(
                color = GlanceTheme.colors.primary
            )
        )

        Text(
            text = "4h 2m"
        )
    }
}