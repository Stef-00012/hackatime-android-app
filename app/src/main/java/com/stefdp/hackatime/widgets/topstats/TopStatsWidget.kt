package com.stefdp.hackatime.widgets.topstats

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext as GlanceLocalContext
import androidx.glance.LocalSize as GlanceLocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Spacer as GlanceSpacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.Row as GlanceRow
import androidx.glance.layout.Alignment as GlanceAlignment
import androidx.glance.layout.Column as GlanceColumn
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserStatsLast7Days
import com.stefdp.hackatime.ui.theme.DarkWidgetBackground
import com.stefdp.hackatime.ui.theme.DarkWidgetSurface
import androidx.glance.preview.Preview as GlancePreview
import androidx.glance.text.FontWeight as GlanceFontWeight
import com.stefdp.hackatime.ui.theme.HackatimeStatsWidgetTheme
import com.stefdp.hackatime.ui.theme.LightWidgetBackground
import com.stefdp.hackatime.ui.theme.LightWidgetSurface
import com.stefdp.hackatime.utils.getTop
import com.stefdp.hackatime.widgets.CELL_HEIGHT
import com.stefdp.hackatime.widgets.CELL_WIDTH
import com.stefdp.hackatime.widgets.cornerRadius
import com.stefdp.hackatime.widgets.components.Text as GlanceText

val StringOpacityKey = "topStats_backgroundOpacity"
val OpacityKey = floatPreferencesKey(StringOpacityKey)

open class TopStatsWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        var topProject = "Unknown"
        var topLanguage = "Unknown"
        var topEditor = "Unknown"
        var topOperatingSystem = "Unknown"

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

        provideContent {
            val prefs = currentState<Preferences>()
            val backgroundOpacity = prefs[OpacityKey] ?: 1f

            WidgetContent(
                topProject = topProject,
                topLanguage = topLanguage,
                topEditor = topEditor,
                topOperatingSystem = topOperatingSystem,
                backgroundOpacity = backgroundOpacity
            )
        }
    }
}

