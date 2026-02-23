package com.stefdp.hackatime.widgets.goal

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image as GlanceImage
import androidx.glance.ImageProvider as GlanceImageProvider
import androidx.glance.LocalContext as GlanceLocalContext
import androidx.glance.LocalSize as GlanceLocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator as GlanceLinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.ContentScale as GlanceContentScale
import androidx.glance.layout.Box as GlanceBox
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
import androidx.glance.text.TextAlign as GlanceTextAlign
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.backendapi.models.Goal
import com.stefdp.hackatime.network.backendapi.requests.getUserGoals
import com.stefdp.hackatime.ui.theme.DarkOnSurfaceVariant
import com.stefdp.hackatime.ui.theme.DarkWidgetBackground
import com.stefdp.hackatime.ui.theme.DarkWidgetSurface
import androidx.glance.preview.Preview as GlancePreview
import androidx.glance.text.FontWeight as GlanceFontWeight
import com.stefdp.hackatime.ui.theme.HackatimeStatsWidgetTheme
import com.stefdp.hackatime.ui.theme.LightOnSurfaceVariant
import com.stefdp.hackatime.ui.theme.LightWidgetBackground
import com.stefdp.hackatime.ui.theme.LightWidgetSurface
import com.stefdp.hackatime.utils.formatMs
import com.stefdp.hackatime.utils.getDatesBetween
import com.stefdp.hackatime.utils.isSameDate
import com.stefdp.hackatime.widgets.CELL_HEIGHT
import com.stefdp.hackatime.widgets.CELL_WIDTH
import com.stefdp.hackatime.widgets.components.CircularProgressBarOptions
import com.stefdp.hackatime.widgets.components.GlanceVerticalProgressBar
import com.stefdp.hackatime.widgets.components.VerticalProgressBar
import com.stefdp.hackatime.widgets.components.createCircularProgressBar
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import com.stefdp.hackatime.widgets.components.Text as GlanceText

const val StringOpacityKey = "goal_backgroundOpacity"
val OpacityKey = floatPreferencesKey(StringOpacityKey)

const val StringProgressTextBoldKey = "goal_progressTextBold"
val ProgressTextBoldKey = booleanPreferencesKey(StringProgressTextBoldKey)