//@OptIn(ExperimentalGlancePreviewApi::class)
//@GlancePreview(
//    widthDp = CELL_WIDTH * 5,
//    heightDp = CELL_HEIGHT * 2,
//)
@Composable
private fun WidgetContent(
    topProject: String = "Unknown",
    topLanguage: String = "Unknown",
    topEditor: String = "Unknown",
    topOperatingSystem: String = "Unknown",
    backgroundOpacity: Float = 1f
) {
    HackatimeStatsWidgetTheme {
        val context = GlanceLocalContext.current
        val size = GlanceLocalSize.current
        val screenWidthDp = context.resources.configuration.screenWidthDp.dp
//
        val isWideWidget = size.width > (screenWidthDp / 2)
        val isTallWidget = size.height > 115.dp

        val dynamicBackground = ColorProvider(
            day = LightWidgetBackground.copy(alpha = backgroundOpacity),
            night = DarkWidgetBackground.copy(alpha = backgroundOpacity)
        )
        
        val boxOpacity = (backgroundOpacity + (80f / 255f)).coerceAtMost(1f)

        val dynamicBoxBackground = ColorProvider(
            day = LightWidgetSurface.copy(alpha = boxOpacity),
            night = DarkWidgetSurface.copy(alpha = boxOpacity)
        )

        GlanceColumn(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(dynamicBackground)
                .padding(8.dp),
            horizontalAlignment = GlanceAlignment.CenterHorizontally,
            verticalAlignment = GlanceAlignment.CenterVertically
        ) {
            GlanceRow(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight()
            ) {
                GlanceColumn(
                    verticalAlignment = GlanceAlignment.Top,
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .cornerRadius(cornerRadius)
                        .background(dynamicBoxBackground)
                        .padding(8.dp)
                        .defaultWeight()
                ) {
                    GlanceText(
                        text = "Top Project",
                        color = GlanceTheme.colors.primary,
                        fontWeight = GlanceFontWeight.Bold,
                        fontSize = 20.sp
                    )

                    GlanceSpacer(
                        modifier = GlanceModifier.defaultWeight()
                    )

                    GlanceText(
                        text = topProject,
                        fontSize = 20.sp
                    )

                    GlanceSpacer(
                        modifier = GlanceModifier.defaultWeight()
                    )
                }

                if (isWideWidget) {
                    GlanceSpacer(
                        modifier = GlanceModifier.width(16.dp)
                    )

                    GlanceColumn(
                        verticalAlignment = GlanceAlignment.Top,
                        modifier = GlanceModifier
                            .fillMaxHeight()
                            .cornerRadius(cornerRadius)
                            .background(dynamicBoxBackground)
                            .padding(8.dp)
                            .defaultWeight()
                    ) {
                        GlanceText(
                            text = "Top Language",
                            color = GlanceTheme.colors.primary,
                            fontWeight = GlanceFontWeight.Bold,
                            fontSize = 20.sp
                        )

                        GlanceSpacer(
                            modifier = GlanceModifier.defaultWeight()
                        )

                        GlanceText(
                            text = topLanguage,
                            fontSize = 20.sp
                        )

                        GlanceSpacer(
                            modifier = GlanceModifier.defaultWeight()
                        )
                    }
                }
            }

            if (isTallWidget) {
                GlanceSpacer(
                    modifier = GlanceModifier.height(16.dp)
                )

                GlanceRow(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight()
                ) {
                    GlanceColumn(
                        verticalAlignment = GlanceAlignment.Top,
                        modifier = GlanceModifier
                            .fillMaxHeight()
                            .cornerRadius(cornerRadius)
                            .background(dynamicBoxBackground)
                            .padding(8.dp)
                            .defaultWeight()
                    ) {
                        GlanceText(
                            text = "Top Editor",
                            color = GlanceTheme.colors.primary,
                            fontWeight = GlanceFontWeight.Bold,
                            fontSize = 20.sp
                        )

                        GlanceSpacer(
                            modifier = GlanceModifier.defaultWeight()
                        )

                        GlanceText(
                            text = topEditor,
                            fontSize = 20.sp
                        )

                        GlanceSpacer(
                            modifier = GlanceModifier.defaultWeight()
                        )
                    }

                    if (isWideWidget) {
                        GlanceSpacer(
                            modifier = GlanceModifier.width(16.dp)
                        )

                        GlanceColumn(
                            verticalAlignment = GlanceAlignment.Top,
                            modifier = GlanceModifier
                                .fillMaxHeight()
                                .cornerRadius(cornerRadius)
                                .background(dynamicBoxBackground)
                                .padding(8.dp)
                                .defaultWeight()
                        ) {
                            GlanceText(
                                text = "Top OS",
                                color = GlanceTheme.colors.primary,
                                fontWeight = GlanceFontWeight.Bold,
                                fontSize = 20.sp
                            )

                            GlanceSpacer(
                                modifier = GlanceModifier.defaultWeight()
                            )

                            GlanceText(
                                text = topOperatingSystem,
                                fontSize = 20.sp
                            )

                            GlanceSpacer(
                                modifier = GlanceModifier.defaultWeight()
                            )
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun WidgetPreview(
    modifier: Modifier = Modifier,
    topProject: String = "Unknown",
    topLanguage: String = "Unknown",
    topEditor: String = "Unknown",
    topOperatingSystem: String = "Unknown",
    backgroundOpacity: Float = 1f,
    width: Dp = (CELL_WIDTH * 5).dp,
    height: Dp = (CELL_HEIGHT * 2).dp
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
//
    val isWideWidget = width > (screenWidthDp / 2)
    val isTallWidget = height > 115.dp

    val dynamicBackground = MaterialTheme.colorScheme.background.copy(alpha = backgroundOpacity)

    val boxOpacity = (backgroundOpacity + (80f / 255f)).coerceAtMost(1f)

    val dynamicBoxBackground = MaterialTheme.colorScheme.surface.copy(alpha = boxOpacity)

    Column(
        modifier = modifier
            .width((CELL_WIDTH * 5).dp)
            .height((CELL_HEIGHT * 2).dp)
            .background(dynamicBackground)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(dynamicBoxBackground)
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Top Project",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = topProject,
                    fontSize = 20.sp
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )
            }

            if (isWideWidget) {
                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(dynamicBoxBackground)
                        .padding(8.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = "Top Language",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = topLanguage,
                        fontSize = 20.sp
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (isTallWidget) {
            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(dynamicBoxBackground)
                        .padding(8.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = "Top Editor",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = topEditor,
                        fontSize = 20.sp
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isWideWidget) {
                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(cornerRadius))
                            .background(dynamicBoxBackground)
                            .padding(8.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = "Top OS",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = topOperatingSystem,
                            fontSize = 20.sp
                        )

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}