open class GoalWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val startDate = Instant
            .now()
            .minus(Duration.ofDays(7))
            .atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endDate = Instant
            .now()
            .atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_LOCAL_DATE)

        val goals = getUserGoals(
            context = context,
            startDate = startDate,
            endDate = endDate
        ).reversed()

        provideContent {
            val prefs = currentState<Preferences>()
            val backgroundOpacity = prefs[OpacityKey] ?: 1f
            val progressTextBold = prefs[ProgressTextBoldKey] ?: true

            WidgetContent(
                goals = goals,
                progressTextBold = progressTextBold,
                backgroundOpacity = backgroundOpacity
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@GlancePreview(
    widthDp = CELL_WIDTH * 2,
    heightDp = CELL_HEIGHT * 2,
)
@Composable
private fun WidgetContent(
    goals: List<Goal> = emptyList(),
    progressTextBold: Boolean = true,
    backgroundOpacity: Float = 1f
) {
    HackatimeStatsWidgetTheme {
        val context = GlanceLocalContext.current
        val size = GlanceLocalSize.current

        val cellsWidth = (size.width / CELL_WIDTH).value.roundToInt()
        val cellsHeight = (size.height / CELL_HEIGHT).value.roundToInt()

        val dynamicBackground = ColorProvider(
            day = LightWidgetBackground.copy(alpha = backgroundOpacity),
            night = DarkWidgetBackground.copy(alpha = backgroundOpacity)
        )

        val boxOpacity = (backgroundOpacity + (80f / 255f)).coerceAtMost(1f)

        val dynamicBoxBackground = ColorProvider(
            day = LightWidgetSurface.copy(alpha = boxOpacity),
            night = DarkWidgetSurface.copy(alpha = boxOpacity)
        )

        val padding = 8.dp

        GlanceColumn(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(dynamicBackground)
                .padding(padding),
            horizontalAlignment = GlanceAlignment.CenterHorizontally,
            verticalAlignment = GlanceAlignment.CenterVertically
        ) {
            if (goals.isEmpty()) {
                GlanceText(
                    "No Goal Data",
                    fontSize = 20.sp,
                    fontWeight = GlanceFontWeight.Bold,
                    maxLines = 3,
                    textAlign = GlanceTextAlign.Center
                )

                return@GlanceColumn
            }

            val today = Instant.now()

            val startDate = Instant
                .now()
                .minus(Duration.ofDays(7))
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
            val endDate = today
                .atZone(ZoneOffset.UTC)
                .toLocalDate()

            val todayGoal = goals.find { goal -> isSameDate(
                isoString = goal.date,
                instant = today
            ) } ?: goals.last()
            
            val todayGoalPercentage = if (todayGoal.goal > 0) {
                (todayGoal.achieved / todayGoal.goal)
            } else 0.0

            if (cellsWidth <= 3 && cellsHeight == 1) {
                // simple time + linear progress bar
                GlanceSpacer(
                    modifier = GlanceModifier.defaultWeight()
                )

                GlanceText(
                    text = formatMs(
                        ms = todayGoal.achieved * 1000,
                        limit = 2
                    ),
                    modifier = GlanceModifier,
                    fontSize = 32.sp,
                    fontWeight = GlanceFontWeight.Bold
                )

                GlanceSpacer(
                    modifier = GlanceModifier.defaultWeight()
                )

                GlanceRow(
                    modifier = GlanceModifier.padding(
                        start = padding,
                        end = padding,
                        bottom = padding
                    )
                ) {
                    GlanceLinearProgressIndicator(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .cornerRadius(999.dp),
                        progress = todayGoalPercentage.toFloat(),
                        color = GlanceTheme.colors.primary,
                        backgroundColor = GlanceTheme.colors.onSurfaceVariant
                    )
                }

                return@GlanceColumn
            }

            if (cellsWidth > 3 && cellsHeight == 1) {
                // timer icon + time + (target + linear progress bar)
                GlanceRow(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    val containerSize = size.height - (padding * 4)

                    GlanceColumn(
                        modifier = GlanceModifier.fillMaxHeight(),
                        verticalAlignment = GlanceAlignment.CenterVertically
                    ) {
                        GlanceBox(
                            modifier = GlanceModifier
                                .height(containerSize)
                                .width(containerSize)
                                .padding(padding * 2)
                                .cornerRadius(999.dp)
                                .background(dynamicBoxBackground),
                            contentAlignment = GlanceAlignment.CenterStart
                        ) {
                            val timerIcon = GlanceImageProvider(R.drawable.timer)
                            GlanceImage(
                                provider = timerIcon,
                                contentDescription = "Timer Icon",
                                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                                modifier = GlanceModifier.fillMaxSize(),
                                contentScale = GlanceContentScale.FillBounds
                            )
                        }
                    }

                    GlanceSpacer(
                        modifier = GlanceModifier.defaultWeight()
                    )

                    GlanceColumn(
                        modifier = GlanceModifier.width((size.width.value.toDouble() / 1.5).dp).fillMaxHeight()
                    ) {
                        GlanceSpacer(
                            modifier = GlanceModifier.defaultWeight()
                        )

                        GlanceRow(
                            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 10.dp)
                        ) {
                            GlanceSpacer(
                                modifier = GlanceModifier.defaultWeight()
                            )

                            GlanceText(
                                text = "Target: " + formatMs(
                                    ms = todayGoal.goal * 1000,
                                    limit = 2
                                ),
                                fontSize = 25.sp,
                                fontWeight = GlanceFontWeight.Bold
                            )

                            GlanceSpacer(
                                modifier = GlanceModifier.defaultWeight()
                            )
                        }

                        GlanceRow(
                            modifier = GlanceModifier.padding(
                                end = padding,
                                bottom = padding
                            )
                        ) {
                            GlanceLinearProgressIndicator(
                                modifier = GlanceModifier.fillMaxWidth().cornerRadius(999.dp),
                                progress = todayGoalPercentage.toFloat(),
                                color = GlanceTheme.colors.primary,
                                backgroundColor = GlanceTheme.colors.onSurfaceVariant,
                            )
                        }
                    }
                }

                return@GlanceColumn
            }

            if (cellsWidth <= 2 && cellsHeight >= 2) {
                // title + time + target + linear progress bar
                GlanceColumn(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    GlanceText(
                        text = "Hours",
                        fontSize = 35.sp,
                        fontWeight = GlanceFontWeight.Bold,
                        color = GlanceTheme.colors.primary
                    )

                    GlanceSpacer(
                        modifier = GlanceModifier.defaultWeight()
                    )

                    GlanceText(
                        text = formatMs(
                            ms = todayGoal.achieved * 1000,
                            limit = 2
                        ),
                        fontSize = 30.sp,
                        fontWeight = GlanceFontWeight.Bold
                    )

                    GlanceText(
                        text = "/" + formatMs(
                            ms = todayGoal.goal * 1000,
                            limit = 2
                        ),
                        fontSize = 20.sp,
                        color = GlanceTheme.colors.onSurfaceVariant
                    )

                    GlanceSpacer(
                        modifier = GlanceModifier.height(10.dp)
                    )

                    GlanceRow(
                        modifier = GlanceModifier.padding(
                            end = padding,
                            start = padding,
                            bottom = padding
                        )
                    ) {
                        GlanceLinearProgressIndicator(
                            modifier = GlanceModifier.fillMaxWidth().cornerRadius(999.dp),
                            progress = todayGoalPercentage.toFloat(),
                            color = GlanceTheme.colors.primary,
                            backgroundColor = GlanceTheme.colors.onSurfaceVariant
                        )
                    }
                }
                
                return@GlanceColumn
            }

            if (cellsWidth == 3 && cellsHeight >= 2) {
                // title w/ time & target + circular progress bar w/ percentage in middle
                GlanceColumn(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    GlanceRow(
                        modifier = GlanceModifier.fillMaxWidth(),
                    ) {
                        GlanceText(
                            text = "Hours",
                            fontSize = 25.sp,
                            fontWeight = GlanceFontWeight.Bold,
                            color = GlanceTheme.colors.primary
                        )

                        GlanceSpacer(
                            modifier = GlanceModifier.defaultWeight()
                        )

                        GlanceText(
                            text = formatMs(
                                ms = todayGoal.achieved * 1000,
                                limit = 2
                            ),
                            fontSize = 20.sp,
                            fontWeight = GlanceFontWeight.Bold
                        )

                        GlanceText(
                            text = "/" + formatMs(
                                ms = todayGoal.goal * 1000,
                                limit = 2
                            ),
                            fontSize = 15.sp,
                            color = GlanceTheme.colors.onSurfaceVariant
                        )
                    }

                    val circularProgressBar = createCircularProgressBar(
                        context = context,
                        progress = todayGoalPercentage.toFloat()  * 100f,
                        options = CircularProgressBarOptions(
                            size = 100,
                            barWidth = 12f,
                            backgroundColor = GlanceTheme.colors.onSurfaceVariant.getColor(context),
                            progressColor = GlanceTheme.colors.primary.getColor(context),
                            showPercentage = true,
                            fontSize = 20f,
                            textColor = GlanceTheme.colors.onSurfaceVariant.getColor(context),
                            textBold = progressTextBold
                        )
                    )

                    val circularProgressBarProvider = GlanceImageProvider(circularProgressBar)

                    GlanceImage(
                        provider = circularProgressBarProvider,
                        contentDescription = "Progress",
                        modifier = GlanceModifier.fillMaxWidth().defaultWeight().padding(vertical = 8.dp),
                        contentScale = GlanceContentScale.Fit
                    )
                }

                return@GlanceColumn
            }

            if (cellsWidth > 3 && cellsHeight >= 2) {
                val padding = 10.dp
                val mainProgressBarHeight = 30.dp
                val mainProgressBarPadding = 10.dp
                val textSpace = 5.dp
                val verticalProgressBarHeight = size.height - (padding * 2) - mainProgressBarHeight - (mainProgressBarPadding * 2) - textSpace
                
                val rowHeight = size.height - mainProgressBarHeight - (mainProgressBarPadding * 2)
                // title + last 7 days vertical linear progress bar + time + target + today linear progress bar
                GlanceRow(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(rowHeight)
                        .padding(horizontal = padding),
                    verticalAlignment = GlanceAlignment.Top,
                ) {
                    GlanceColumn(
                        modifier = GlanceModifier.fillMaxHeight()
                    ) {
                        GlanceText(
                            text = "Hours",
                            fontSize = 25.sp,
                            fontWeight = GlanceFontWeight.Bold,
                            color = GlanceTheme.colors.primary
                        )

                        GlanceSpacer(
                            modifier = GlanceModifier.defaultWeight()
                        )

                        GlanceText(
                            text = formatMs(
                                ms = todayGoal.achieved * 1000,
                                limit = 2
                            ),
                            fontSize = 20.sp,
                            fontWeight = GlanceFontWeight.Bold,
                        )

                        GlanceText(
                            text = "/" + formatMs(
                                ms = todayGoal.goal * 1000,
                                limit = 2
                            ),
                            fontSize = 15.sp,
                            fontWeight = GlanceFontWeight.Bold,
                            color = GlanceTheme.colors.onSurfaceVariant
                        )

                        GlanceSpacer(
                            modifier = GlanceModifier.height(textSpace)
                        )
                    }

                    GlanceSpacer(
                        modifier = GlanceModifier.defaultWeight()
                    )

                    val goalDates = getDatesBetween(
                        date1 = startDate,
                        date2 = endDate
                    )

                    goalDates.forEach { date ->
                        val goal = goals.find { isSameDate(
                            isoString = it.date,
                            instant = date
                                .atStartOfDay()
                                .toInstant(ZoneOffset.UTC))
                        }

                        val dayNum = date.dayOfMonth
                        val dayOfWeek = date.dayOfWeek.value
                        val isSunday = dayOfWeek == 7

                        val isToday = isSameDate(
                            isoString = date
                                .atStartOfDay()
                                .toInstant(ZoneOffset.UTC)
                                .toString(),
                            instant = today
                        )

                        if (goal == null) {
                            GlanceColumn(
                                modifier = GlanceModifier.padding(horizontal = 6.dp)
                            ) {
                                GlanceVerticalProgressBar(
                                    progress = 0f,
                                    progressColor = GlanceTheme.colors.primary,
                                    trackColor = ColorProvider(
                                        day = LightOnSurfaceVariant.copy(alpha = 0.6f),
                                        night = DarkOnSurfaceVariant.copy(alpha = 0.6f)
                                    ),
                                    totalHeight = verticalProgressBarHeight,
                                )

                                GlanceText(
                                    text = dayNum.toString(),
                                    color = if (isSunday) GlanceTheme.colors.primary else GlanceTheme.colors.onBackground,
                                    fontWeight = if (isToday) GlanceFontWeight.Bold else GlanceFontWeight.Normal
                                )
                            }

                            return@forEach
                        }

                        GlanceColumn(
                            modifier = GlanceModifier.padding(horizontal = 6.dp)
                        ) {
                            GlanceVerticalProgressBar(
                                progress = (goal.achieved / goal.goal).toFloat(),
                                progressColor = GlanceTheme.colors.primary,
                                trackColor = GlanceTheme.colors.onSurfaceVariant,
                                totalHeight = verticalProgressBarHeight,
                            )

                            GlanceText(
                                text = dayNum.toString(),
                                color = if (isSunday) GlanceTheme.colors.primary else GlanceTheme.colors.onBackground,
                                fontWeight = if (isToday) GlanceFontWeight.Bold else GlanceFontWeight.Normal
                            )
                        }
                    }
                }

                GlanceLinearProgressIndicator(
                    modifier = GlanceModifier.fillMaxWidth().height(mainProgressBarHeight).cornerRadius(999.dp),
                    progress = todayGoalPercentage.toFloat(),
                    color = GlanceTheme.colors.primary,
                    backgroundColor = GlanceTheme.colors.onSurfaceVariant
                )

                return@GlanceColumn
            }
        }
    }
}

@Preview(
    widthDp = CELL_WIDTH * 5,
    heightDp = CELL_HEIGHT * 2,)
@Composable
fun WidgetPreview(
    modifier: Modifier = Modifier,
    goals: List<Goal> = emptyList(),
    progressTextBold: Boolean = true,
    backgroundOpacity: Float = 1f,
    width: Dp = (CELL_WIDTH * 5).dp,
    height: Dp = (CELL_HEIGHT * 2).dp
) {
    val context = LocalContext.current

    val cellsWidth = (width / CELL_WIDTH).value.roundToInt()
    val cellsHeight = (height / CELL_HEIGHT).value.roundToInt()

    val padding = 8.dp

    Column(
        modifier = modifier
            .height(height)
            .width(width)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = backgroundOpacity))
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (goals.isEmpty()) {
            Text(
                "No Goal Data",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                textAlign = TextAlign.Center
            )

            return@Column
        }

        val today = Instant.now()

        val startDate = Instant
            .now()
            .minus(Duration.ofDays(7))
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
        val endDate = today
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        val todayGoal = goals.find { goal -> isSameDate(
            isoString = goal.date,
            instant = today
        ) } ?: goals.last()

        val todayGoalPercentage = if (todayGoal.goal > 0) {
            (todayGoal.achieved / todayGoal.goal)
        } else 0.0

        if (cellsWidth <= 3 && cellsHeight == 1) {
            // simple time + linear progress bar
            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = formatMs(
                    ms = todayGoal.achieved * 1000,
                    limit = 2
                ),
                modifier = Modifier,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier.padding(
                    start = padding,
                    end = padding,
                    bottom = padding
                )
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(999.dp))
                        .height(20.dp),
                    progress = { todayGoalPercentage.toFloat() },
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    strokeCap = StrokeCap.Square,
                    gapSize = 0.dp,
                    drawStopIndicator = {}
                )
            }

            return@Column
        }

        if (cellsWidth > 3 && cellsHeight == 1) {
            // timer icon + time + (target + linear progress bar)
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                val containerSize = height - (padding * 4)

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .height(containerSize)
                            .width(containerSize)
                            .padding(padding * 2)
                            .clip(RoundedCornerShape(999.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = backgroundOpacity)),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        val timerIcon = painterResource(R.drawable.timer)
                        Icon(
                            painter = timerIcon,
                            contentDescription = "Timer Icon",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Column(
                    modifier = Modifier.width((width.value.toDouble() / 1.5).dp).fillMaxHeight()
                ) {
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                    ) {
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "Target: " + formatMs(
                                ms = todayGoal.goal * 1000,
                                limit = 2
                            ),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.padding(
                            end = padding,
                            bottom = padding
                        )
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp)).height(20.dp),
                            progress = { todayGoalPercentage.toFloat() },
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            strokeCap = StrokeCap.Square,
                            gapSize = 0.dp,
                            drawStopIndicator = {}
                        )
                    }
                }
            }

            return@Column
        }

        if (cellsWidth <= 2 && cellsHeight >= 2) {
            // title + time + target + linear progress bar
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Hours",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formatMs(
                        ms = todayGoal.achieved * 1000,
                        limit = 2
                    ),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "/" + formatMs(
                        ms = todayGoal.goal * 1000,
                        limit = 2
                    ),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row(
                    modifier = Modifier.padding(
                        end = padding,
                        start = padding,
                        bottom = padding
                    )
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp)).height(20.dp),
                        progress = { todayGoalPercentage.toFloat() },
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        strokeCap = StrokeCap.Square,
                        gapSize = 0.dp,
                        drawStopIndicator = {}
                     )
                }
            }

            return@Column
        }

        if (cellsWidth == 3 && cellsHeight >= 2) {
            // title w/ time & target + circular progress bar w/ percentage in middle
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Hours",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = formatMs(
                            ms = todayGoal.achieved * 1000,
                            limit = 2
                        ),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "/" + formatMs(
                            ms = todayGoal.goal * 1000,
                            limit = 2
                        ),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val circularProgressBar = createCircularProgressBar(
                    context = context,
                    progress = todayGoalPercentage.toFloat()  * 100f,
                    options = CircularProgressBarOptions(
                        size = 100,
                        barWidth = 12f,
                        backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        progressColor = MaterialTheme.colorScheme.primary,
                        showPercentage = true,
                        fontSize = 20f,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        textBold = progressTextBold
                    )
                )



                Image(
                    bitmap = circularProgressBar.asImageBitmap(),
                    contentDescription = "Progress",
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 8.dp),
                    contentScale = ContentScale.Fit
                )
            }

            return@Column
        }

        if (cellsWidth > 3 && cellsHeight >= 2) {
            val padding = 10.dp
            val mainProgressBarHeight = 30.dp
            val mainProgressBarPadding = 10.dp
            val textSpace = 5.dp
            val verticalProgressBarHeight = height - (padding * 2) - mainProgressBarHeight - (mainProgressBarPadding * 2) - textSpace

            val rowHeight = height - mainProgressBarHeight - (mainProgressBarPadding * 2)
            // title + last 7 days vertical linear progress bar + time + target + today linear progress bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight)
                    .padding(horizontal = padding),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = "Hours",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = formatMs(
                            ms = todayGoal.achieved * 1000,
                            limit = 2
                        ),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = "/" + formatMs(
                            ms = todayGoal.goal * 1000,
                            limit = 2
                        ),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.height(textSpace)
                    )
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                val goalDates = getDatesBetween(
                    date1 = startDate,
                    date2 = endDate
                )

                goalDates.forEach { date ->
                    val goal = goals.find { isSameDate(
                        isoString = it.date,
                        instant = date
                            .atStartOfDay()
                            .toInstant(ZoneOffset.UTC))
                    }

                    val dayNum = date.dayOfMonth
                    val dayOfWeek = date.dayOfWeek.value
                    val isSunday = dayOfWeek == 7

                    val isToday = isSameDate(
                        isoString = date
                            .atStartOfDay()
                            .toInstant(ZoneOffset.UTC)
                            .toString(),
                        instant = today
                    )

                    if (goal == null) {
                        Column(
                            modifier = Modifier.padding(horizontal = 6.dp)
                        ) {
                            VerticalProgressBar(
                                progress = 0f,
                                progressColor = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                totalHeight = verticalProgressBarHeight,
                            )

                            Text(
                                text = dayNum.toString(),
                                color = if (isSunday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        return@forEach
                    }

                    Column(
                        modifier = Modifier.padding(horizontal = 6.dp)
                    ) {
                        VerticalProgressBar(
                            progress = (goal.achieved / goal.goal).toFloat(),
                            progressColor = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            totalHeight = verticalProgressBarHeight,
                        )

                        Text(
                            text = dayNum.toString(),
                            color = if (isSunday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(mainProgressBarHeight).clip(RoundedCornerShape(999.dp)),
                progress = { todayGoalPercentage.toFloat() },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurfaceVariant,
                strokeCap = StrokeCap.Square,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )

            return@Column
        }
    }
}